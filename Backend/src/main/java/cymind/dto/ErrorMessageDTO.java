package cymind.dto;

import java.util.List;

public record ErrorMessageDTO(String url, List<String> error) {
    public ErrorMessageDTO(String url, Exception exception) {
        this(url, List.of(exception.getLocalizedMessage()));
    }
}
