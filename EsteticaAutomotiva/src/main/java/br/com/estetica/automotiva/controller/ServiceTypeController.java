package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.ServiceType;
import br.com.estetica.automotiva.repository.ServiceTypeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/servicos")
public class ServiceTypeController {
    private final ServiceTypeRepository services;

    public ServiceTypeController(ServiceTypeRepository services) {
        this.services = services;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", services.findAll());
        model.addAttribute("service", new ServiceType());
        return "servicos/lista";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("service") ServiceType service, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("services", services.findAll());
            return "servicos/lista";
        }
        services.save(service);
        return "redirect:/servicos";
    }

    @PostMapping("/{id}/alternar")
    public String toggle(@PathVariable String id) {
        ServiceType service = services.findById(id).orElseThrow();
        service.setActive(!service.isActive());
        services.save(service);
        return "redirect:/servicos";
    }
}
