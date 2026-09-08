import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BadgeComponent, CardComponent } from '../../shared/ui';

@Component({
  selector: 'app-landing-page',
  imports: [RouterLink, BadgeComponent, CardComponent],
  templateUrl: './landing.page.html',
  styleUrl: './landing.page.css',
})
export class LandingPage {}
