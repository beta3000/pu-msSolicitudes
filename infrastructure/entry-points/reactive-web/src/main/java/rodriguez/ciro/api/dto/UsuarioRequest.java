package rodriguez.ciro.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos del usuario/cliente")
public class UsuarioRequest {

    @Schema(description = "Nombres del cliente", example = "Juan Carlos")
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @Schema(description = "Apellidos del cliente", example = "García López")
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @Schema(description = "Tipo de documento del cliente", example = "CC")
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @Schema(description = "Número de documento del cliente", example = "12345678")
    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    @Schema(description = "Correo electrónico del cliente", example = "cliente@example.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String correoElectronico;

    @Schema(description = "Fecha de nacimiento del cliente", example = "1990-05-15")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @Schema(description = "Dirección del cliente", example = "Calle 123 #45-67")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Schema(description = "Teléfono del cliente", example = "3001234567")
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Schema(description = "Salario base del cliente", example = "3000000.00", minimum = "0")
    @NotNull(message = "El salario base es obligatorio")
    @DecimalMin(value = "0", message = "El salario base debe ser mayor o igual a 0")
    private BigDecimal salarioBase;

    @Schema(description = "Identificador del rol del cliente", example = "2")
    @NotNull(message = "El rol es obligatorio")
    private Long idRol;
}
