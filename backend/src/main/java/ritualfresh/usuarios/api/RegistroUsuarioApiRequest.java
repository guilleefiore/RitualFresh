package ritualfresh.usuarios.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistroUsuarioApiRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private String dni;

    @NotBlank
    private String telefono;

    @NotBlank
    @Email
    private String mail;

    @NotBlank
    private String contrasena;

    @NotBlank
    private String confirmacionContrasena;

    @NotNull
    private RolRegistroUsuario rol;
}
