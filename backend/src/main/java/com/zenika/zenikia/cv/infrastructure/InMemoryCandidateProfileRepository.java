package com.zenika.zenikia.cv.infrastructure;

import com.zenika.zenikia.cv.application.CandidateProfileRepository;
import com.zenika.zenikia.cv.domain.CandidateProfile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link CandidateProfileRepository} for the POC. Replace with a
 * PostgreSQL-backed adapter later — the port contract in {@code
 * application} does not need to change.
 */
@Component
class InMemoryCandidateProfileRepository implements CandidateProfileRepository {

    private final Map<String, CandidateProfile> store = new ConcurrentHashMap<>();

    @Override
    public CandidateProfile save(CandidateProfile profile) {
        store.put(profile.id(), profile);
        return profile;
    }

    @Override
    public Optional<CandidateProfile> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
