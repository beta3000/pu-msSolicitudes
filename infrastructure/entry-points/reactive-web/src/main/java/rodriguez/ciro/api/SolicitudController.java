package rodriguez.ciro.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import rodriguez.ciro.api.dto.RegistrarSolicitudRequest;
import rodriguez.ciro.api.dto.SolicitudResponse;
import rodriguez.ciro.api.dto.UsuarioResponse;
import rodriguez.ciro.model.solicitud.Solicitud;
import rodriguez.ciro.model.usuario.Usuario;
import rodriguez.ciro.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

@Slf4j
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "API para gestión de solicitudes de préstamo")
public class SolicitudController {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @Operation(
            summary = "Registrar una nueva solicitud de préstamo con usuario",
            description = "Registra un nuevo usuario en el sistema externo y luego crea su solicitud de préstamo. Incluye toda la información del cliente (documento de identidad implícito en los datos) y los detalles del préstamo según la HU02."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario y solicitud registrados exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SolicitudResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o tipo de préstamo no existe",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Error al registrar usuario (correo ya existe)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping(path = "/v1/solicitud", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SolicitudResponse> registrarSolicitud(
            @Parameter(description = "Datos completos de la solicitud de préstamo incluyendo información del cliente", required = true)
            @Valid @RequestBody RegistrarSolicitudRequest request) {
        log.debug("Recibida solicitud de registro para email: {}", request.getUsuario().getCorreoElectronico());

        return Mono.just(request)
                .flatMap(req -> {
                    Usuario usuario = mapToUsuario(req);
                    Solicitud solicitud = mapToSolicitud(req);
                    log.info("Enviando usuario y solicitud al use case");
                    return registrarSolicitudUseCase.registrarSolicitudConUsuario(usuario, solicitud);
                })
                .map(result -> mapToResponse(result.getSolicitud(), result.getUsuario()))
                .doOnSuccess(response -> log.info("Usuario y solicitud registrados exitosamente. Usuario ID: {}, Solicitud ID: {}",
                    response.getUsuario().getIdUsuario(), response.getIdSolicitud()))
                .onErrorMap(IllegalArgumentException.class, ex ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage()))
                .onErrorMap(Exception.class, ex ->
                    new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor"));
    }

    private Usuario mapToUsuario(RegistrarSolicitudRequest request) {
        return Usuario.builder()
                .nombres(request.getUsuario().getNombres())
                .apellidos(request.getUsuario().getApellidos())
                .tipoDocumento(request.getUsuario().getTipoDocumento())
                .numeroDocumento(request.getUsuario().getNumeroDocumento())
                .correoElectronico(request.getUsuario().getCorreoElectronico())
                .fechaNacimiento(request.getUsuario().getFechaNacimiento())
                .direccion(request.getUsuario().getDireccion())
                .telefono(request.getUsuario().getTelefono())
                .salarioBase(request.getUsuario().getSalarioBase())
                .rol(rodriguez.ciro.model.usuario.Rol.builder()
                        .idRol(request.getUsuario().getIdRol())
                        .build())
                .build();
    }

    private Solicitud mapToSolicitud(RegistrarSolicitudRequest request) {
        return Solicitud.builder()
                .monto(request.getMonto())
                .plazo(request.getPlazo())
                .idTipoPrestamo(request.getIdTipoPrestamo())
                .build();
    }

    private SolicitudResponse mapToResponse(Solicitud solicitud, Usuario usuario) {
        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .direccion(usuario.getDireccion())
                .telefono(usuario.getTelefono())
                .salarioBase(usuario.getSalarioBase())
                .build();

        return SolicitudResponse.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .idTipoPrestamo(solicitud.getIdTipoPrestamo())
                .idEstado(solicitud.getIdEstado())
                .estado("Pendiente de revisión")
                .usuario(usuarioResponse)
                .build();
    }
}
