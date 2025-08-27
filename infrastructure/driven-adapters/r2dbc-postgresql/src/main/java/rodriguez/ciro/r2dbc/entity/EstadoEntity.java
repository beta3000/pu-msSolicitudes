package rodriguez.ciro.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("estados")
public class EstadoEntity {
    @Id
    @Column("id_estado")
    private Long idEstado;
    @Column("nombre")
    private String nombre;
    @Column("descripcion")
    private String descripcion;
}
