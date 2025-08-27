package rodriguez.ciro.model.estado;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Estado {
    private Long idEstado;
    private String nombre;
    private String descripcion;
}
