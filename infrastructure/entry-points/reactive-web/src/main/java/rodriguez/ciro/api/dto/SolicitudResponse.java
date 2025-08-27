package rodriguez.ciro.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de la solicitud de préstamo registrada con información del usuario")
public class SolicitudResponse {

    @Schema(description = "Identificador único de la solicitud", example = "1")
    private Long idSolicitud;

    @Schema(description = "Monto solicitado del préstamo", example = "50000.00")
    private BigDecimal monto;

    @Schema(description = "Plazo del préstamo en meses", example = "12")
    private Integer plazo;

    @Schema(description = "Correo electrónico del cliente", example = "cliente@example.com")
    private String email;

    @Schema(description = "Identificador del tipo de préstamo", example = "1")
    private Long idTipoPrestamo;

    @Schema(description = "Identificador del estado actual", example = "1")
    private Long idEstado;

    @Schema(description = "Descripción del estado actual", example = "Pendiente de revisión")
    private String estado;

    // Información del usuario registrado
    @Schema(description = "Datos del usuario registrado")
    private UsuarioResponse usuario;
}
