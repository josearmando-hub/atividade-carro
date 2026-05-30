package br.com.estetica.automotiva.repository;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.model.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<AppUser> findByRole(Role role);
}
