package com.securebank.notification.service;

import com.securebank.auth.service.AuthService;
import com.securebank.customer.entity.Customer;
import com.securebank.notification.dto.NotificationResponse;
import com.securebank.notification.entity.Notification;
import com.securebank.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthService authService;

    public NotificationService(NotificationRepository notificationRepository, AuthService authService) {
        this.notificationRepository = notificationRepository;
        this.authService = authService;
    }

    public List<NotificationResponse> getNotifications() {
        Customer customer = authService.getAuthenticatedCustomer();
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long id) {
        Customer customer = authService.getAuthenticatedCustomer();
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getCustomer().getId().equals(customer.getId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.isRead(),
                n.getType(),
                n.getCreatedAt()
        );
    }
}
