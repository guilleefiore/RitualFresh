package ritualfresh.usuarios.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfirmacionRecuperacionContrasenaApiRequest {
    @NotBlank
    private String tokenRecuperacion;

    @NotBlank
    private String contrasena;

    @NotBlank
    private String confirmacionContrasena;
}
