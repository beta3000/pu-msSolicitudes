package rodriguez.ciro.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de la solicitud de préstamo incluyendo información del cliente")
public class RegistrarSolicitudRequest {

    // Información del préstamo
    @Schema(description = "Monto solicitado del préstamo", example = "50000.00", minimum = "0.01")
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Schema(description = "Plazo del préstamo en meses", example = "12", minimum = "1")
    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 1, message = "El plazo debe ser mayor a 0")
    private Integer plazo;

    @Schema(description = "Identificador del tipo de préstamo", example = "1")
    @NotNull(message = "El tipo de préstamo es obligatorio")
    private Long idTipoPrestamo;

    // Información del cliente
    @Schema(description = "Datos del usuario/cliente")
    @NotNull(message = "Los datos del usuario son obligatorios")
    @Valid
    private UsuarioRequest usuario;
}
