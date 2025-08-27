package rodriguez.ciro.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos del usuario registrado")
public class UsuarioResponse {

    @Schema(description = "Identificador único del usuario registrado", example = "123")
    private Long idUsuario;

    @Schema(description = "Nombres completos del usuario", example = "Juan Carlos")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "García López")
    private String apellidos;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-05-15")
    private LocalDate fechaNacimiento;

    @Schema(description = "Dirección del usuario", example = "Calle 123 #45-67")
    private String direccion;

    @Schema(description = "Teléfono del usuario", example = "3001234567")
    private String telefono;

    @Schema(description = "Salario base del usuario", example = "3000000.00")
    private BigDecimal salarioBase;
}
