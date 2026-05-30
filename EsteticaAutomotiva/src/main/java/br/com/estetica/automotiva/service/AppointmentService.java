package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.model.Appointment;
import br.com.estetica.automotiva.model.AppointmentStatus;
import br.com.estetica.automotiva.model.ServiceType;
import br.com.estetica.automotiva.repository.AppointmentRepository;
import br.com.estetica.automotiva.repository.ServiceTypeRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final ServiceTypeRepository serviceTypes;

    public AppointmentService(AppointmentRepository appointments, ServiceTypeRepository serviceTypes) {
        this.appointments = appointments;
        this.serviceTypes = serviceTypes;
    }

    public Appointment schedule(AppUser client, String serviceTypeId, LocalDate date, LocalTime time) {
        validateFutureSlot(date, time);
        ensureAvailable(date, time, null);

        ServiceType service = serviceTypes.findById(serviceTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de servico nao encontrado."));
        Appointment appointment = new Appointment();
        appointment.setClientId(client.getId());
        appointment.setClientName(client.getName());
        appointment.setServiceTypeId(service.getId());
        appointment.setServiceName(service.getName());
        appointment.setServicePrice(service.getCurrentPrice());
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setCreatedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());
        return appointments.save(appointment);
    }

    public Appointment update(String id, String serviceTypeId, LocalDate date, LocalTime time, AppUser requester, boolean admin) {
        Appointment appointment = findAllowed(id, requester, admin);
        validateFutureSlot(date, time);
        ensureAvailable(date, time, id);
        ServiceType service = serviceTypes.findById(serviceTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de servico nao encontrado."));
        appointment.setServiceTypeId(service.getId());
        appointment.setServiceName(service.getName());
        appointment.setServicePrice(service.getCurrentPrice());
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setUpdatedAt(Instant.now());
        return appointments.save(appointment);
    }

    public void cancel(String id, AppUser requester, boolean admin) {
        Appointment appointment = findAllowed(id, requester, admin);
        appointment.setStatus(AppointmentStatus.CANCELED);
        appointment.setUpdatedAt(Instant.now());
        appointments.save(appointment);
    }

    public Appointment findAllowed(String id, AppUser requester, boolean admin) {
        Appointment appointment = appointments.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento nao encontrado."));
        if (!admin && !appointment.getClientId().equals(requester.getId())) {
            throw new IllegalArgumentException("Acesso negado ao agendamento.");
        }
        return appointment;
    }

    public List<Appointment> agenda(LocalDate start, LocalDate end) {
        return appointments.findByDateBetweenOrderByDateAscTimeAsc(start, end);
    }

    public List<Appointment> clientAgenda(String clientId) {
        return appointments.findByClientIdOrderByDateDescTimeDesc(clientId);
    }

    private void ensureAvailable(LocalDate date, LocalTime time, String currentId) {
        boolean taken = appointments.findByDateBetweenOrderByDateAscTimeAsc(date, date).stream()
                .filter(item -> item.getStatus() == AppointmentStatus.SCHEDULED)
                .filter(item -> currentId == null || !item.getId().equals(currentId))
                .anyMatch(item -> item.getTime().equals(time));
        if (taken) {
            throw new IllegalArgumentException("Horario indisponivel. Escolha outro horario.");
        }
    }

    private void validateFutureSlot(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            throw new IllegalArgumentException("Informe data e hora.");
        }
        if (date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now()))) {
            throw new IllegalArgumentException("O agendamento deve ser feito para um horario futuro.");
        }
    }
}
