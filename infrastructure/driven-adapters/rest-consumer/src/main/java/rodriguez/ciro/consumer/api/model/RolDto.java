package rodriguez.ciro.consumer.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RolDto
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolDto {
    private Long idRol = null;
    private String nombre = null;
    private String descripcion = null;
}