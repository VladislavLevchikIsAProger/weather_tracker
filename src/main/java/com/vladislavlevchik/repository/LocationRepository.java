package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.exception.DuplicateLocationException;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

public class LocationRepository extends BaseRepository {

    public Location save(Location entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.save(entity);

            session.getTransaction().commit();

            return entity;
        } catch (ConstraintViolationException e) {
            throw new DuplicateLocationException();
        }
    }

    public void delete(Location location) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "DELETE FROM Location l WHERE l.user = :user AND l.lat = :lat AND l.lon =:lon";
            Query query = session.createQuery(hql);
            query.setParameter("user", location.getUser());
            query.setParameter("lat", location.getLat());
            query.setParameter("lon", location.getLon());

            query.executeUpdate();

            session.getTransaction().commit();
        }
    }

}
