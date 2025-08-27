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
            summary = "Registrar una solicitud de préstamo",
            description = "Registra una solicitud de préstamo validando automáticamente si el cliente ya existe por documento de identidad. Si existe, usa el cliente existente; si no existe, registra un nuevo cliente. Cumple con HU02: validación de información del cliente y detalles del préstamo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud registrada exitosamente (cliente nuevo o existente)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SolicitudResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o tipo de préstamo no existe",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Error al registrar nuevo usuario (email ya existe con documento diferente)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping(path = "/v1/solicitud", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SolicitudResponse> registrarSolicitud(
            @Parameter(description = "Datos completos de la solicitud de préstamo incluyendo información del cliente (se validará automáticamente si ya existe por documento)", required = true)
            @Valid @RequestBody RegistrarSolicitudRequest request) {
        log.debug("Recibida solicitud de registro para documento: {} - {}", 
            request.getUsuario().getTipoDocumento(), request.getUsuario().getNumeroDocumento());

        return Mono.just(request)
                .flatMap(req -> {
                    Usuario usuario = mapToUsuario(req);
                    Solicitud solicitud = mapToSolicitud(req);
                    log.info("Validando cliente y registrando solicitud");
                    return registrarSolicitudUseCase.registrarSolicitudConUsuario(usuario, solicitud);
                })
                .map(result -> mapToResponse(result.getSolicitud(), result.getUsuario()))
                .doOnSuccess(response -> log.info("Solicitud registrada exitosamente. Cliente ID: {}, Solicitud ID: {}",
                    response.getUsuario().getIdUsuario(), response.getIdSolicitud()));
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
