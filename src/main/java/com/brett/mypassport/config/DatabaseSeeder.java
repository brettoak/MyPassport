package com.brett.mypassport.config;

import com.brett.mypassport.entity.User;
import com.brett.mypassport.repository.UserRepository;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final Faker faker;

    public DatabaseSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.faker = new Faker();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!userRepository.existsByUsername("user@example.com")) {
            System.out.println("Seeding database with 5 users...");
            User user = new User();
            user.setUsername("user@example.com");
            user.setPassword("password"); // Plain text as requested
            user.setEmail("user@example.com");
            userRepository.save(user);
            for (int i = 0; i < 2; i++) {
                user = new User();
                user.setUsername(faker.name().username());
                user.setPassword("password"); // Plain text as requested
                user.setEmail(faker.internet().emailAddress());
                userRepository.save(user);
            }
            System.out.println("Database seeded successfully.");
        } else {
            System.out.println("Database already seeded. Skipping...");
        }
    }
}
