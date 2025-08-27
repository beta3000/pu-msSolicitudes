package rodriguez.ciro.r2dbc.repository;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import rodriguez.ciro.r2dbc.entity.SolicitudEntity;

public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

}
