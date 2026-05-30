package br.com.estetica.automotiva.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class AppUser {
    @Id
    private String id;

    @NotBlank
    private String name;

    @Indexed(unique = true)
    @NotBlank
    private String cpf;

    @NotBlank
    private String phone;

    @Indexed(unique = true)
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotEmpty
    private List<String> vehiclePlates = new ArrayList<>();

    private Role role = Role.CLIENT;
    private boolean lgpdConsent;
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getVehiclePlates() {
        return vehiclePlates;
    }

    public void setVehiclePlates(List<String> vehiclePlates) {
        this.vehiclePlates = vehiclePlates;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isLgpdConsent() {
        return lgpdConsent;
    }

    public void setLgpdConsent(boolean lgpdConsent) {
        this.lgpdConsent = lgpdConsent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
