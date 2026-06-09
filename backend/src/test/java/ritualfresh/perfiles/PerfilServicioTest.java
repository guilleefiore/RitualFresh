package ritualfresh.perfiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ritualfresh.compartido.ReglaNegocioException;
import ritualfresh.usuarios.RegistroUsuarioRequest;
import ritualfresh.usuarios.ResultadoInicioSesionUsuario;
import ritualfresh.usuarios.ResultadoRegistroUsuario;
import ritualfresh.usuarios.RolUsuario;
import ritualfresh.usuarios.SesionUsuarioRepositorio;
import ritualfresh.usuarios.SesionUsuarioRepositorioEnMemoria;
import ritualfresh.usuarios.Usuario;
import ritualfresh.usuarios.UsuarioRepositorio;
import ritualfresh.usuarios.UsuarioRepositorioEnMemoria;
import ritualfresh.usuarios.UsuarioServicio;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PerfilServicioTest {
    private UsuarioServicio usuarioServicio;
    private PerfilServicio perfilServicio;

    @BeforeEach
    void setUp() {
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorioEnMemoria();
        SesionUsuarioRepositorio sesionUsuarioRepositorio = new SesionUsuarioRepositorioEnMemoria();
        usuarioServicio = new UsuarioServicio(usuarioRepositorio, sesionUsuarioRepositorio);
        perfilServicio = new PerfilServicio(
                usuarioServicio,
                new PerfilClienteRepositorioEnMemoria(),
                new PerfilTrabajadorRepositorioEnMemoria());
    }

    @Test
    void us02M02Rf02CreaPerfilClienteConDatosValidos() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionCliente();

        PerfilUsuarioResultado resultado = perfilServicio.crearPerfilCliente(
                sesion.getTokenSesion(),
                requestClienteValido());

        assertEquals(TipoPerfil.CLIENTE, resultado.getTipoPerfil());
        assertEquals(sesion.getUsuario().getIdUsuario(), resultado.getIdUsuario());
        assertEquals("https://cdn.example.com/cliente.png", resultado.getUrlFotoPerfil());
        assertEquals("2615555555", resultado.getTelefonoContacto());
        assertEquals("San Martin", resultado.getNombreCalle());
        assertEquals("Limpieza semanal por la manana", resultado.getPreferenciasContratacion());
        assertEquals(0, resultado.getClasificacionCliente());
    }

    @Test
    void us02M02Rf02ImpideTelefonoClienteConFormatoInvalido() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionCliente();
        CrearPerfilClienteRequest request = new CrearPerfilClienteRequest(
                null,
                "abc",
                "San Martin",
                "123",
                null,
                null,
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilCliente(
                sesion.getTokenSesion(),
                request));

        assertEquals("El telefono de contacto no posee un formato valido.", excepcion.getMessage());
    }

    @Test
    void us02M02Rf02ImpideDireccionClienteConFormatoInvalido() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionCliente();
        CrearPerfilClienteRequest request = new CrearPerfilClienteRequest(
                null,
                "2615555555",
                "!",
                "123",
                null,
                null,
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal");

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilCliente(
                sesion.getTokenSesion(),
                request));

        assertEquals("La direccion ingresada no posee un formato valido.", excepcion.getMessage());
    }

    @Test
    void us01M02Rf01CreaEditaYObtienePerfilTrabajadorConDatosValidos() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionTrabajador();
        perfilServicio.crearPerfilTrabajador(
                sesion.getTokenSesion(),
                requestTrabajadorValido());

        PerfilUsuarioResultado editado = perfilServicio.editarPerfilTrabajador(
                sesion.getTokenSesion(),
                new EditarPerfilTrabajadorRequest(
                        "https://cdn.example.com/trabajador.png",
                        "Limpieza general, profunda y mantenimiento preventivo",
                        4,
                        "Limpieza general, limpieza profunda, mantenimiento",
                        "Gran Mendoza",
                        "Lunes a viernes de 9 a 17",
                        new BigDecimal("4500.00")));
        PerfilUsuarioResultado obtenido = perfilServicio.obtenerMiPerfil(sesion.getTokenSesion());

        assertEquals(TipoPerfil.TRABAJADOR, editado.getTipoPerfil());
        assertEquals("Limpieza general, profunda y mantenimiento preventivo", obtenido.getDescripcion());
        assertEquals(4, obtenido.getAniosExperiencia());
        assertEquals("Gran Mendoza", obtenido.getZonaTrabajo());
        assertEquals(new BigDecimal("4500.00"), obtenido.getPrecioHoraOrientativo());
        assertEquals(0, obtenido.getPuestoRanking());
    }

    @Test
    void us01M02Rf01ImpideAniosExperienciaNegativos() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionTrabajador();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilTrabajador(
                sesion.getTokenSesion(),
                new CrearPerfilTrabajadorRequest(
                        null,
                        "Limpieza profunda",
                        -1,
                        "Limpieza profunda",
                        "Mendoza",
                        "Turno tarde",
                        new BigDecimal("3500.00"))));

        assertEquals("Los anios de experiencia no pueden ser negativos.", excepcion.getMessage());
    }

    @Test
    void us01M02Rf01ImpidePrecioHoraNoPositivo() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionTrabajador();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilTrabajador(
                sesion.getTokenSesion(),
                new CrearPerfilTrabajadorRequest(
                        null,
                        "Limpieza profunda",
                        2,
                        "Limpieza profunda",
                        "Mendoza",
                        "Turno tarde",
                        BigDecimal.ZERO)));

        assertEquals("El precio por hora orientativo debe ser mayor a cero.", excepcion.getMessage());
    }

    @Test
    void impideCrearPerfilClienteParaUsuarioTrabajador() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionTrabajador();

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilCliente(
                sesion.getTokenSesion(),
                requestClienteValido()));

        assertEquals("El rol del usuario no permite crear un perfil de cliente.", excepcion.getMessage());
    }

    @Test
    void impideCrearMasDeUnPerfilPorUsuario() {
        ResultadoInicioSesionUsuario sesion = registrarValidarEIniciarSesionCliente();
        perfilServicio.crearPerfilCliente(sesion.getTokenSesion(), requestClienteValido());

        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilCliente(
                sesion.getTokenSesion(),
                requestClienteValido()));

        assertEquals("El usuario ya posee un perfil creado.", excepcion.getMessage());
    }

    @Test
    void impideGestionarPerfilSinSesion() {
        ReglaNegocioException excepcion = assertThrows(ReglaNegocioException.class, () -> perfilServicio.crearPerfilCliente(
                "token-inexistente",
                requestClienteValido()));

        assertEquals("Debe iniciar sesion para acceder a esta funcionalidad.", excepcion.getMessage());
    }

    private ResultadoInicioSesionUsuario registrarValidarEIniciarSesionCliente() {
        Usuario usuario = registrarYValidarCliente();
        return usuarioServicio.iniciarSesion(new ritualfresh.usuarios.InicioSesionRequest(usuario.getMail(), "clave123"));
    }

    private ResultadoInicioSesionUsuario registrarValidarEIniciarSesionTrabajador() {
        Usuario usuario = registrarYValidarTrabajador();
        return usuarioServicio.iniciarSesion(new ritualfresh.usuarios.InicioSesionRequest(usuario.getMail(), "clave123"));
    }

    private Usuario registrarYValidarCliente() {
        ResultadoRegistroUsuario resultado = usuarioServicio.registrarUsuario(new RegistroUsuarioRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "clave123",
                RolUsuario.CLIENTE));

        return usuarioServicio.validarCuenta(resultado.getTokenValidacionCuenta());
    }

    private Usuario registrarYValidarTrabajador() {
        ResultadoRegistroUsuario resultado = usuarioServicio.registrarUsuario(new RegistroUsuarioRequest(
                "Joaquin",
                "Becerra",
                "22333444",
                "2612222222",
                "joaquin@example.com",
                "clave123",
                "clave123",
                RolUsuario.TRABAJADOR));

        return usuarioServicio.validarCuenta(resultado.getTokenValidacionCuenta());
    }

    private CrearPerfilClienteRequest requestClienteValido() {
        return new CrearPerfilClienteRequest(
                " https://cdn.example.com/cliente.png ",
                "2615555555",
                "San Martin",
                "123",
                "2",
                "A",
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal por la manana");
    }

    private CrearPerfilTrabajadorRequest requestTrabajadorValido() {
        return new CrearPerfilTrabajadorRequest(
                null,
                "Limpieza profunda y mantenimiento del hogar",
                3,
                "Limpieza general y profunda",
                "Godoy Cruz y Ciudad de Mendoza",
                "Lunes a viernes de 8 a 16",
                new BigDecimal("4000.00"));
    }
}
