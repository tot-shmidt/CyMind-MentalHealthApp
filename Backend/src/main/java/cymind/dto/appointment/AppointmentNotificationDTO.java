package cymind.dto.appointment;

import java.time.LocalDateTime;

// This DTO contains the exact fields you requested
public record AppointmentNotificationDTO(
        String type,
        String title,
        String location,
        LocalDateTime startTime
) {}
