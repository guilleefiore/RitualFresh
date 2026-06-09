package ritualfresh.perfiles;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CrearPerfilTrabajadorRequest {
    private final String urlFotoPerfil;
    private final String descripcion;
    private final Integer aniosExperiencia;
    private final String serviciosOfrecidos;
    private final String zonaTrabajo;
    private final String disponibilidad;
    private final BigDecimal precioHoraOrientativo;
}
