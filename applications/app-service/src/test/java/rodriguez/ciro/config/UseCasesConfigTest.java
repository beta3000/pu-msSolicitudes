package rodriguez.ciro.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import rodriguez.ciro.model.solicitud.gateways.SolicitudRepository;
import rodriguez.ciro.model.tipoprestamo.gateways.TipoPrestamoRepository;
import rodriguez.ciro.model.usuario.gateways.UsuarioGateway;
import rodriguez.ciro.usecase.registrarsolicitud.RegistrarSolicitudUseCase;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' were found");
            
            // Verify that the specific use case bean can be retrieved
            RegistrarSolicitudUseCase useCase = context.getBean(RegistrarSolicitudUseCase.class);
            assertNotNull(useCase, "RegistrarSolicitudUseCase bean should be available");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public SolicitudRepository solicitudRepository() {
            return mock(SolicitudRepository.class);
        }

        @Bean
        public TipoPrestamoRepository tipoPrestamoRepository() {
            return mock(TipoPrestamoRepository.class);
        }

        @Bean
        public UsuarioGateway usuarioGateway() {
            return mock(UsuarioGateway.class);
        }
    }
}