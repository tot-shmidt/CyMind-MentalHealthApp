package cymind.dto;

import java.util.Date;
import java.util.List;

public record ErrorMessageDTO(String path, Date timestamp, List<String> errors) {
    public ErrorMessageDTO(String path, List<String> errors) {
        this(path, new Date(), errors);
    }

    public ErrorMessageDTO(String path, Exception exception) {
        this(path, new Date(), List.of(exception.getLocalizedMessage()));
    }
}
