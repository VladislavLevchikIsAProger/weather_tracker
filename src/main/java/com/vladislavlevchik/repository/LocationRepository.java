package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.exception.DuplicateLocationException;
import com.vladislavlevchik.utils.HibernateUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

public class LocationRepository extends BaseRepository<Location>{
    public LocationRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public LocationRepository() {
        super();
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

    @Override
    protected void handleConstraintViolationException(ConstraintViolationException e) {
        throw new DuplicateLocationException();
    }

}
