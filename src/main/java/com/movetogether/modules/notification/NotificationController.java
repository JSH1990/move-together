package com.movetogether.modules.notification;

import com.movetogether.modules.account.Account;
import com.movetogether.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentAccount Account account, Model model){
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "notifications/list";
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentAccount Account account, Model model){
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "notifications/list";
    }

    @PostMapping("/notifications")
    public String deleteNotifications(@CurrentAccount Account account){
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications,
                                             long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newClubNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingClubNotifications = new ArrayList<>();
        for (var notification : notifications) {
            switch (notification.getNotificationType()) {
                case CLUB_CREATED: newClubNotifications.add(notification); break;
                case CLUB_ENROLLMENT: eventEnrollmentNotifications.add(notification); break;
                case CLUB_UPDATED: watchingClubNotifications.add(notification); break;
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newClubNotifications", newClubNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingClubNotifications", watchingClubNotifications);
    }
}
