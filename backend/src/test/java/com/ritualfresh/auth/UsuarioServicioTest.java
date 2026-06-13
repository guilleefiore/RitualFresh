package com.ritualfresh.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ritualfresh.shared.ReglaNegocioException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioServicioTest {
    private UsuarioServicio servicio;

    @BeforeEach
    void setUp() {
        UsuarioRepositorio repositorio = new UsuarioRepositorioEnMemoria();
        servicio = new UsuarioServicio(repositorio, new SesionUsuarioRepositorioEnMemoria());
    }

    @Test
    void us01M01Rf01RegistraUsuarioClienteConCuentaPendienteDeValidacion() {
        ResultadoRegistroUsuario resultado = registrarCliente();

        assertEquals(EstadoCuenta.PENDIENTE_VALIDACION, resultado.getUsuario().getEstadoCuenta());
        assertEquals(RolUsuario.CLIENTE, resultado.getUsuario().getRol());
        assertNotNull(resultado.getTokenValidacionCuenta());
    }

    @Test
    void us01M01Rf01ImpideRegistrarCorreoExistente() {
        registrarCliente();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> servicio.registrarUsuario(new RegistroUsuarioRequest(
                "Otra",
                "Persona",
                "87654321",
                "2611111111",
                "guillermina@example.com",
                "clave123",
                "clave123",
                RolUsuario.TRABAJADOR)));

        assertEquals("El correo ya se encuentra registrado.", excepcion.getMessage());
    }

    @Test
    void us01M01Rf01ImpideRegistrarConContrasenasDistintas() {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> servicio.registrarUsuario(new RegistroUsuarioRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "otraClave",
                RolUsuario.CLIENTE)));

        assertEquals("Las contrasenas no coinciden.", excepcion.getMessage());
    }

    @Test
    void us02M01Rf02ImpideIniciarSesionAntesDeValidarCuenta() {
        registrarCliente();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> servicio.iniciarSesion(new InicioSesionRequest(
                "guillermina@example.com",
                "clave123")));

        assertEquals("Debe validar su cuenta antes de iniciar sesion.", excepcion.getMessage());
    }

    @Test
    void us02M01Rf02PermiteIniciarSesionDespuesDeValidarCuentaYGeneraSesion() {
        ResultadoRegistroUsuario resultado = registrarCliente();
        Usuario usuarioValidado = servicio.validarCuenta(resultado.getTokenValidacionCuenta());

        ResultadoInicioSesionUsuario resultadoInicioSesion = servicio.iniciarSesion(new InicioSesionRequest(
                "guillermina@example.com",
                "clave123"));
        Usuario usuarioAutenticado = servicio.obtenerUsuarioAutenticado(resultadoInicioSesion.getTokenSesion());

        assertEquals(EstadoCuenta.ACTIVA, usuarioValidado.getEstadoCuenta());
        assertEquals(RolUsuario.CLIENTE, resultadoInicioSesion.getUsuario().getRol());
        assertEquals(usuarioValidado.getIdUsuario(), usuarioAutenticado.getIdUsuario());
        assertNotNull(resultadoInicioSesion.getTokenSesion());
        assertNotNull(resultadoInicioSesion.getFechaHoraExpiracionSesion());
    }

    @Test
    void us02M01Rf02ImpideIniciarSesionConCredencialesIncorrectas() {
        ResultadoRegistroUsuario resultado = registrarCliente();
        servicio.validarCuenta(resultado.getTokenValidacionCuenta());

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> servicio.iniciarSesion(new InicioSesionRequest(
                "guillermina@example.com",
                "incorrecta")));

        assertEquals("El correo o la contrasena son incorrectos.", excepcion.getMessage());
    }

    @Test
    void us03M01Rf03GeneraTokenDeRecuperacionYPermiteCambiarContrasena() {
        ResultadoRegistroUsuario resultado = registrarCliente();
        servicio.validarCuenta(resultado.getTokenValidacionCuenta());

        ResultadoRecuperacionContrasena recuperacion = servicio.solicitarRecuperacionContrasena(
                new SolicitudRecuperacionContrasenaRequest("guillermina@example.com"));
        servicio.confirmarRecuperacionContrasena(new ConfirmacionRecuperacionContrasenaRequest(
                recuperacion.getTokenRecuperacion(),
                "nuevaClave123",
                "nuevaClave123"));

        ResultadoInicioSesionUsuario resultadoInicioSesion = servicio.iniciarSesion(new InicioSesionRequest(
                "guillermina@example.com",
                "nuevaClave123"));

        assertNotNull(recuperacion.getTokenRecuperacion());
        assertNotNull(resultadoInicioSesion.getTokenSesion());
    }

    @Test
    void us03M01Rf03ImpideRecuperacionConCorreoInexistente() {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> servicio.solicitarRecuperacionContrasena(
                new SolicitudRecuperacionContrasenaRequest("nadie@example.com")));

        assertEquals("No existe una cuenta asociada al correo ingresado.", excepcion.getMessage());
    }

    private ResultadoRegistroUsuario registrarCliente() {
        return servicio.registrarUsuario(new RegistroUsuarioRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "clave123",
                RolUsuario.CLIENTE));
    }
}
