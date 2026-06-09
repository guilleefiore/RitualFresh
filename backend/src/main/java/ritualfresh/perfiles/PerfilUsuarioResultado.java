package ritualfresh.perfiles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ritualfresh.usuarios.Usuario;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PerfilUsuarioResultado {
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

    public static PerfilUsuarioResultado desdeCliente(PerfilCliente perfil) {
        Usuario usuario = perfil.getUsuario();

        return new PerfilUsuarioResultado(
                TipoPerfil.CLIENTE,
                perfil.getIdCliente(),
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getMail(),
                perfil.getUrlFotoPerfil(),
                perfil.getClasificacionCliente(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                perfil.getTelefonoContacto(),
                perfil.getNombreCalle(),
                perfil.getNumeroDomicilio(),
                perfil.getPiso(),
                perfil.getDepartamentoDomicilio(),
                perfil.getCodigoPostal(),
                perfil.getLocalidad(),
                perfil.getProvincia(),
                perfil.getPreferenciasContratacion());
    }

    public static PerfilUsuarioResultado desdeTrabajador(PerfilTrabajador perfil) {
        Usuario usuario = perfil.getUsuario();

        return new PerfilUsuarioResultado(
                TipoPerfil.TRABAJADOR,
                perfil.getIdTrabajador(),
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getMail(),
                perfil.getUrlFotoPerfil(),
                null,
                perfil.getPuestoRanking(),
                perfil.getDescripcion(),
                perfil.getAniosExperiencia(),
                perfil.getServiciosOfrecidos(),
                perfil.getZonaTrabajo(),
                perfil.getDisponibilidad(),
                perfil.getPrecioHoraOrientativo(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
