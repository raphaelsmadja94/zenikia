package com.zenika.zenikia.interview.application;

import com.zenika.zenikia.interview.domain.InterviewSession;

import java.util.Optional;

/**
 * Port for persisting {@link InterviewSession}. In-memory implementation
 * for the POC; designed to be swapped for a PostgreSQL-backed adapter
 * without touching the application layer.
 */
public interface InterviewSessionRepository {

    InterviewSession save(InterviewSession session);

    Optional<InterviewSession> findById(String id);
}
