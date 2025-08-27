package rodriguez.ciro.r2dbc.repository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rodriguez.ciro.model.solicitud.Solicitud;
import rodriguez.ciro.model.solicitud.gateways.SolicitudRepository;
import rodriguez.ciro.r2dbc.entity.SolicitudEntity;
import rodriguez.ciro.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        Long,
        SolicitudReactiveRepository
        > implements SolicitudRepository {

    public SolicitudRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
    }

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        log.debug("Guardando solicitud en base de datos");
        return Mono.just(solicitud)
                .map(s -> mapper.map(s, SolicitudEntity.class))
                .flatMap(repository::save)
                .map(solicitudData -> mapper.map(solicitudData, Solicitud.class))
                .doOnSuccess(s -> log.debug("Solicitud guardada exitosamente con ID: {}", s.getIdSolicitud()));
    }
}
