import { DecimalPipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { FinalReportResponse } from '../../core/models/interview.model';
import { AlertComponent, CardComponent, SkeletonComponent } from '../../shared/ui';

@Component({
  selector: 'app-report-page',
  imports: [DecimalPipe, RouterLink, CardComponent, AlertComponent, SkeletonComponent],
  templateUrl: './report.page.html',
  styleUrl: './report.page.css',
})
export class ReportPage implements OnInit {
  readonly report = signal<FinalReportResponse | null>(null);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly api: ApiService,
  ) {}

  ngOnInit(): void {
    const sessionId = this.route.snapshot.paramMap.get('id') ?? '';
    this.api.getReport(sessionId).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Impossible de charger le rapport pour cette session.');
      },
    });
  }
}
