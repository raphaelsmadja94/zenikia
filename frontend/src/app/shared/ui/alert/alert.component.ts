import { Component, computed, input } from '@angular/core';

export type AlertVariant = 'success' | 'warning' | 'error' | 'info';

const ICONS: Record<AlertVariant, string> = {
  success: '✅',
  warning: '⚠️',
  error: '⛔',
  info: 'ℹ️',
};

/** Design System alert — see design-system/components/feedback.md. Announced via role="alert". */
@Component({
  selector: 'zk-alert',
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css',
})
export class AlertComponent {
  readonly variant = input<AlertVariant>('error');
  readonly icon = computed(() => ICONS[this.variant()]);
}
