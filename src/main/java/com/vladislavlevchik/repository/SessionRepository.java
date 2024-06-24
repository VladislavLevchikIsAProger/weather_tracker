package com.vladislavlevchik.repository;

import com.vladislavlevchik.entity.Session;

import java.util.UUID;

public class SessionRepository extends BaseRepository<Session, UUID>{
    public SessionRepository() {
        super(Session.class);
    }
}
