package misis.ignatova_maria.shoe_store.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import misis.ignatova_maria.shoe_store.entity.*;
import misis.ignatova_maria.shoe_store.repository.OrderItemRepository;
import misis.ignatova_maria.shoe_store.repository.OrderRepository;
import misis.ignatova_maria.shoe_store.repository.OrderStatusRepository;
import misis.ignatova_maria.shoe_store.repository.PickupPointRepository;
import misis.ignatova_maria.shoe_store.repository.UserRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderStatusRepository orderStatusRepository;

	@Autowired
	private PickupPointRepository pickupPointRepository;

	@Autowired
	private UserRepository userRepository;

	public List<Order> getAllOrders() {
		return orderRepository.findAllWithDetails();
	}

	public List<OrderStatus> getAllStatuses() {
		return orderStatusRepository.findAll();
	}

	public List<PickupPoint> getAllPickupPoints() {
		return pickupPointRepository.findAllOrdered();
	}

	public OrderStatus getDefaultStatus() {
		return orderStatusRepository.findByName("Новый")
				.orElseGet(() -> orderStatusRepository.findById(1).orElse(null));
	}

	@Transactional
	public Order createOrder(Order order) {
		if (order.getOrderDate() == null) {
			order.setOrderDate(LocalDate.now());
		}
		if (order.getStatus() == null) {
			order.setStatus(getDefaultStatus());
		}

		order.setId(null);

		if (order.getUser() != null && order.getUser().getId() != null) {
			User existingUser = userRepository.findById(order.getUser().getId())
					.orElseThrow(() -> new RuntimeException("Пользователь не найден"));
			order.setUser(existingUser);
		}

		Order savedOrder = orderRepository.save(order);

		if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
			for (OrderItem item : order.getOrderItems()) {
				item.setOrder(savedOrder);
				orderItemRepository.save(item);
			}
		}

		return savedOrder;
	}

	@Transactional
	public Order updateOrder(Order order) {
		Order existingOrder = orderRepository.findById(order.getId())
				.orElseThrow(() -> new RuntimeException("Заказ не найден"));

		existingOrder.setOrderDate(order.getOrderDate());
		existingOrder.setDeliveryDate(order.getDeliveryDate());
		existingOrder.setPickupPoint(order.getPickupPoint());
		existingOrder.setStatus(order.getStatus());
		existingOrder.setPickupCode(order.getPickupCode());

		if (order.getUser() != null && order.getUser().getId() != null) {
			User existingUser = userRepository.findById(order.getUser().getId())
					.orElseThrow(() -> new RuntimeException("Пользователь не найден"));
			existingOrder.setUser(existingUser);
		}

		if (existingOrder.getOrderItems() != null && !existingOrder.getOrderItems().isEmpty()) {
			for (OrderItem item : existingOrder.getOrderItems()) {
				orderItemRepository.delete(item);
			}
			existingOrder.getOrderItems().clear();
			orderItemRepository.flush();
		}

		if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
			for (OrderItem item : order.getOrderItems()) {
				OrderItem newItem = new OrderItem();
				newItem.setOrder(existingOrder);
				newItem.setProduct(item.getProduct());
				newItem.setQuantity(item.getQuantity());
				existingOrder.getOrderItems().add(newItem);
			}
			orderItemRepository.saveAll(existingOrder.getOrderItems());
		}

		return orderRepository.save(existingOrder);
	}

	@Transactional
	public boolean deleteOrder(Integer orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			if (order.get().getOrderItems() != null && !order.get().getOrderItems().isEmpty()) {
				orderItemRepository.deleteAll(order.get().getOrderItems());
			}
			orderRepository.delete(order.get());
			return true;
		}
		return false;
	}

	@Transactional
	public void updatePickupCode(Integer orderId, String pickupCode) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Заказ не найден"));
		order.setPickupCode(pickupCode);
		orderRepository.save(order);
	}

	public String generatePickupCode(Order order) {
		return String.format("%d-%04d", order.getId(), System.currentTimeMillis() % 10000);
	}

	public Order getOrderWithItems(Integer orderId) {
		Order order = orderRepository.findById(orderId).orElse(null);
		if (order != null) {
			Hibernate.initialize(order.getOrderItems());
		}
		return order;
	}
}
