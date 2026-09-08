/** Mirrors backend `cv.api.CandidateProfileResponse` and friends. */

export interface SkillResponse {
  name: string;
  category: string;
  /** Self-reported level as it reads in the CV — NEVER a certified level. */
  claimedLevel: 'UNSPECIFIED' | 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
}

export interface SkillClaimResponse {
  id: string;
  statement: string;
  relatedSkills: string[];
  challengeableConcepts: string[];
}

export interface ExperienceResponse {
  title: string;
  company: string;
  period: string;
  summary: string;
}

export interface CandidateProfileResponse {
  id: string;
  role: string;
  experienceYears: number | null;
  skills: SkillResponse[];
  claims: SkillClaimResponse[];
  experiences: ExperienceResponse[];
}
