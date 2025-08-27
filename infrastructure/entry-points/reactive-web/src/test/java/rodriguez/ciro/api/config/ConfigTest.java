package rodriguez.ciro.api.config;

import rodriguez.ciro.api.SolicitudController;
import rodriguez.ciro.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {SolicitudController.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @MockBean
    private RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void securityHeadersConfigurationShouldBePresent() {
        webTestClient.get()
                .uri("/api/v1/solicitud")
                .exchange()
                .expectStatus().is4xxClientError() // Should be 405 Method Not Allowed for GET on POST endpoint
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}
