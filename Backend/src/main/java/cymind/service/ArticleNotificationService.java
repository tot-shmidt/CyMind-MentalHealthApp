package cymind.service;

import cymind.enums.UserType;
import cymind.exceptions.AuthorizationDeniedException;
import cymind.model.AbstractUser;
import cymind.model.ResourceNotification;
import cymind.repository.ResourceNotificationRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleNotificationService {
    @Autowired
    private ResourceNotificationRepository resourceNotifRepo;

    @Transactional
    public void deleteNotification(long id) {
        checkAuth();

        ResourceNotification notification = resourceNotifRepo.findById(id)
                .orElseThrow(() -> new NoResultException("Notification not found with id: " + id));

        resourceNotifRepo.delete(notification);
    }

    private void checkAuth() throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (authedUser.getUserType() != UserType.STUDENT) {
            throw new AuthorizationDeniedException("Only Professionals can delete notifications.");
        }
    }
}
