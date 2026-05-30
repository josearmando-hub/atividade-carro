package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.model.Appointment;
import br.com.estetica.automotiva.model.Role;
import br.com.estetica.automotiva.repository.ServiceTypeRepository;
import br.com.estetica.automotiva.service.AppointmentService;
import br.com.estetica.automotiva.service.ClientService;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppointmentController {
    private final AppointmentService appointments;
    private final ServiceTypeRepository services;
    private final ClientService clients;

    public AppointmentController(AppointmentService appointments, ServiceTypeRepository services, ClientService clients) {
        this.appointments = appointments;
        this.services = services;
        this.clients = clients;
    }

    @GetMapping("/meus-agendamentos")
    public String myAppointments(Authentication authentication, Model model) {
        AppUser client = currentUser(authentication);
        model.addAttribute("appointments", appointments.clientAgenda(client.getId()));
        return "agenda/meus";
    }

    @GetMapping("/agenda")
    public String agenda(
            @RequestParam(defaultValue = "semana") String visao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model
    ) {
        LocalDate base = data == null ? LocalDate.now() : data;
        LocalDate start = visao.equals("dia") ? base : base.minusDays(base.getDayOfWeek().getValue() - 1);
        LocalDate end = visao.equals("dia") ? base : start.plusDays(6);
        model.addAttribute("appointments", appointments.agenda(start, end));
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("visao", visao);
        return "agenda/gestor";
    }

    @GetMapping("/agendamentos/novo")
    public String newForm(Model model) {
        model.addAttribute("services", services.findByActiveTrueOrderByNameAsc());
        model.addAttribute("appointment", new Appointment());
        return "agenda/form";
    }

    @PostMapping("/agendamentos")
    public String schedule(
            @RequestParam String serviceTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            Authentication authentication,
            Model model
    ) {
        try {
            appointments.schedule(currentUser(authentication), serviceTypeId, date, time);
            return "redirect:/meus-agendamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("services", services.findByActiveTrueOrderByNameAsc());
            return "agenda/form";
        }
    }

    @GetMapping("/agendamentos/{id}/editar")
    public String editForm(@PathVariable String id, Authentication authentication, Model model) {
        AppUser requester = currentUser(authentication);
        boolean admin = requester.getRole() == Role.ADMIN;
        model.addAttribute("appointment", appointments.findAllowed(id, requester, admin));
        model.addAttribute("services", services.findByActiveTrueOrderByNameAsc());
        return "agenda/edit";
    }

    @PostMapping("/agendamentos/{id}/editar")
    public String update(
            @PathVariable String id,
            @RequestParam String serviceTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            Authentication authentication,
            Model model
    ) {
        AppUser requester = currentUser(authentication);
        boolean admin = requester.getRole() == Role.ADMIN;
        try {
            appointments.update(id, serviceTypeId, date, time, requester, admin);
            return admin ? "redirect:/agenda" : "redirect:/meus-agendamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("appointment", appointments.findAllowed(id, requester, admin));
            model.addAttribute("services", services.findByActiveTrueOrderByNameAsc());
            return "agenda/edit";
        }
    }

    @PostMapping("/agendamentos/{id}/cancelar")
    public String cancel(@PathVariable String id, Authentication authentication) {
        AppUser requester = currentUser(authentication);
        boolean admin = requester.getRole() == Role.ADMIN;
        appointments.cancel(id, requester, admin);
        return admin ? "redirect:/agenda" : "redirect:/meus-agendamentos";
    }

    private AppUser currentUser(Authentication authentication) {
        return clients.findByEmail(authentication.getName());
    }
}
