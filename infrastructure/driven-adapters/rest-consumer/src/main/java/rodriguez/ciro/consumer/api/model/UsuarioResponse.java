package rodriguez.ciro.consumer.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
* UsuarioResponse
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long idUsuario = null;
    private String nombres = null;
    private String apellidos = null;
    private String tipoDocumento = null;
    private String numeroDocumento = null;
    private LocalDate fechaNacimiento = null;
    private String direccion = null;
    private String telefono = null;
    private String correoElectronico = null;
    private BigDecimal salarioBase = null;
    private RolDto rol = null;
}