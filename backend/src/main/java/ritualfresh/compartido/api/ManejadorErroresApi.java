package ritualfresh.compartido.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ritualfresh.compartido.ReglaNegocioException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorErroresApi {
    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApiResponse manejarReglaNegocio(ReglaNegocioException excepcion) {
        return new ErrorApiResponse(HttpStatus.BAD_REQUEST.value(), excepcion.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApiResponse manejarValidacion(MethodArgumentNotValidException excepcion) {
        String mensaje = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> "Debe completar correctamente el campo " + error.getField() + ".")
                .distinct()
                .collect(Collectors.joining(" "));

        return new ErrorApiResponse(HttpStatus.BAD_REQUEST.value(), mensaje);
    }
}
