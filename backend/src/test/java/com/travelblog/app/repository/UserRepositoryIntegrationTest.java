package com.travelblog.app.repository;

import com.travelblog.app.integration.AbstractIntegrationTest;
import com.travelblog.app.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and find user by email in real MySQL container")
    void shouldSaveAndFindByEmail() {
        User user = new User();
        user.setUsername("integration_user");
        user.setEmail("integration@example.com");
        user.setPassword("hashed_pass");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("integration@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("integration_user");
    }
}