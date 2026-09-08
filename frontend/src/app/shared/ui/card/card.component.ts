import { Component, input } from '@angular/core';

export type CardVariant = 'default' | 'elevated' | 'interactive' | 'inverse';

/** Design System card — see design-system/components/card.md. */
@Component({
  selector: 'zk-card',
  templateUrl: './card.component.html',
  styleUrl: './card.component.css',
})
export class CardComponent {
  readonly variant = input<CardVariant>('default');
}
