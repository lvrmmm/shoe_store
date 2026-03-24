package misis.ignatova_maria.shoe_store.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public Optional<User> authenticate(String login, String password) {
		log.debug("Authenticating user: {}", login);
		return userRepository.findByLoginAndPassword(login, password);
	}

	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		log.debug("Getting all users");
		return userRepository.findAll();
	}
}
