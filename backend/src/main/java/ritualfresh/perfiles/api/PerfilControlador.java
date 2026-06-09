package ritualfresh.perfiles.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ritualfresh.compartido.ReglaNegocioException;
import ritualfresh.perfiles.CrearPerfilClienteRequest;
import ritualfresh.perfiles.CrearPerfilTrabajadorRequest;
import ritualfresh.perfiles.EditarPerfilClienteRequest;
import ritualfresh.perfiles.EditarPerfilTrabajadorRequest;
import ritualfresh.perfiles.PerfilServicio;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilControlador {
    private final PerfilServicio perfilServicio;

    public PerfilControlador(PerfilServicio perfilServicio) {
        this.perfilServicio = perfilServicio;
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilOperacionApiResponse crearPerfilCliente(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CrearPerfilClienteApiRequest request) {
        PerfilApiResponse perfil = PerfilApiResponse.desde(perfilServicio.crearPerfilCliente(
                extraerTokenSesion(authorization),
                new CrearPerfilClienteRequest(
                        request.getUrlFotoPerfil(),
                        request.getTelefonoContacto(),
                        request.getNombreCalle(),
                        request.getNumeroDomicilio(),
                        request.getPiso(),
                        request.getDepartamentoDomicilio(),
                        request.getCodigoPostal(),
                        request.getLocalidad(),
                        request.getProvincia(),
                        request.getPreferenciasContratacion())));

        return new PerfilOperacionApiResponse("Perfil de cliente creado correctamente.", perfil);
    }

    @PostMapping("/trabajadores")
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilOperacionApiResponse crearPerfilTrabajador(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CrearPerfilTrabajadorApiRequest request) {
        PerfilApiResponse perfil = PerfilApiResponse.desde(perfilServicio.crearPerfilTrabajador(
                extraerTokenSesion(authorization),
                new CrearPerfilTrabajadorRequest(
                        request.getUrlFotoPerfil(),
                        request.getDescripcion(),
                        request.getAniosExperiencia(),
                        request.getServiciosOfrecidos(),
                        request.getZonaTrabajo(),
                        request.getDisponibilidad(),
                        request.getPrecioHoraOrientativo())));

        return new PerfilOperacionApiResponse("Perfil de trabajador creado correctamente.", perfil);
    }

    @GetMapping("/me")
    public PerfilApiResponse obtenerMiPerfil(@RequestHeader("Authorization") String authorization) {
        return PerfilApiResponse.desde(perfilServicio.obtenerMiPerfil(extraerTokenSesion(authorization)));
    }

    @PutMapping("/clientes/me")
    public PerfilOperacionApiResponse editarPerfilCliente(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody EditarPerfilClienteApiRequest request) {
        PerfilApiResponse perfil = PerfilApiResponse.desde(perfilServicio.editarPerfilCliente(
                extraerTokenSesion(authorization),
                new EditarPerfilClienteRequest(
                        request.getUrlFotoPerfil(),
                        request.getTelefonoContacto(),
                        request.getNombreCalle(),
                        request.getNumeroDomicilio(),
                        request.getPiso(),
                        request.getDepartamentoDomicilio(),
                        request.getCodigoPostal(),
                        request.getLocalidad(),
                        request.getProvincia(),
                        request.getPreferenciasContratacion())));

        return new PerfilOperacionApiResponse("Perfil de cliente actualizado correctamente.", perfil);
    }

    @PutMapping("/trabajadores/me")
    public PerfilOperacionApiResponse editarPerfilTrabajador(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody EditarPerfilTrabajadorApiRequest request) {
        PerfilApiResponse perfil = PerfilApiResponse.desde(perfilServicio.editarPerfilTrabajador(
                extraerTokenSesion(authorization),
                new EditarPerfilTrabajadorRequest(
                        request.getUrlFotoPerfil(),
                        request.getDescripcion(),
                        request.getAniosExperiencia(),
                        request.getServiciosOfrecidos(),
                        request.getZonaTrabajo(),
                        request.getDisponibilidad(),
                        request.getPrecioHoraOrientativo())));

        return new PerfilOperacionApiResponse("Perfil de trabajador actualizado correctamente.", perfil);
    }

    private String extraerTokenSesion(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ReglaNegocioException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return authorization.substring("Bearer ".length()).trim();
    }
}
