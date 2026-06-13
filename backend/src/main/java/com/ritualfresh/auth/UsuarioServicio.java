package com.ritualfresh.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ritualfresh.shared.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsuarioServicio {
    private static final Pattern FORMATO_MAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int HORAS_VIGENCIA_SESION = 8;
    private static final int HORAS_VIGENCIA_RECUPERACION = 1;

    private final UsuarioRepositorio usuarioRepositorio;
    private final SesionUsuarioRepositorio sesionUsuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, SesionUsuarioRepositorio sesionUsuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.sesionUsuarioRepositorio = sesionUsuarioRepositorio;
    }

    @Transactional
    public ResultadoRegistroUsuario registrarUsuario(RegistroUsuarioRequest request) {
        validarRegistro(request);

        String mailNormalizado = normalizarMail(request.getMail());
        String tokenValidacion = UUID.randomUUID().toString();
        Usuario usuario = new Usuario(
                request.getNombre().trim(),
                request.getApellido().trim(),
                request.getDni().trim(),
                request.getTelefono().trim(),
                mailNormalizado,
                SeguridadContrasena.generarHash(request.getContrasena()),
                request.getRol(),
                tokenValidacion);

        usuarioRepositorio.guardar(usuario);

        return new ResultadoRegistroUsuario(
                usuario,
                "Registro exitoso. La cuenta queda pendiente de validacion.",
                tokenValidacion);
    }

    @Transactional
    public Usuario validarCuenta(String tokenValidacion) {
        Usuario usuario = usuarioRepositorio.buscarPorTokenValidacionCuenta(tokenValidacion)
                .orElseThrow(() -> new ReglaNegocioException("El enlace de validacion no es valido o expiro."));

        usuario.validarCuenta();
        usuarioRepositorio.guardar(usuario);

        return usuario;
    }

    @Transactional
    public ResultadoInicioSesionUsuario iniciarSesion(InicioSesionRequest request) {
        validarInicioSesion(request);

        Usuario usuario = usuarioRepositorio.buscarPorMail(request.getMail())
                .orElseThrow(() -> new ReglaNegocioException("El correo o la contrasena son incorrectos."));

        if (!SeguridadContrasena.coincide(request.getContrasena(), usuario.getContrasenaHash())) {
            throw new ReglaNegocioException("El correo o la contrasena son incorrectos.");
        }

        if (usuario.getEstadoCuenta() == EstadoCuenta.PENDIENTE_VALIDACION) {
            throw new ReglaNegocioException("Debe validar su cuenta antes de iniciar sesion.");
        }

        if (!usuario.estaActivo()) {
            throw new ReglaNegocioException("La cuenta no se encuentra activa.");
        }

        LocalDateTime fechaHoraCreacion = LocalDateTime.now();
        LocalDateTime fechaHoraExpiracion = fechaHoraCreacion.plusHours(HORAS_VIGENCIA_SESION);
        String tokenSesion = UUID.randomUUID().toString();
        sesionUsuarioRepositorio.guardar(new SesionUsuario(usuario, tokenSesion, fechaHoraCreacion, fechaHoraExpiracion));

        return new ResultadoInicioSesionUsuario(usuario, tokenSesion, fechaHoraExpiracion);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioAutenticado(String tokenSesion) {
        SesionUsuario sesion = sesionUsuarioRepositorio.buscarPorToken(tokenSesion)
                .orElseThrow(() -> new ReglaNegocioException("Debe iniciar sesion para acceder a esta funcionalidad."));

        if (!sesion.estaVigente(LocalDateTime.now())) {
            throw new ReglaNegocioException("La sesion expiro. Debe iniciar sesion nuevamente.");
        }

        Usuario usuario = sesion.getUsuario();
        if (!usuario.estaActivo()) {
            throw new ReglaNegocioException("La cuenta no se encuentra activa.");
        }

        return usuario;
    }

    @Transactional
    public void cerrarSesion(String tokenSesion) {
        SesionUsuario sesion = sesionUsuarioRepositorio.buscarPorToken(tokenSesion)
                .orElseThrow(() -> new ReglaNegocioException("La sesion indicada no existe."));
        sesion.cerrar(LocalDateTime.now());
        sesionUsuarioRepositorio.guardar(sesion);
    }

    @Transactional
    public ResultadoRecuperacionContrasena solicitarRecuperacionContrasena(SolicitudRecuperacionContrasenaRequest request) {
        validarSolicitudRecuperacion(request);

        Usuario usuario = usuarioRepositorio.buscarPorMail(request.getMail())
                .orElseThrow(() -> new ReglaNegocioException("No existe una cuenta asociada al correo ingresado."));

        String tokenRecuperacion = UUID.randomUUID().toString();
        LocalDateTime fechaHoraExpiracion = LocalDateTime.now().plusHours(HORAS_VIGENCIA_RECUPERACION);
        usuario.iniciarRecuperacionContrasena(tokenRecuperacion, fechaHoraExpiracion);
        usuarioRepositorio.guardar(usuario);

        return new ResultadoRecuperacionContrasena(
                "Se envio el enlace de recuperacion al correo electronico indicado.",
                tokenRecuperacion,
                fechaHoraExpiracion);
    }

    @Transactional
    public Usuario confirmarRecuperacionContrasena(ConfirmacionRecuperacionContrasenaRequest request) {
        validarConfirmacionRecuperacion(request);

        Usuario usuario = usuarioRepositorio.buscarPorTokenRecuperacionContrasena(request.getTokenRecuperacion())
                .orElseThrow(() -> new ReglaNegocioException("El enlace de recuperacion no es valido o expiro."));

        if (!usuario.tieneTokenRecuperacionVigente(LocalDateTime.now())) {
            throw new ReglaNegocioException("El enlace de recuperacion no es valido o expiro.");
        }

        usuario.cambiarContrasena(SeguridadContrasena.generarHash(request.getContrasena()));
        usuarioRepositorio.guardar(usuario);

        return usuario;
    }

    private void validarRegistro(RegistroUsuarioRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos de registro.");
        }

        validarCampoObligatorio(request.getNombre(), "nombre");
        validarCampoObligatorio(request.getApellido(), "apellido");
        validarCampoObligatorio(request.getDni(), "DNI");
        validarCampoObligatorio(request.getTelefono(), "telefono");
        validarCampoObligatorio(request.getMail(), "correo electronico");
        validarCampoObligatorio(request.getContrasena(), "contrasena");
        validarCampoObligatorio(request.getConfirmacionContrasena(), "confirmacion de contrasena");

        if (!esMailValido(request.getMail())) {
            throw new ReglaNegocioException("El correo ingresado no posee un formato valido.");
        }

        if (usuarioRepositorio.existeMail(request.getMail())) {
            throw new ReglaNegocioException("El correo ya se encuentra registrado.");
        }

        if (!request.getContrasena().equals(request.getConfirmacionContrasena())) {
            throw new ReglaNegocioException("Las contrasenas no coinciden.");
        }

        if (request.getRol() != RolUsuario.CLIENTE && request.getRol() != RolUsuario.TRABAJADOR) {
            throw new ReglaNegocioException("Debe seleccionar el rol cliente o trabajador.");
        }
    }

    private void validarInicioSesion(InicioSesionRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos de inicio de sesion.");
        }

        validarCampoObligatorio(request.getMail(), "correo electronico");
        validarCampoObligatorio(request.getContrasena(), "contrasena");

        if (!esMailValido(request.getMail())) {
            throw new ReglaNegocioException("El correo ingresado no posee un formato valido.");
        }
    }

    private void validarSolicitudRecuperacion(SolicitudRecuperacionContrasenaRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar el correo electronico.");
        }

        validarCampoObligatorio(request.getMail(), "correo electronico");

        if (!esMailValido(request.getMail())) {
            throw new ReglaNegocioException("El correo ingresado no posee un formato valido.");
        }
    }

    private void validarConfirmacionRecuperacion(ConfirmacionRecuperacionContrasenaRequest request) {
        if (request == null) {
            throw new ReglaNegocioException("Debe completar los datos de recuperacion.");
        }

        validarCampoObligatorio(request.getTokenRecuperacion(), "token de recuperacion");
        validarCampoObligatorio(request.getContrasena(), "contrasena");
        validarCampoObligatorio(request.getConfirmacionContrasena(), "confirmacion de contrasena");

        if (!request.getContrasena().equals(request.getConfirmacionContrasena())) {
            throw new ReglaNegocioException("Las contrasenas no coinciden.");
        }
    }

    private void validarCampoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("Debe completar el campo " + nombreCampo + ".");
        }
    }

    private boolean esMailValido(String mail) {
        return mail != null && FORMATO_MAIL.matcher(mail.trim()).matches();
    }

    private String normalizarMail(String mail) {
        return mail.trim().toLowerCase();
    }
}
