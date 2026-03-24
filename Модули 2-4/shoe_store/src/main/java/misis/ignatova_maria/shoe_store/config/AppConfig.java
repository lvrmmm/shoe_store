package misis.ignatova_maria.shoe_store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "misis.ignatova_maria.shoe_store.repository")
@EnableTransactionManagement
public class AppConfig {
}
