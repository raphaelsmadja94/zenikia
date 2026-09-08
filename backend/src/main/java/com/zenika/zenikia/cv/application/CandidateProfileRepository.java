package com.zenika.zenikia.cv.application;

import com.zenika.zenikia.cv.domain.CandidateProfile;

import java.util.Optional;

/**
 * Port for persisting {@link CandidateProfile}. In-memory implementation for
 * the POC; designed to be swapped for a PostgreSQL-backed adapter without
 * touching the application layer.
 */
public interface CandidateProfileRepository {

    CandidateProfile save(CandidateProfile profile);

    Optional<CandidateProfile> findById(String id);
}
