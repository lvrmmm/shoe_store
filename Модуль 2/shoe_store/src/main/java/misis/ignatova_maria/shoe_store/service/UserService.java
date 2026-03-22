package misis.ignatova_maria.shoe_store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import misis.ignatova_maria.shoe_store.entity.User;
import misis.ignatova_maria.shoe_store.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

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
}