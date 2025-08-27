package rodriguez.ciro.model.estado.gateways;

import reactor.core.publisher.Mono;

public interface EstadoRepository {
    Mono<Boolean> existePorId(Long idEstado);
}
