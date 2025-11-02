package cymind.dto.errors;

import java.time.LocalDateTime;
import java.util.List;

public record OverlappingAppointmentDTO(Long groupId, Long studentId, List<Long> professionalIds, LocalDateTime startTime, LocalDateTime endTime) {
}
