import { Component, computed, input } from '@angular/core';

/** Design System progress/meter — see design-system/components/data-display.md. */
@Component({
  selector: 'zk-progress-meter',
  templateUrl: './progress-meter.component.html',
  styleUrl: './progress-meter.component.css',
})
export class ProgressMeterComponent {
  readonly value = input.required<number>();
  readonly max = input(5);
  readonly label = input<string | null>(null);

  readonly percent = computed(() => {
    const max = this.max();
    if (max <= 0) return 0;
    return Math.max(0, Math.min(100, (this.value() / max) * 100));
  });
}
