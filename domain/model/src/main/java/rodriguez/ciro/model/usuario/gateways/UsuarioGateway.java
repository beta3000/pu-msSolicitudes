package rodriguez.ciro.model.usuario.gateways;

import reactor.core.publisher.Mono;
import rodriguez.ciro.model.usuario.Usuario;

public interface UsuarioGateway {
    Mono<Usuario> registrarUsuario(Usuario usuario);
    
    Mono<Usuario> buscarUsuarioPorDocumento(String tipoDocumento, String numeroDocumento);
    
    Mono<Usuario> buscarUsuarioPorEmail(String correoElectronico);
}
