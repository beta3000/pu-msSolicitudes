package rodriguez.ciro.consumer.api;

import rodriguez.ciro.consumer.api.model.RegistrarUsuarioRequest;
import rodriguez.ciro.consumer.api.model.UsuarioResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;

@Log4j2
@AllArgsConstructor
@Service
public class UsuariosApi {
    private final WebClient client;

    /**
     * Build call for registrarUsuario
     *
     * @param body (required)
     * @return Mono<UsuarioResponse> response
     */
    public Mono<UsuarioResponse> registrarUsuarioRequest(RegistrarUsuarioRequest body) {
        return client.method(HttpMethod.POST)
                .uri("/api/v1/usuarios")
                .contentType(MediaType.parseMediaType("application/json"))
                .body(BodyInserters.fromValue(body))
                .accept(MediaType.parseMediaType("application/json"))
                .retrieve()
                .bodyToMono(UsuarioResponse.class);
    }
}
