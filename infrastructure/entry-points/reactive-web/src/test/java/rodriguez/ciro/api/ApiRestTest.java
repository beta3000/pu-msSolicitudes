package rodriguez.ciro.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import rodriguez.ciro.api.dto.RegistrarSolicitudRequest;
import rodriguez.ciro.api.dto.UsuarioRequest;
import rodriguez.ciro.api.exception.GlobalExceptionHandler;
import rodriguez.ciro.model.exception.EmailEnUsoException;
import rodriguez.ciro.model.solicitud.Solicitud;
import rodriguez.ciro.model.usuario.Usuario;
import rodriguez.ciro.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {SolicitudController.class, GlobalExceptionHandler.class})
@WebFluxTest
class ApiRestTest {

    @MockBean
    private RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testRegistrarSolicitud() {
        RegistrarSolicitudUseCase.SolicitudConUsuarioResult mockResult = 
            new RegistrarSolicitudUseCase.SolicitudConUsuarioResult(
                Solicitud.builder()
                    .idSolicitud(1L)
                    .monto(new BigDecimal("10000"))
                    .plazo(12)
                    .email("test@example.com")
                    .idTipoPrestamo(1L)
                    .idEstado(1L)
                    .build(),
                Usuario.builder()
                    .idUsuario(1L)
                    .nombres("Test")
                    .apellidos("User")
                    .correoElectronico("test@example.com")
                    .build()
            );

        when(registrarSolicitudUseCase.registrarSolicitudConUsuario(any(Usuario.class), any(Solicitud.class)))
            .thenReturn(Mono.just(mockResult));

        RegistrarSolicitudRequest request = new RegistrarSolicitudRequest();
        request.setMonto(new BigDecimal("10000"));
        request.setPlazo(12);
        request.setIdTipoPrestamo(1L);
        
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNombres("Test");
        usuarioRequest.setApellidos("User");
        usuarioRequest.setCorreoElectronico("test@example.com");
        usuarioRequest.setTipoDocumento("CC");
        usuarioRequest.setNumeroDocumento("123456789");
        usuarioRequest.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuarioRequest.setDireccion("Test Address");
        usuarioRequest.setTelefono("1234567890");
        usuarioRequest.setSalarioBase(new BigDecimal("2000"));
        usuarioRequest.setIdRol(1L);
        
        request.setUsuario(usuarioRequest);

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testRegistrarSolicitudConEmailEnUso() {
        when(registrarSolicitudUseCase.registrarSolicitudConUsuario(any(Usuario.class), any(Solicitud.class)))
            .thenReturn(Mono.error(new EmailEnUsoException("El correo electrónico test@example.com ya está registrado con un documento diferente")));

        RegistrarSolicitudRequest request = new RegistrarSolicitudRequest();
        request.setMonto(new BigDecimal("10000"));
        request.setPlazo(12);
        request.setIdTipoPrestamo(1L);
        
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNombres("Test");
        usuarioRequest.setApellidos("User");
        usuarioRequest.setCorreoElectronico("test@example.com");
        usuarioRequest.setTipoDocumento("CC");
        usuarioRequest.setNumeroDocumento("123456789");
        usuarioRequest.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuarioRequest.setDireccion("Test Address");
        usuarioRequest.setTelefono("1234567890");
        usuarioRequest.setSalarioBase(new BigDecimal("2000"));
        usuarioRequest.setIdRol(1L);
        
        request.setUsuario(usuarioRequest);

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("test@example.com ya está registrado"))
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/api/v1/solicitud");
    }

    @Test
    void testRegistrarSolicitudConTipoPrestamoInvalido() {
        when(registrarSolicitudUseCase.registrarSolicitudConUsuario(any(Usuario.class), any(Solicitud.class)))
            .thenReturn(Mono.error(new IllegalArgumentException("El tipo de préstamo con ID 99 no existe")));

        RegistrarSolicitudRequest request = new RegistrarSolicitudRequest();
        request.setMonto(new BigDecimal("10000"));
        request.setPlazo(12);
        request.setIdTipoPrestamo(99L);
        
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNombres("Test");
        usuarioRequest.setApellidos("User");
        usuarioRequest.setCorreoElectronico("test@example.com");
        usuarioRequest.setTipoDocumento("CC");
        usuarioRequest.setNumeroDocumento("123456789");
        usuarioRequest.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuarioRequest.setDireccion("Test Address");
        usuarioRequest.setTelefono("1234567890");
        usuarioRequest.setSalarioBase(new BigDecimal("2000"));
        usuarioRequest.setIdRol(1L);
        
        request.setUsuario(usuarioRequest);

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("tipo de préstamo con ID 99 no existe"))
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/api/v1/solicitud");
    }

}
