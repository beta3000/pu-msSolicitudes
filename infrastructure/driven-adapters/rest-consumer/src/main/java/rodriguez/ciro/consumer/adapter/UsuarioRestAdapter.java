package rodriguez.ciro.consumer.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import rodriguez.ciro.consumer.api.UsuariosApi;
import rodriguez.ciro.consumer.api.model.RegistrarUsuarioRequest;
import rodriguez.ciro.consumer.api.model.RolDto;
import rodriguez.ciro.consumer.api.model.UsuarioResponse;
import rodriguez.ciro.model.usuario.Rol;
import rodriguez.ciro.model.usuario.Usuario;
import rodriguez.ciro.model.usuario.gateways.UsuarioGateway;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsuarioRestAdapter implements UsuarioGateway {

    private final UsuariosApi usuariosApi;

    @Override
    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        log.debug("Registrando usuario en servicio externo: {}", usuario.getCorreoElectronico());

        return Mono.just(usuario)
                .map(this::mapToRegistrarUsuarioRequest)
                .flatMap(usuariosApi::registrarUsuarioRequest)
                .doOnNext(response -> log.info("Respuesta del servicio de usuarios: idUsuario={}, nombres={}", 
                    response.getIdUsuario(), response.getNombres()))
                .map(this::mapToUsuario)
                .doOnSuccess(u -> log.debug("Usuario registrado exitosamente con ID: {}", u.getIdUsuario()))
                .doOnError(error -> log.error("Error al registrar usuario: {}", error.getMessage()));
    }

    @Override
    public Mono<Usuario> buscarUsuarioPorDocumento(String tipoDocumento, String numeroDocumento) {
        log.debug("Buscando usuario por documento: {} - {}", tipoDocumento, numeroDocumento);

        return usuariosApi.buscarUsuarioPorDocumentoRequest(tipoDocumento, numeroDocumento)
                .doOnNext(response -> log.info("Usuario encontrado en servicio externo: idUsuario={}, nombres={}", 
                    response.getIdUsuario(), response.getNombres()))
                .map(this::mapToUsuario)
                .doOnSuccess(u -> log.debug("Usuario encontrado con ID: {}", u.getIdUsuario()))
                .doOnError(error -> log.error("Error al buscar usuario por documento: {}", error.getMessage()));
    }

    @Override
    public Mono<Usuario> buscarUsuarioPorEmail(String correoElectronico) {
        log.debug("Buscando usuario por email: {}", correoElectronico);

        return usuariosApi.buscarUsuarioPorEmailRequest(correoElectronico)
                .doOnNext(response -> log.info("Usuario encontrado por email en servicio externo: idUsuario={}, nombres={}", 
                    response.getIdUsuario(), response.getNombres()))
                .map(this::mapToUsuario)
                .doOnSuccess(u -> log.debug("Usuario encontrado por email con ID: {}", u.getIdUsuario()))
                .doOnError(error -> log.error("Error al buscar usuario por email: {}", error.getMessage()));
    }

    private RegistrarUsuarioRequest mapToRegistrarUsuarioRequest(Usuario usuario) {
        RegistrarUsuarioRequest request = new RegistrarUsuarioRequest();
        request.setNombres(usuario.getNombres());
        request.setApellidos(usuario.getApellidos());
        request.setTipoDocumento(usuario.getTipoDocumento());
        request.setNumeroDocumento(usuario.getNumeroDocumento());
        request.setFechaNacimiento(usuario.getFechaNacimiento());
        request.setDireccion(usuario.getDireccion());
        request.setTelefono(usuario.getTelefono());
        request.setCorreoElectronico(usuario.getCorreoElectronico());
        request.setSalarioBase(usuario.getSalarioBase());

        if (usuario.getRol() != null) {
            RolDto rolDto = new RolDto();
            rolDto.setIdRol(usuario.getRol().getIdRol());
            rolDto.setNombre(usuario.getRol().getNombre());
            request.setRol(rolDto);
        }

        return request;
    }

    private Usuario mapToUsuario(UsuarioResponse response) {
        log.info("Mapeando UsuarioResponse: idUsuario raw={} (tipo={})", 
            response.getIdUsuario(), 
            response.getIdUsuario() != null ? response.getIdUsuario().getClass().getSimpleName() : "null");
        
        Long idUsuarioMapeado = mapToLong(response.getIdUsuario());
        log.info("idUsuario después del mapeo: {}", idUsuarioMapeado);
        
        return Usuario.builder()
                .idUsuario(idUsuarioMapeado)
                .nombres(mapToString(response.getNombres()))
                .apellidos(mapToString(response.getApellidos()))
                .tipoDocumento(mapToString(response.getTipoDocumento()))
                .numeroDocumento(mapToString(response.getNumeroDocumento()))
                .fechaNacimiento(mapToLocalDate(response.getFechaNacimiento()))
                .direccion(mapToString(response.getDireccion()))
                .telefono(mapToString(response.getTelefono()))
                .correoElectronico(mapToString(response.getCorreoElectronico()))
                .salarioBase(mapToBigDecimal(response.getSalarioBase()))
                .rol(mapToRol(response.getRol()))
                .build();
    }

    private Rol mapToRol(RolDto rolDto) {
        if (rolDto == null) {
            return null;
        }
        return Rol.builder()
                .idRol(mapToLong(rolDto.getIdRol()))
                .nombre(mapToString(rolDto.getNombre()))
                .build();
    }

    private Long mapToLong(Object obj) {
        if (obj == null) {
            log.info("mapToLong: objeto es null");
            return null;
        }
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        if (obj instanceof String) {
            try {
                return Long.valueOf((String) obj);
            } catch (NumberFormatException e) {
                log.info("Error al convertir string a Long: {}", obj);
                return null;
            }
        }
        log.info("Tipo no soportado para mapToLong: {} (valor={}, tipo={})", obj, obj, obj.getClass().getSimpleName());
        return null;
    }

    private String mapToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private LocalDate mapToLocalDate(Object obj) {
        if (obj == null) return null;
        if (obj instanceof LocalDate) return (LocalDate) obj;
        if (obj instanceof String) return LocalDate.parse((String) obj);
        return null;
    }

    private BigDecimal mapToBigDecimal(Object obj) {
        if (obj == null) return null;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return BigDecimal.valueOf(((Number) obj).doubleValue());
        if (obj instanceof String) return new BigDecimal((String) obj);
        return null;
    }
}
