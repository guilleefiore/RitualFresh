package ritualfresh.usuarios;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmacionRecuperacionContrasenaRequest {
    private final String tokenRecuperacion;
    private final String contrasena;
    private final String confirmacionContrasena;
}
