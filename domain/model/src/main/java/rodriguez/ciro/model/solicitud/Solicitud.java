package rodriguez.ciro.model.solicitud;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private Long idSolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Long idEstado;
    private Long idTipoPrestamo;
}
