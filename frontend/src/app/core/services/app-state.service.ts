import { Injectable, signal } from '@angular/core';
import { CandidateProfileResponse } from '../models/candidate-profile.model';

const LAST_PROFILE_ID_KEY = 'zenikia.lastCandidateProfileId';

/**
 * Small cross-page bridge for the CV → profile → persona → interview flow.
 * The spec's `/profile` route carries no id, so the profile analyzed in
 * `/upload` is handed over here rather than through the URL. The profile
 * id is mirrored to sessionStorage so a page refresh on `/profile` doesn't
 * lose it (the profile itself is then re-fetched from the backend).
 */
@Injectable({ providedIn: 'root' })
export class AppStateService {
  readonly candidateProfile = signal<CandidateProfileResponse | null>(null);

  setCandidateProfile(profile: CandidateProfileResponse): void {
    this.candidateProfile.set(profile);
    try {
      sessionStorage.setItem(LAST_PROFILE_ID_KEY, profile.id);
    } catch {
      // sessionStorage can throw in private browsing contexts — losing reload-resilience is fine.
    }
  }

  lastCandidateProfileId(): string | null {
    try {
      return sessionStorage.getItem(LAST_PROFILE_ID_KEY);
    } catch {
      return null;
    }
  }
}
