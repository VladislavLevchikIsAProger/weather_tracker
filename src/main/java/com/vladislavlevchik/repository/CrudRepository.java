package com.vladislavlevchik.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<E, K> {

    List<E> findAll();

    Optional<E> findById(K id);

    E save(E entity);

    void update(E entity);

    void delete(K id);

}
