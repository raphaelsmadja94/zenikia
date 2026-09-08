package com.zenika.zenikia.interview.infrastructure;

import com.zenika.zenikia.interview.application.InterviewSessionRepository;
import com.zenika.zenikia.interview.domain.InterviewSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link InterviewSessionRepository} for the POC. Replace with a
 * PostgreSQL-backed adapter later — the port contract does not need to change.
 */
@Component
class InMemoryInterviewSessionRepository implements InterviewSessionRepository {

    private final Map<String, InterviewSession> store = new ConcurrentHashMap<>();

    @Override
    public InterviewSession save(InterviewSession session) {
        store.put(session.id(), session);
        return session;
    }

    @Override
    public Optional<InterviewSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
