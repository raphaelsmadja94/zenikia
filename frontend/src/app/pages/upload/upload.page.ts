import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService } from '../../core/services/api.service';
import { AppStateService } from '../../core/services/app-state.service';
import { ApiError } from '../../core/models/api-error.model';
import { AlertComponent, ButtonComponent, CardComponent } from '../../shared/ui';

@Component({
  selector: 'app-upload-page',
  imports: [CardComponent, AlertComponent, ButtonComponent],
  templateUrl: './upload.page.html',
  styleUrl: './upload.page.css',
})
export class UploadPage {
  readonly selectedFile = signal<File | null>(null);
  readonly uploading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly dragOver = signal(false);

  constructor(
    private readonly api: ApiService,
    private readonly appState: AppStateService,
    private readonly router: Router,
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.pickFile(file);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragOver.set(false);
    const file = event.dataTransfer?.files?.[0] ?? null;
    this.pickFile(file);
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragOver.set(true);
  }

  onDragLeave(): void {
    this.dragOver.set(false);
  }

  private pickFile(file: File | null): void {
    this.errorMessage.set(null);
    if (!file) {
      return;
    }
    if (file.type !== 'application/pdf') {
      this.errorMessage.set('Seuls les fichiers PDF sont acceptés.');
      return;
    }
    this.selectedFile.set(file);
  }

  submit(): void {
    const file = this.selectedFile();
    if (!file || this.uploading()) {
      return;
    }
    this.uploading.set(true);
    this.errorMessage.set(null);

    this.api.analyzeCv(file).subscribe({
      next: (profile) => {
        this.appState.setCandidateProfile(profile);
        this.uploading.set(false);
        this.router.navigate(['/profile']);
      },
      error: (err: HttpErrorResponse) => {
        this.uploading.set(false);
        const apiError = err.error as ApiError | undefined;
        this.errorMessage.set(apiError?.message ?? "L'analyse du CV a échoué. Réessaie.");
      },
    });
  }
}
