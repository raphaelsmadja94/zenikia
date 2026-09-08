import { Component, input } from '@angular/core';

export type SkeletonShape = 'text' | 'block' | 'circle';

/** Design System skeleton — see design-system/components/data-display.md and patterns/loading-states.md. */
@Component({
  selector: 'zk-skeleton',
  templateUrl: './skeleton.component.html',
  styleUrl: './skeleton.component.css',
})
export class SkeletonComponent {
  readonly shape = input<SkeletonShape>('text');
  /** CSS width, e.g. "100%", "12rem". */
  readonly width = input('100%');
  /** CSS height, e.g. "1rem", "120px". Ignored for shape="circle" (square, driven by width). */
  readonly height = input('1rem');
}
