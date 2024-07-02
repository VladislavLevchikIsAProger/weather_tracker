package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Location;
import com.vladislavlevchik.entity.User;
import com.vladislavlevchik.exception.DuplicateLocationException;
import com.vladislavlevchik.exception.UserAlreadyExistException;
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

}
