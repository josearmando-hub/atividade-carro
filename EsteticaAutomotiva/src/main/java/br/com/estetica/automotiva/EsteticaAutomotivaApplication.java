package br.com.estetica.automotiva;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.model.Role;
import br.com.estetica.automotiva.model.ServiceType;
import br.com.estetica.automotiva.repository.AppUserRepository;
import br.com.estetica.automotiva.repository.ServiceTypeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EsteticaAutomotivaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsteticaAutomotivaApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            AppUserRepository users,
            ServiceTypeRepository services,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (users.findByEmail("admin@estetica.com").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setName("Gestor do Sistema");
                admin.setEmail("admin@estetica.com");
                admin.setCpf("00000000000");
                admin.setPhone("(00) 00000-0000");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setLgpdConsent(true);
                admin.setCreatedAt(Instant.now());
                users.save(admin);
            }

            if (services.count() == 0) {
                services.saveAll(List.of(
                        new ServiceType(null, "Lavagem simples", "Limpeza externa com enxague, shampoo automotivo e secagem.", new BigDecimal("45.00"), true),
                        new ServiceType(null, "Lavagem completa", "Lavagem externa, limpeza interna, aspiracao e acabamento dos plasticos.", new BigDecimal("85.00"), true),
                        new ServiceType(null, "Lavagem com cera", "Lavagem completa com aplicacao de cera protetiva.", new BigDecimal("120.00"), true),
                        new ServiceType(null, "Lavagem detalhada", "Higienizacao detalhada interna e externa com acabamento premium.", new BigDecimal("220.00"), true)
                ));
            }
        };
    }
}
