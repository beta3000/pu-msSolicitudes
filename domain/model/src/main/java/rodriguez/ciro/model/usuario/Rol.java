package rodriguez.ciro.model.usuario;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private Long idRol;
    private String nombre;
}
