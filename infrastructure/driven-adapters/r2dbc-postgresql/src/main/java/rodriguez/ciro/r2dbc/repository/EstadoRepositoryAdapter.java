package rodriguez.ciro.r2dbc.repository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rodriguez.ciro.model.estado.Estado;
import rodriguez.ciro.model.estado.gateways.EstadoRepository;
import rodriguez.ciro.r2dbc.entity.EstadoEntity;
import rodriguez.ciro.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EstadoRepositoryAdapter extends ReactiveAdapterOperations<
        Estado,
        EstadoEntity,
        Long,
        EstadoReactiveRepository
        > implements EstadoRepository {
    public EstadoRepositoryAdapter(EstadoReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Estado.class));
    }

    @Override
    public Mono<Boolean> existePorId(Long idEstado) {
        log.debug("existePorId {}", idEstado);
        if (idEstado == null) {
            return Mono.just(false);
        }
        return repository.existsById(idEstado);
    }
}
