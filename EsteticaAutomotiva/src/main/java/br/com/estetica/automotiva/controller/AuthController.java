package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final ClientService clients;

    public AuthController(ClientService clients) {
        this.clients = clients;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String registerForm(Model model) {
        model.addAttribute("client", new AppUser());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String register(
            @ModelAttribute("client") AppUser client,
            @RequestParam String rawPassword,
            @RequestParam String platesText,
            Model model
    ) {
        try {
            clients.registerClient(client, rawPassword, platesText);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "cadastro";
        }
    }
}
