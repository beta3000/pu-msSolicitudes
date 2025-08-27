package rodriguez.ciro.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import rodriguez.ciro.r2dbc.entity.EstadoEntity;
import rodriguez.ciro.r2dbc.repository.EstadoReactiveRepository;
import rodriguez.ciro.r2dbc.repository.EstadoRepositoryAdapter;
import rodriguez.ciro.model.estado.Estado;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    EstadoRepositoryAdapter repositoryAdapter;

    @Mock
    EstadoReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {
        EstadoEntity entity = EstadoEntity.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();
        Estado estado = Estado.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Estado.class)).thenReturn(estado);

        Mono<Estado> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdEstado().equals(1L))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        EstadoEntity entity = EstadoEntity.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();
        Estado estado = Estado.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();

        when(repository.findAll()).thenReturn(Flux.just(entity));
        when(mapper.map(entity, Estado.class)).thenReturn(estado);

        Flux<Estado> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdEstado().equals(1L))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        EstadoEntity entity = EstadoEntity.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();
        Estado estadoExample = Estado.builder()
                .nombre("test")
                .build();
        Estado estado = Estado.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();
        EstadoEntity entityExample = EstadoEntity.builder()
                .nombre("test")
                .build();

        when(mapper.map(estadoExample, EstadoEntity.class)).thenReturn(entityExample);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        when(mapper.map(entity, Estado.class)).thenReturn(estado);

        Flux<Estado> result = repositoryAdapter.findByExample(estadoExample);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdEstado().equals(1L))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        Estado estado = Estado.builder()
                .nombre("test")
                .descripcion("test description")
                .build();
        EstadoEntity entityToSave = EstadoEntity.builder()
                .nombre("test")
                .descripcion("test description")
                .build();
        EstadoEntity savedEntity = EstadoEntity.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();
        Estado savedEstado = Estado.builder()
                .idEstado(1L)
                .nombre("test")
                .descripcion("test description")
                .build();

        when(mapper.map(estado, EstadoEntity.class)).thenReturn(entityToSave);
        when(repository.save(entityToSave)).thenReturn(Mono.just(savedEntity));
        when(mapper.map(savedEntity, Estado.class)).thenReturn(savedEstado);

        Mono<Estado> result = repositoryAdapter.save(estado);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getIdEstado().equals(1L))
                .verifyComplete();
    }
}
