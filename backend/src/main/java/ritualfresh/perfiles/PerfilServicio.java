package ritualfresh.perfiles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ritualfresh.compartido.ReglaNegocioException;
import ritualfresh.usuarios.RolUsuario;
import ritualfresh.usuarios.Usuario;
import ritualfresh.usuarios.UsuarioServicio;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
public class PerfilServicio {
    private static final Pattern FORMATO_TELEFONO = Pattern.compile("^[0-9+()\\-\\s]{7,30}$");
    private static final Pattern FORMATO_CALLE = Pattern.compile("^[\\p{L}0-9 .'-]{2,120}$");
    private static final Pattern FORMATO_NUMERO_DOMICILIO = Pattern.compile("^[0-9]{1,6}[A-Za-z]?$");
    private static final Pattern FORMATO_CODIGO_POSTAL = Pattern.compile("^[A-Za-z0-9\\s-]{3,12}$");

    private final UsuarioServicio usuarioServicio;
    private final PerfilClienteRepositorio perfilClienteRepositorio;
    private final PerfilTrabajadorRepositorio perfilTrabajadorRepositorio;

    public PerfilServicio(
            UsuarioServicio usuarioServicio,
            PerfilClienteRepositorio perfilClienteRepositorio,
            PerfilTrabajadorRepositorio perfilTrabajadorRepositorio) {
        this.usuarioServicio = usuarioServicio;
        this.perfilClienteRepositorio = perfilClienteRepositorio;
        this.perfilTrabajadorRepositorio = perfilTrabajadorRepositorio;
    }

    @Transactional
    public PerfilUsuarioResultado crearPerfilCliente(String tokenSesion, CrearPerfilClienteRequest request) {
        validarRequest(request);
        Usuario usuario = usuarioServicio.obtenerUsuarioAutenticado(tokenSesion);
        validarRol(usuario, RolUsuario.CLIENTE, "El rol del usuario no permite crear un perfil de cliente.");
        validarUsuarioSinPerfil(usuario.getIdUsuario());

        PerfilCliente perfil = new PerfilCliente(
                usuario,
                normalizarOpcional(request.getUrlFotoPerfil()),
                validarTelefonoContacto(request.getTelefonoContacto()),
                validarNombreCalle(request.getNombreCalle()),
                validarNumeroDomicilio(request.getNumeroDomicilio()),
                normalizarOpcional(request.getPiso()),
                normalizarOpcional(request.getDepartamentoDomicilio()),
                validarCodigoPostal(request.getCodigoPostal()),
                validarTextoObligatorio(request.getLocalidad(), "localidad"),
                validarTextoObligatorio(request.getProvincia(), "provincia"),
                validarTextoObligatorio(request.getPreferenciasContratacion(), "preferencias de contratacion"));

        return PerfilUsuarioResultado.desdeCliente(perfilClienteRepositorio.guardar(perfil));
    }

    @Transactional
    public PerfilUsuarioResultado crearPerfilTrabajador(String tokenSesion, CrearPerfilTrabajadorRequest request) {
        validarRequest(request);
        Usuario usuario = usuarioServicio.obtenerUsuarioAutenticado(tokenSesion);
        validarRol(usuario, RolUsuario.TRABAJADOR, "El rol del usuario no permite crear un perfil de trabajador.");
        validarUsuarioSinPerfil(usuario.getIdUsuario());

        PerfilTrabajador perfil = new PerfilTrabajador(
                usuario,
                normalizarOpcional(request.getUrlFotoPerfil()),
                validarTextoObligatorio(request.getDescripcion(), "descripcion del trabajador"),
                validarAniosExperiencia(request.getAniosExperiencia()),
                validarTextoObligatorio(request.getServiciosOfrecidos(), "servicios ofrecidos"),
                validarTextoObligatorio(request.getZonaTrabajo(), "zona de trabajo"),
                validarTextoObligatorio(request.getDisponibilidad(), "disponibilidad"),
                validarPrecioHora(request.getPrecioHoraOrientativo()));

        return PerfilUsuarioResultado.desdeTrabajador(perfilTrabajadorRepositorio.guardar(perfil));
    }

    @Transactional(readOnly = true)
    public PerfilUsuarioResultado obtenerMiPerfil(String tokenSesion) {
        Usuario usuario = usuarioServicio.obtenerUsuarioAutenticado(tokenSesion);

        return perfilClienteRepositorio.buscarPorUsuarioId(usuario.getIdUsuario())
                .map(PerfilUsuarioResultado::desdeCliente)
                .or(() -> perfilTrabajadorRepositorio.buscarPorUsuarioId(usuario.getIdUsuario())
                        .map(PerfilUsuarioResultado::desdeTrabajador))
                .orElseThrow(() -> new ReglaNegocioException("El usuario no posee un perfil creado."));
    }

    @Transactional
    public PerfilUsuarioResultado editarPerfilCliente(String tokenSesion, EditarPerfilClienteRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos del perfil.");
        }

        Usuario usuario = usuarioServicio.obtenerUsuarioAutenticado(tokenSesion);
        validarRol(usuario, RolUsuario.CLIENTE, "El rol del usuario no permite editar un perfil de cliente.");

