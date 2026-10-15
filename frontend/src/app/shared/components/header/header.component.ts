import { Component, OnInit, ChangeDetectorRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ThemeService } from '../../../core/services/theme.service';
import { NotificationItem } from '../../../core/models/models';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  @Input() primaryActionLabel = '+ New Transfer';
  @Input() primaryActionRoute = '/transfers';
  @Input() secondaryActionLabel = 'Manage Accounts';
  @Input() secondaryActionRoute = '/accounts';
  @Input() pageTitle = '';
  @Input() pageSubtitle = '';
  @Input() pageKicker = '';

  notifications: NotificationItem[] = [];
  showNotifications = false;

  constructor(
    public authService: AuthService,
    public themeService: ThemeService,
    private notificationService: NotificationService,
    public router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    if (this.authService.isAuthenticated()) {
      this.notificationService.getNotifications().subscribe({
        next: (res) => {
          if (res.success && res.data) {
            this.notifications = res.data;
            this.cdr.markForCheck();
          }
        },
        error: () => {}
      });
    }
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  markRead(n: NotificationItem): void {
    if (!n.read) {
      this.notificationService.markAsRead(n.id).subscribe({
        next: () => {
          n.read = true;
          this.cdr.markForCheck();
        }
      });
    }
  }

  markAllAsRead(): void {
    this.notifications.forEach(n => {
      if (!n.read) {
        this.markRead(n);
      }
    });
  }

  get userFirstName(): string {
    const fullName = this.authService.currentUser()?.fullName || 'Aarav';
    return fullName.split(' ')[0];
  }

  get userLastName(): string {
    const parts = (this.authService.currentUser()?.fullName || 'Aarav Sharma').split(' ');
    return parts.length > 1 ? parts.slice(1).join(' ') : 'Sharma';
  }

  get userFullName(): string {
    return this.authService.currentUser()?.fullName || 'Aarav Sharma';
  }

  showUserMenu = false;

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  get userInitial(): string {
    const fullName = this.authService.currentUser()?.fullName || 'A';
    return fullName.charAt(0).toUpperCase();
  }

  get customerId(): string {
    return this.authService.currentUser()?.customerId || '100102345';
  }

  get currentDateLabel(): string {
    const options: Intl.DateTimeFormatOptions = { weekday: 'long', month: 'long', day: 'numeric' };
    return new Date().toLocaleDateString('en-US', options).toUpperCase();
  }

  get currentUrlPath(): string {
    return this.router.url;
  }

  logout(): void {
    this.authService.logout();
  }
}
