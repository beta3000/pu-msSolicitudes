package rodriguez.ciro.usecase.registrarsolicitud;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import rodriguez.ciro.model.exception.EmailEnUsoException;
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
                .then(Mono.defer(() -> {
                    // Primero intentar buscar usuario existente por documento
                    return usuarioGateway.buscarUsuarioPorDocumento(usuario.getTipoDocumento(), usuario.getNumeroDocumento())
                            .doOnNext(usuarioExistente -> System.out.println("UseCase - Usuario ya existe: idUsuario=" + usuarioExistente.getIdUsuario()))
                            // Si no existe por documento, validar que el email no esté en uso
                            .onErrorResume(throwable -> {
                                System.out.println("UseCase - Usuario no existe por documento, validando email");
                                return validarEmailNoEnUso(usuario)
                                        .then(Mono.defer(() -> {
                                            System.out.println("UseCase - Email disponible, registrando nuevo usuario");
                                            return usuarioGateway.registrarUsuario(usuario)
                                                    .doOnNext(usuarioRegistrado -> System.out.println("UseCase - Usuario registrado: idUsuario=" + usuarioRegistrado.getIdUsuario()));
                                        }));
                            });
                }))
                .flatMap(usuarioFinal -> registrarSolicitudParaUsuario(solicitudConDatos, usuarioFinal)
                        .map(solicitud -> new SolicitudConUsuarioResult(solicitud, usuarioFinal)));
    }

    private Mono<Void> validarEmailNoEnUso(Usuario usuario) {
        return usuarioGateway.buscarUsuarioPorEmail(usuario.getCorreoElectronico())
                .flatMap(usuarioExistente -> {
                    // Si encontramos un usuario con ese email, verificar si es el mismo documento
                    if (!usuarioExistente.getTipoDocumento().equals(usuario.getTipoDocumento()) ||
                        !usuarioExistente.getNumeroDocumento().equals(usuario.getNumeroDocumento())) {
                        return Mono.error(new EmailEnUsoException(
                                "El correo electrónico " + usuario.getCorreoElectronico() + 
                                " ya está registrado con un documento diferente"));
                    }
                    return Mono.empty();
                })
                .onErrorResume(throwable -> {
                    // Si no encontramos usuario con ese email, está disponible
                    if (throwable.getMessage() != null && 
                        (throwable.getMessage().contains("Usuario no encontrado") ||
                         throwable.getMessage().contains("404 Not Found"))) {
                        return Mono.empty();
                    }
                    // Si es otro tipo de error, lo propagamos
                    return Mono.error(throwable);
                })
                .then();
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
