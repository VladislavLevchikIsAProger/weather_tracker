package com.vladislavlevchik.repository;

import com.vladislavlevchik.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

public abstract class BaseRepository<T> {

    protected final SessionFactory sessionFactory;

    public BaseRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public BaseRepository() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public T save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.save(entity);

            session.getTransaction().commit();

            return entity;
        } catch (ConstraintViolationException e) {
            handleConstraintViolationException(e);
            return null; // or throw a custom exception
        }
    }

    protected abstract void handleConstraintViolationException(ConstraintViolationException e);
}