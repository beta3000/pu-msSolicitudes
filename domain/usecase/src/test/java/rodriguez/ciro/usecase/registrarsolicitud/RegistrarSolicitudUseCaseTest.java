package rodriguez.ciro.usecase.registrarsolicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rodriguez.ciro.model.solicitud.Solicitud;
import rodriguez.ciro.model.solicitud.gateways.SolicitudRepository;
import rodriguez.ciro.model.tipoprestamo.gateways.TipoPrestamoRepository;
import rodriguez.ciro.model.usuario.Rol;
import rodriguez.ciro.model.usuario.Usuario;
import rodriguez.ciro.model.usuario.gateways.UsuarioGateway;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarSolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private RegistrarSolicitudUseCase registrarSolicitudUseCase;

    private Usuario usuario;
    private Solicitud solicitud;
    private Usuario usuarioRegistrado;
    private Solicitud solicitudGuardada;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombres("Juan Carlos")
                .apellidos("García López")
                .tipoDocumento("CC")
                .numeroDocumento("12345678")
                .correoElectronico("juan@example.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .direccion("Calle 123 #45-67")
                .telefono("3001234567")
                .salarioBase(new BigDecimal("3000000"))
                .rol(Rol.builder().idRol(2L).build())
                .build();

        usuarioRegistrado = usuario.toBuilder()
                .idUsuario(1L)
                .build();

        solicitud = Solicitud.builder()
                .monto(new BigDecimal("1000000"))
                .plazo(12)
                .idTipoPrestamo(1L)
                .build();

        solicitudGuardada = solicitud.toBuilder()
                .idSolicitud(1L)
                .email("juan@example.com")
                .idEstado(1L)
                .build();
    }

    @Test
    void registrarSolicitud_DeberiaRegistrarSolicitudExitosamente() {
        // Given
        when(tipoPrestamoRepository.existePorId(1L)).thenReturn(Mono.just(true));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudGuardada));

        // When & Then
        StepVerifier.create(registrarSolicitudUseCase.registrarSolicitud(solicitud))
                .expectNextMatches(result -> 
                    result.getIdSolicitud().equals(1L) && 
                    result.getIdEstado().equals(1L))
                .verifyComplete();

        verify(tipoPrestamoRepository).existePorId(1L);
        verify(solicitudRepository).guardar(any(Solicitud.class));
    }

    @Test
    void registrarSolicitud_DeberiaFallarCuandoTipoPrestamoNoExiste() {
        // Given
        when(tipoPrestamoRepository.existePorId(1L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(registrarSolicitudUseCase.registrarSolicitud(solicitud))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("El tipo de préstamo con ID 1 no existe"))
                .verify();

        verify(tipoPrestamoRepository).existePorId(1L);
        verify(solicitudRepository, never()).guardar(any());
    }

    @Test
    void registrarSolicitudConUsuario_DeberiaRegistrarUsuarioYSolicitudExitosamente() {
        // Given
        when(tipoPrestamoRepository.existePorId(1L)).thenReturn(Mono.just(true));
        when(usuarioGateway.registrarUsuario(usuario)).thenReturn(Mono.just(usuarioRegistrado));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenReturn(Mono.just(solicitudGuardada));

        // When & Then
        StepVerifier.create(registrarSolicitudUseCase.registrarSolicitudConUsuario(usuario, solicitud))
                .expectNextMatches(result -> 
                    result.getUsuario().getIdUsuario().equals(1L) &&
                    result.getSolicitud().getIdSolicitud().equals(1L) &&
                    result.getSolicitud().getEmail().equals("juan@example.com"))
                .verifyComplete();

        verify(tipoPrestamoRepository).existePorId(1L);
        verify(usuarioGateway).registrarUsuario(usuario);
        verify(solicitudRepository).guardar(any(Solicitud.class));
    }

    @Test
    void registrarSolicitudConUsuario_DeberiaFallarCuandoTipoPrestamoNoExiste() {
        // Given
        when(tipoPrestamoRepository.existePorId(1L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(registrarSolicitudUseCase.registrarSolicitudConUsuario(usuario, solicitud))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("El tipo de préstamo con ID 1 no existe"))
                .verify();

        verify(tipoPrestamoRepository).existePorId(1L);
        verify(usuarioGateway, never()).registrarUsuario(any());
        verify(solicitudRepository, never()).guardar(any());
    }

    @Test
    void registrarSolicitudConUsuario_DeberiaFallarCuandoRegistroUsuarioFalla() {
        // Given
        when(tipoPrestamoRepository.existePorId(1L)).thenReturn(Mono.just(true));
        when(usuarioGateway.registrarUsuario(usuario)).thenReturn(Mono.error(new RuntimeException("Error al registrar usuario")));

        // When & Then
        StepVerifier.create(registrarSolicitudUseCase.registrarSolicitudConUsuario(usuario, solicitud))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Error al registrar usuario"))
                .verify();

        verify(tipoPrestamoRepository).existePorId(1L);
        verify(usuarioGateway).registrarUsuario(usuario);
        verify(solicitudRepository, never()).guardar(any());
    }
}