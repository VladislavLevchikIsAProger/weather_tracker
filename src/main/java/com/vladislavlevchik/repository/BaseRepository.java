package com.vladislavlevchik.repository;

import com.vladislavlevchik.utils.HibernateUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

@RequiredArgsConstructor
public abstract class BaseRepository {

    protected final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

}
