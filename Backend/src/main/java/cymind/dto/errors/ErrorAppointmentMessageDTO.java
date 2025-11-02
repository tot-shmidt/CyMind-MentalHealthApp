package cymind.dto.errors;

import java.util.Date;
import java.util.List;

public record ErrorAppointmentMessageDTO(String path, Date timestamp, List<OverlappingAppointmentDTO> errors) {
}