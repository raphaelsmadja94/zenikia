import { Component, input } from '@angular/core';

export type ButtonVariant = 'primary' | 'brand' | 'ghost' | 'danger';
export type ButtonSize = 'sm' | 'md' | 'lg';

/**
 * Design System button — see design-system/components/button.md.
 * `brand` (the Zenika gradient) is reserved for the 1-2 most important
 * actions of the product; never use it for a secondary action.
 */
@Component({
  selector: 'zk-button',
  templateUrl: './button.component.html',
  styleUrl: './button.component.css',
})
export class ButtonComponent {
  readonly variant = input<ButtonVariant>('primary');
  readonly size = input<ButtonSize>('md');
  readonly type = input<'button' | 'submit'>('button');
  readonly disabled = input(false);
  readonly loading = input(false);
}
