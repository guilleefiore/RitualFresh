package ritualfresh.perfiles.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ritualfresh.perfiles.PerfilUsuarioResultado;
import ritualfresh.perfiles.TipoPerfil;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PerfilApiResponse {
    private final TipoPerfil tipoPerfil;
    private final Long idPerfil;
    private final Long idUsuario;
    private final String nombre;
    private final String apellido;
    private final String mail;
    private final String urlFotoPerfil;
    private final Integer clasificacionCliente;
    private final Integer puestoRanking;
    private final String descripcion;
    private final Integer aniosExperiencia;
    private final String serviciosOfrecidos;
    private final String zonaTrabajo;
    private final String disponibilidad;
    private final BigDecimal precioHoraOrientativo;
    private final String telefonoContacto;
    private final String nombreCalle;
    private final String numeroDomicilio;
    private final String piso;
    private final String departamentoDomicilio;
    private final String codigoPostal;
    private final String localidad;
    private final String provincia;
    private final String preferenciasContratacion;

    public static PerfilApiResponse desde(PerfilUsuarioResultado resultado) {
        return new PerfilApiResponse(
                resultado.getTipoPerfil(),
                resultado.getIdPerfil(),
                resultado.getIdUsuario(),
                resultado.getNombre(),
                resultado.getApellido(),
                resultado.getMail(),
                resultado.getUrlFotoPerfil(),
                resultado.getClasificacionCliente(),
                resultado.getPuestoRanking(),
                resultado.getDescripcion(),
                resultado.getAniosExperiencia(),
                resultado.getServiciosOfrecidos(),
                resultado.getZonaTrabajo(),
                resultado.getDisponibilidad(),
                resultado.getPrecioHoraOrientativo(),
                resultado.getTelefonoContacto(),
                resultado.getNombreCalle(),
                resultado.getNumeroDomicilio(),
                resultado.getPiso(),
                resultado.getDepartamentoDomicilio(),
                resultado.getCodigoPostal(),
                resultado.getLocalidad(),
                resultado.getProvincia(),
                resultado.getPreferenciasContratacion());
    }
}
