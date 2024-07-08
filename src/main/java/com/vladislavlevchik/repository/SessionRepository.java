package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Session;
import com.vladislavlevchik.utils.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;
import java.util.UUID;

public class SessionRepository extends BaseRepository<Session> {

    public SessionRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public SessionRepository() {
        super();
    }

    public Optional<Session> findById(UUID id) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Session.class, id));
        }
    }

//    public Session save(Session entity) {
//        try (org.hibernate.Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//
//            session.save(entity);
//
//            session.getTransaction().commit();
//
//            return entity;
//        }
//    }

    public void deleteById(UUID id) {

        try (org.hibernate.Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "delete from Session where id = :UUID";
            Query query = session.createQuery(hql);
            query.setParameter("UUID", id);

            query.executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    protected void handleConstraintViolationException(ConstraintViolationException e) {

    }
}
