import { LowerCasePipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AppStateService } from '../../core/services/app-state.service';
import { CandidateProfileResponse } from '../../core/models/candidate-profile.model';
import { InterviewPersona } from '../../core/models/interview.model';
import { AlertComponent, BadgeComponent, ButtonComponent, CardComponent, SkeletonComponent } from '../../shared/ui';

interface PersonaOption {
  value: InterviewPersona;
  label: string;
  description: string;
  icon: string;
}

const PERSONA_OPTIONS: PersonaOption[] = [
  {
    value: 'RECRUITER',
    label: 'Recruiter',
    description: 'Présentation, parcours, motivation, storytelling, synthèse.',
    icon: '🎯',
  },
  {
    value: 'DEVELOPER',
    label: 'Developer',
    description: 'Concepts techniques, fonctionnement réel, debugging, bonnes pratiques.',
    icon: '💻',
  },
  {
    value: 'TECH_LEAD',
    label: 'Tech Lead',
    description: 'Architecture, qualité, production, compromis, tests, observabilité.',
    icon: '🧭',
  },
  {
    value: 'CTO',
    label: 'CTO',
    description: 'Challenge fort des affirmations vagues : scalable, résilient, event-driven…',
    icon: '🧠',
  },
];

@Component({
  selector: 'app-profile-page',
  imports: [LowerCasePipe, CardComponent, BadgeComponent, AlertComponent, ButtonComponent, SkeletonComponent],
  templateUrl: './profile.page.html',
  styleUrl: './profile.page.css',
})
export class ProfilePage implements OnInit {
  readonly personaOptions = PERSONA_OPTIONS;
  readonly profile = signal<CandidateProfileResponse | null>(null);
  readonly selectedPersona = signal<InterviewPersona | null>(null);
  readonly loading = signal(true);
  readonly starting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  constructor(
    private readonly api: ApiService,
    private readonly appState: AppStateService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const current = this.appState.candidateProfile();
    if (current) {
      this.profile.set(current);
      this.loading.set(false);
      return;
    }

    const lastId = this.appState.lastCandidateProfileId();
    if (!lastId) {
      this.router.navigate(['/upload']);
      return;
    }

    this.api.getProfile(lastId).subscribe({
      next: (profile) => {
        this.appState.setCandidateProfile(profile);
        this.profile.set(profile);
        this.loading.set(false);
      },
      error: () => {
        this.router.navigate(['/upload']);
      },
    });
  }

  selectPersona(persona: InterviewPersona): void {
    this.selectedPersona.set(persona);
  }

  startInterview(): void {
    const profile = this.profile();
    const persona = this.selectedPersona();
    if (!profile || !persona || this.starting()) {
      return;
    }
    this.starting.set(true);
    this.errorMessage.set(null);

    this.api.startInterview(profile.id, persona).subscribe({
      next: (session) => {
        this.starting.set(false);
        this.router.navigate(['/interview', session.id]);
      },
      error: () => {
        this.starting.set(false);
        this.errorMessage.set("Impossible de démarrer l'entretien. Réessaie.");
      },
    });
  }
}
