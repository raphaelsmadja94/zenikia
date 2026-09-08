import { Component, input } from '@angular/core';

export type BadgeVariant = 'neutral' | 'primary' | 'success' | 'warning' | 'error' | 'info';

/** Design System badge — see design-system/components/feedback.md. */
@Component({
  selector: 'zk-badge',
  templateUrl: './badge.component.html',
  styleUrl: './badge.component.css',
})
export class BadgeComponent {
  readonly variant = input<BadgeVariant>('neutral');
}
