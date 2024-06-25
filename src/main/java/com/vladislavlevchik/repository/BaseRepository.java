package com.vladislavlevchik.repository;

import com.vladislavlevchik.utils.HibernateUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<E, K> implements CrudRepository<E, K> {

    private final Class<E> clazz;
    protected final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<E> findAll() {
        return null;
    }

    @Override
    public Optional<E> findById(K id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(clazz, id));
        }
    }

    @Override
    public E save(E entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.save(entity);

            session.getTransaction().commit();

            return entity;
        }
    }

    @Override
    public void update(E entity) {

    }

    @Override
    public void delete(K id) {

    }
}
