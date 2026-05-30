package br.com.estetica.automotiva.repository;

import br.com.estetica.automotiva.model.Appointment;
import br.com.estetica.automotiva.model.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    boolean existsByDateAndTimeAndStatus(LocalDate date, LocalTime time, AppointmentStatus status);

    List<Appointment> findByDateBetweenOrderByDateAscTimeAsc(LocalDate start, LocalDate end);

    List<Appointment> findByClientIdOrderByDateDescTimeDesc(String clientId);
}
