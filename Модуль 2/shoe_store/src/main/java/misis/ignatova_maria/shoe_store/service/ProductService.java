package misis.ignatova_maria.shoe_store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import misis.ignatova_maria.shoe_store.entity.Product;
import misis.ignatova_maria.shoe_store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAllWithDetails();
    }
}