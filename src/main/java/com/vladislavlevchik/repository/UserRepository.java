package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.exception.authentication.UserAlreadyExistException;
import com.vladislavlevchik.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;

public class UserRepository extends BaseRepository<User> {
    public UserRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public UserRepository() {
        super();
    }

//    public User save(User entity) {
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//
//            session.save(entity);
//
//            session.getTransaction().commit();
//
//            return entity;
//        } catch (ConstraintViolationException e) {
//            throw new UserAlreadyExistException();
//        }
//    }

    public Optional<User> findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
        }
    }

    @Override
    protected void handleConstraintViolationException(ConstraintViolationException e) {
        throw new UserAlreadyExistException();
    }

}
