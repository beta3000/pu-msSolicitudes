package rodriguez.ciro.model.tipoprestamo.gateways;

import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<Boolean> existePorId(Long idTipoPrestamo);
}
