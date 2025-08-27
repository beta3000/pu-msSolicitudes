package rodriguez.ciro.r2dbc.repository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rodriguez.ciro.model.tipoprestamo.TipoPrestamo;
import rodriguez.ciro.model.tipoprestamo.gateways.TipoPrestamoRepository;
import rodriguez.ciro.r2dbc.entity.TipoPrestamoEntity;
import rodriguez.ciro.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        Long,
        TipoPrestamoReactiveRepository
        > implements TipoPrestamoRepository {

    public TipoPrestamoRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

    @Override
    public Mono<Boolean> existePorId(Long idTipoPrestamo) {
        log.debug("existePorId {}", idTipoPrestamo);
        if (idTipoPrestamo == null) {
            return Mono.just(false);
        }
        return repository.existsById(idTipoPrestamo);
    }
}
