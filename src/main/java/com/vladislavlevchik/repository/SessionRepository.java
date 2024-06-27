package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Session;
import jakarta.persistence.Query;

import java.util.Optional;
import java.util.UUID;

public class SessionRepository extends BaseRepository{

    public Optional<Session> findById(UUID id) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Session.class, id));
        }
    }

    public Session save(Session entity) {
        try (org.hibernate.Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.save(entity);

            session.getTransaction().commit();

            return entity;
        }
    }

    public void deleteById(UUID id) {

        try(org.hibernate.Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "delete from Session where id = :UUID";
            Query query = session.createQuery(hql);
            query.setParameter("UUID", id);

            query.executeUpdate();

            session.getTransaction().commit();
        }
    }
}
