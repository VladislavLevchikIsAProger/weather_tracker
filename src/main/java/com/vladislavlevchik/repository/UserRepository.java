package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.User;
import org.hibernate.Session;

import java.util.Optional;

public class UserRepository extends BaseRepository<User, Long> {
    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
        }
    }


}