        PerfilCliente perfil = perfilClienteRepositorio.buscarPorUsuarioId(usuario.getIdUsuario())
                .orElseThrow(() -> new ReglaNegocioException("El usuario no posee un perfil de cliente."));
        perfil.editar(
                normalizarOpcional(request.getUrlFotoPerfil()),
                validarTelefonoContacto(request.getTelefonoContacto()),
                validarNombreCalle(request.getNombreCalle()),
                validarNumeroDomicilio(request.getNumeroDomicilio()),
                normalizarOpcional(request.getPiso()),
                normalizarOpcional(request.getDepartamentoDomicilio()),
                validarCodigoPostal(request.getCodigoPostal()),
                validarTextoObligatorio(request.getLocalidad(), "localidad"),
                validarTextoObligatorio(request.getProvincia(), "provincia"),
                validarTextoObligatorio(request.getPreferenciasContratacion(), "preferencias de contratacion"));

        return PerfilUsuarioResultado.desdeCliente(perfilClienteRepositorio.guardar(perfil));
    }

    @Transactional
    public PerfilUsuarioResultado editarPerfilTrabajador(String tokenSesion, EditarPerfilTrabajadorRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos del perfil.");
        }

        Usuario usuario = usuarioServicio.obtenerUsuarioAutenticado(tokenSesion);
        validarRol(usuario, RolUsuario.TRABAJADOR, "El rol del usuario no permite editar un perfil de trabajador.");

        PerfilTrabajador perfil = perfilTrabajadorRepositorio.buscarPorUsuarioId(usuario.getIdUsuario())
                .orElseThrow(() -> new ReglaNegocioException("El usuario no posee un perfil de trabajador."));
        perfil.editar(
                normalizarOpcional(request.getUrlFotoPerfil()),
                validarTextoObligatorio(request.getDescripcion(), "descripcion del trabajador"),
                validarAniosExperiencia(request.getAniosExperiencia()),
                validarTextoObligatorio(request.getServiciosOfrecidos(), "servicios ofrecidos"),
                validarTextoObligatorio(request.getZonaTrabajo(), "zona de trabajo"),
                validarTextoObligatorio(request.getDisponibilidad(), "disponibilidad"),
                validarPrecioHora(request.getPrecioHoraOrientativo()));

        return PerfilUsuarioResultado.desdeTrabajador(perfilTrabajadorRepositorio.guardar(perfil));
    }

    private void validarRequest(CrearPerfilClienteRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos del perfil.");
        }
    }

    private void validarRequest(CrearPerfilTrabajadorRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos del perfil.");
        }
    }

    private void validarRol(Usuario usuario, RolUsuario rolEsperado, String mensaje) {
        if (usuario.getRol() != rolEsperado) {
            throw new ReglaNegocioException(mensaje);
        }
    }

    private void validarUsuarioSinPerfil(Long idUsuario) {
        if (perfilClienteRepositorio.existePorUsuarioId(idUsuario)
                || perfilTrabajadorRepositorio.existePorUsuarioId(idUsuario)) {
            throw new ReglaNegocioException("El usuario ya posee un perfil creado.");
        }
    }

    private String validarTelefonoContacto(String telefonoContacto) {
        String telefono = validarTextoObligatorio(telefonoContacto, "telefono de contacto");
        if (!FORMATO_TELEFONO.matcher(telefono).matches()) {
            throw new ReglaNegocioException("El telefono de contacto no posee un formato valido.");
        }

        return telefono;
    }

    private String validarNombreCalle(String nombreCalle) {
        String calle = validarTextoObligatorio(nombreCalle, "calle");
        if (!FORMATO_CALLE.matcher(calle).matches()) {
            throw new ReglaNegocioException("La direccion ingresada no posee un formato valido.");
        }

        return calle;
    }

    private String validarNumeroDomicilio(String numeroDomicilio) {
        String numero = validarTextoObligatorio(numeroDomicilio, "numero de domicilio");
        if (!FORMATO_NUMERO_DOMICILIO.matcher(numero).matches()) {
            throw new ReglaNegocioException("La direccion ingresada no posee un formato valido.");
        }

        return numero;
    }

    private String validarCodigoPostal(String codigoPostal) {
        String codigo = validarTextoObligatorio(codigoPostal, "codigo postal");
        if (!FORMATO_CODIGO_POSTAL.matcher(codigo).matches()) {
            throw new ReglaNegocioException("La direccion ingresada no posee un formato valido.");
        }

        return codigo;
    }

    private int validarAniosExperiencia(Integer aniosExperiencia) {
        if (aniosExperiencia == null) {
            throw new ReglaNegocioException("Debe completar los anios de experiencia.");
        }

        if (aniosExperiencia < 0) {
            throw new ReglaNegocioException("Los anios de experiencia no pueden ser negativos.");
        }

        return aniosExperiencia;
    }

    private BigDecimal validarPrecioHora(BigDecimal precioHoraOrientativo) {
        if (precioHoraOrientativo == null) {
            throw new ReglaNegocioException("Debe completar el precio por hora orientativo.");
        }

        if (precioHoraOrientativo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El precio por hora orientativo debe ser mayor a cero.");
        }

        return precioHoraOrientativo;
    }

    private String validarTextoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("Debe completar el campo " + nombreCampo + ".");
        }

        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}
