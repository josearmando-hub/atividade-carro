package br.com.estetica.automotiva.controller;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/clientes")
public class ClientController {
    private final ClientService clients;

    public ClientController(ClientService clients) {
        this.clients = clients;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clients.listClients());
        model.addAttribute("clientService", clients);
        return "clientes/lista";
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable String id, Model model) {
        AppUser client = clients.findById(id);
        model.addAttribute("client", client);
        model.addAttribute("platesText", String.join(", ", client.getVehiclePlates()));
        return "clientes/form";
    }

    @PostMapping("/{id}/editar")
    public String update(
            @PathVariable String id,
            @ModelAttribute("client") AppUser client,
            @RequestParam String platesText
    ) {
        clients.updateClient(id, client, platesText);
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String delete(@PathVariable String id) {
        clients.deleteClient(id);
        return "redirect:/clientes";
    }
}
