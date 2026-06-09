package ritualfresh.usuarios;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistroUsuarioRequest {
    private final String nombre;
    private final String apellido;
    private final String dni;
    private final String telefono;
    private final String mail;
    private final String contrasena;
    private final String confirmacionContrasena;
    private final RolUsuario rol;
}
