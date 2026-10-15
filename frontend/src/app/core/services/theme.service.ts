import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'securebank_theme';
  theme = signal<'dark' | 'light'>('dark');

  constructor() {
    this.init();
  }

  private init(): void {
    if (typeof window === 'undefined') return;
    const stored = localStorage.getItem(this.THEME_KEY) as 'dark' | 'light' | null;
    if (stored === 'light' || stored === 'dark') {
      this.applyTheme(stored);
    } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
      this.applyTheme('light');
    } else {
      this.applyTheme('dark');
    }
  }

  toggleTheme(): void {
    const next = this.theme() === 'dark' ? 'light' : 'dark';
    this.applyTheme(next);
  }

  private applyTheme(t: 'dark' | 'light'): void {
    this.theme.set(t);
    if (typeof document !== 'undefined') {
      document.documentElement.setAttribute('data-theme', t);
      localStorage.setItem(this.THEME_KEY, t);
    }
  }
}
