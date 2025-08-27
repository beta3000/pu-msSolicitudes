package rodriguez.ciro.usecase.registrarsolicitud;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import rodriguez.ciro.model.solicitud.Solicitud;
import rodriguez.ciro.model.solicitud.gateways.SolicitudRepository;
import rodriguez.ciro.model.tipoprestamo.gateways.TipoPrestamoRepository;
import rodriguez.ciro.model.usuario.Usuario;
import rodriguez.ciro.model.usuario.gateways.UsuarioGateway;

@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final UsuarioGateway usuarioGateway;

    private static final Long ESTADO_PENDIENTE_REVISION = 1L;

    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud) {
        return validarTipoPrestamo(solicitud.getIdTipoPrestamo())
                .then(Mono.just(solicitud))
                .map(this::asignarEstadoInicial)
                .flatMap(solicitudRepository::guardar);
    }

    public Mono<SolicitudConUsuarioResult> registrarSolicitudConUsuario(Usuario usuario, Solicitud solicitudConDatos) {
        return validarTipoPrestamo(solicitudConDatos.getIdTipoPrestamo())
                .then(usuarioGateway.registrarUsuario(usuario))
                .doOnNext(usuarioRegistrado -> System.out.println("UseCase - Usuario registrado: idUsuario=" + usuarioRegistrado.getIdUsuario()))
                .flatMap(usuarioRegistrado -> registrarSolicitudParaUsuario(solicitudConDatos, usuarioRegistrado)
                        .map(solicitud -> new SolicitudConUsuarioResult(solicitud, usuarioRegistrado)));
    }

    private Mono<Solicitud> registrarSolicitudParaUsuario(Solicitud solicitudConDatos, Usuario usuario) {
        Solicitud solicitud = Solicitud.builder()
                .monto(solicitudConDatos.getMonto())
                .plazo(solicitudConDatos.getPlazo())
                .email(usuario.getCorreoElectronico())
                .idTipoPrestamo(solicitudConDatos.getIdTipoPrestamo())
                .idEstado(ESTADO_PENDIENTE_REVISION)
                .build();

        return solicitudRepository.guardar(solicitud);
    }

    private Mono<Void> validarTipoPrestamo(Long idTipoPrestamo) {
        return tipoPrestamoRepository.existePorId(idTipoPrestamo)
                .filter(existe -> existe)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "El tipo de préstamo con ID " + idTipoPrestamo + " no existe")))
                .then();
    }

    private Solicitud asignarEstadoInicial(Solicitud solicitud) {
        return solicitud.toBuilder()
                .idEstado(ESTADO_PENDIENTE_REVISION)
                .build();
    }

    @lombok.RequiredArgsConstructor
    @lombok.Getter
    public static class SolicitudConUsuarioResult {
        private final Solicitud solicitud;
        private final Usuario usuario;
    }
}
