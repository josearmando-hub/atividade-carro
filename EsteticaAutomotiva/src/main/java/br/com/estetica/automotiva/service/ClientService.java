package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.AppUser;
import br.com.estetica.automotiva.model.Role;
import br.com.estetica.automotiva.repository.AppUserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public ClientService(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerClient(AppUser client, String rawPassword, String platesText) {
        validateRequired(client, rawPassword, platesText);
        if (!client.isLgpdConsent()) {
            throw new IllegalArgumentException("E necessario aceitar os termos de privacidade e LGPD.");
        }
        if (users.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("E-mail ja cadastrado.");
        }
        if (users.existsByCpf(client.getCpf())) {
            throw new IllegalArgumentException("CPF ja cadastrado.");
        }
        client.setRole(Role.CLIENT);
        client.setPassword(passwordEncoder.encode(rawPassword));
        client.setVehiclePlates(parsePlates(platesText));
        client.setCreatedAt(Instant.now());
        return users.save(client);
    }

    public List<AppUser> listClients() {
        return users.findByRole(Role.CLIENT);
    }

    public AppUser findByEmail(String email) {
        return users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
    }

    public AppUser findById(String id) {
        return users.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
    }

    public AppUser updateClient(String id, AppUser form, String platesText) {
        AppUser client = findById(id);
        if (form.getName() == null || form.getName().isBlank() || form.getPhone() == null || form.getPhone().isBlank()) {
            throw new IllegalArgumentException("Nome e telefone sao obrigatorios.");
        }
        client.setName(form.getName());
        client.setPhone(form.getPhone());
        client.setVehiclePlates(parsePlates(platesText));
        return users.save(client);
    }

    public void deleteClient(String id) {
        users.deleteById(id);
    }

    public String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 5) {
            return "***";
        }
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() < 11) {
            return "***";
        }
        return "***." + digits.substring(3, 6) + ".***-" + digits.substring(9);
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@", 2);
        String local = parts[0].length() <= 2 ? "**" : parts[0].substring(0, 2) + "***";
        return local + "@" + parts[1];
    }

    public List<String> parsePlates(String platesText) {
        if (platesText == null || platesText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(platesText.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(String::toUpperCase)
                .toList();
    }

    private void validateRequired(AppUser client, String rawPassword, String platesText) {
        if (client.getName() == null || client.getName().isBlank()
                || client.getCpf() == null || client.getCpf().isBlank()
                || client.getPhone() == null || client.getPhone().isBlank()
                || client.getEmail() == null || client.getEmail().isBlank()
                || rawPassword == null || rawPassword.length() < 6
                || platesText == null || platesText.isBlank()) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatorios.");
        }
    }
}
