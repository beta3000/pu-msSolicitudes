package rodriguez.ciro.consumer;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import rodriguez.ciro.consumer.adapter.UsuarioRestAdapter;
import java.io.IOException;


class RestConsumerTest {

    private static UsuarioRestAdapter usuarioRestAdapter;

    private static MockWebServer mockBackEnd;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        // Este test necesita ser actualizado para usar UsuarioRestAdapter
        // o crear un test más apropiado para el contexto actual
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Test placeholder - needs implementation for UsuarioRestAdapter")
    void testPlaceholder() {
        // TODO: Implementar tests apropiados para UsuarioRestAdapter
        // Este test necesita ser reescrito para probar la funcionalidad real
        // del adaptador de usuarios
    }

}