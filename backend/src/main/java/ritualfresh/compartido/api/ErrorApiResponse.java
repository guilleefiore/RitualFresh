package ritualfresh.compartido.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorApiResponse {
    private final int estado;
    private final String mensaje;
}
