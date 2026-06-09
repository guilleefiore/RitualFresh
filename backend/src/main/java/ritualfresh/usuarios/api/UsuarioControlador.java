package ritualfresh.usuarios.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ritualfresh.usuarios.ConfirmacionRecuperacionContrasenaRequest;
import ritualfresh.usuarios.InicioSesionRequest;
import ritualfresh.usuarios.RegistroUsuarioRequest;
import ritualfresh.usuarios.ResultadoRegistroUsuario;
import ritualfresh.usuarios.ResultadoInicioSesionUsuario;
import ritualfresh.usuarios.ResultadoRecuperacionContrasena;
import ritualfresh.usuarios.SolicitudRecuperacionContrasenaRequest;
import ritualfresh.usuarios.Usuario;
import ritualfresh.usuarios.UsuarioServicio;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {
    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroUsuarioApiResponse registrarUsuario(@Valid @RequestBody RegistroUsuarioApiRequest request) {
        ResultadoRegistroUsuario resultado = usuarioServicio.registrarUsuario(new RegistroUsuarioRequest(
                request.getNombre(),
                request.getApellido(),
                request.getDni(),
                request.getTelefono(),
                request.getMail(),
                request.getContrasena(),
                request.getConfirmacionContrasena(),
                request.getRol().toRolUsuario()));

        return new RegistroUsuarioApiResponse(
                resultado.getMensaje(),
                resultado.getTokenValidacionCuenta(),
                UsuarioApiResponse.desde(resultado.getUsuario()));
    }

    @GetMapping("/validacion")
    public ValidacionCuentaApiResponse validarCuenta(@RequestParam String token) {
        Usuario usuario = usuarioServicio.validarCuenta(token);

        return new ValidacionCuentaApiResponse(
                "Cuenta validada correctamente.",
                UsuarioApiResponse.desde(usuario));
    }

    @PostMapping("/login")
    public InicioSesionApiResponse iniciarSesion(@Valid @RequestBody InicioSesionApiRequest request) {
        ResultadoInicioSesionUsuario resultado = usuarioServicio.iniciarSesion(new InicioSesionRequest(
                request.getMail(),
                request.getContrasena()));

        return new InicioSesionApiResponse(
                "Inicio de sesion exitoso.",
                resultado.getTokenSesion(),
                resultado.getFechaHoraExpiracionSesion(),
                UsuarioApiResponse.desde(resultado.getUsuario()));
    }

    @PostMapping("/recuperacion-contrasena")
    public RecuperacionContrasenaApiResponse solicitarRecuperacionContrasena(
            @Valid @RequestBody SolicitudRecuperacionContrasenaApiRequest request) {
        ResultadoRecuperacionContrasena resultado = usuarioServicio.solicitarRecuperacionContrasena(
                new SolicitudRecuperacionContrasenaRequest(request.getMail()));

        return new RecuperacionContrasenaApiResponse(
                resultado.getMensaje(),
                resultado.getTokenRecuperacion(),
                resultado.getFechaHoraExpiracion());
    }

    @PostMapping("/recuperacion-contrasena/confirmacion")
    public MensajeApiResponse confirmarRecuperacionContrasena(
            @Valid @RequestBody ConfirmacionRecuperacionContrasenaApiRequest request) {
        usuarioServicio.confirmarRecuperacionContrasena(new ConfirmacionRecuperacionContrasenaRequest(
                request.getTokenRecuperacion(),
                request.getContrasena(),
                request.getConfirmacionContrasena()));

        return new MensajeApiResponse("Contrasena actualizada correctamente.");
    }

    @PostMapping("/logout")
    public MensajeApiResponse cerrarSesion(@RequestHeader("Authorization") String authorization) {
        usuarioServicio.cerrarSesion(extraerTokenSesion(authorization));

        return new MensajeApiResponse("Sesion cerrada correctamente.");
    }

    private String extraerTokenSesion(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return "";
        }

        return authorization.substring("Bearer ".length()).trim();
    }
}
