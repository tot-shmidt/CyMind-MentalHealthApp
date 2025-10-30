package cymind.dto.appointment;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateAppointmentGroupDTO(@NotNull List<Long> professionalIds, @NotNull Long studentId, String groupName) {
}
