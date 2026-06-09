package ritualfresh.usuarios.api;

import ritualfresh.usuarios.RolUsuario;

public enum RolRegistroUsuario {
    CLIENTE,
    TRABAJADOR;

    public RolUsuario toRolUsuario() {
        return RolUsuario.valueOf(name());
    }
}
