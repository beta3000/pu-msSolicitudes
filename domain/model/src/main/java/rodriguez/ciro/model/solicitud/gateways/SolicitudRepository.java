package rodriguez.ciro.model.solicitud.gateways;

import reactor.core.publisher.Mono;
import rodriguez.ciro.model.solicitud.Solicitud;

public interface SolicitudRepository {
    Mono<Solicitud> guardar(Solicitud solicitud);
}
