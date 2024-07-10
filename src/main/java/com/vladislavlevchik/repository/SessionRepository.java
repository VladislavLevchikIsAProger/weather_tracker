package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Session;
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
