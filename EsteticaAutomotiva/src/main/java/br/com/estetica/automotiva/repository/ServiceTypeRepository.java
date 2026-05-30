package br.com.estetica.automotiva.repository;

import br.com.estetica.automotiva.model.ServiceType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServiceTypeRepository extends MongoRepository<ServiceType, String> {
    List<ServiceType> findByActiveTrueOrderByNameAsc();
}
