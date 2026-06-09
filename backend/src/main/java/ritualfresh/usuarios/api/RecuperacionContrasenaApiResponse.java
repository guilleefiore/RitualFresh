package ritualfresh.usuarios.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecuperacionContrasenaApiResponse {
    private final String mensaje;
    private final String tokenRecuperacion;
    private final LocalDateTime fechaHoraExpiracion;
}
