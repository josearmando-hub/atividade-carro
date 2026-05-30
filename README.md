# Estetica Automotiva
<img width="761" height="1308" alt="imagem (4)" src="https://github.com/user-attachments/assets/b4bf617c-eb3b-4710-85d6-e47b4ac31aff" />
<img width="1174" height="632" alt="imagem (3)" src="https://github.com/user-attachments/assets/a7f94ad9-36f1-46a4-9292-45c1607aaa80" />
<img width="1082" height="703" alt="imagem (2)" src="https://github.com/user-attachments/assets/b9aced50-43f3-484b-9514-38f4a8f27645" />


Aplicacao Java Web para controle de clientes e agendamento de servicos em uma estetica automotiva. O projeto usa Maven, Spring Boot, Spring Security, Thymeleaf e MongoDB, podendo ser conectado ao MongoDB Atlas.

## Funcionalidades

- Login completo com Spring Security e senhas criptografadas com BCrypt.
- Cadastro publico de cliente com aceite LGPD.
- Gestao administrativa de clientes com edicao, exclusao definitiva e listagem com dados sensiveis mascarados.
- Cadastro e listagem de tipos de servico com nome, descricao detalhada, preco vigente e status ativo/inativo.
- Agendamento autonomo pelo cliente com validacao de disponibilidade.
- Agenda diaria ou semanal para o gestor.
- Edicao e cancelamento de agendamentos pelo cliente ou gestor.

## Tecnologias

- Java 17
- Maven
- Spring Boot 3
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- MongoDB Atlas

## Como executar no IntelliJ IDEA

1. Abra a pasta do projeto no IntelliJ IDEA.
2. Aguarde o Maven baixar as dependencias.
3. Configure a variavel de ambiente `MONGODB_URI` com a connection string do MongoDB Atlas.
4. Execute a classe `br.com.estetica.automotiva.EsteticaAutomotivaApplication`.
5. Acesse `http://localhost:8080`.

Exemplo de variavel:

```bash
MONGODB_URI=mongodb+srv://usuario:senha@cluster.mongodb.net/estetica_automotiva?retryWrites=true&w=majority
```

## Como criar o banco de dados no MongoDB Atlas

1. Acesse o site do MongoDB Atlas e entre na sua conta.
2. Crie um projeto, por exemplo `Estetica Automotiva`.
3. Crie um cluster gratuito ou utilize um cluster existente.
4. Em **Database Access**, crie um usuario de banco de dados com usuario e senha.
5. Em **Network Access**, libere o acesso do seu IP atual.
6. Clique em **Connect** no cluster.
7. Escolha **Drivers** e copie a connection string.
8. Substitua `<username>`, `<password>` e o nome do banco pela configuracao do projeto.

Exemplo:

```bash
mongodb+srv://meuUsuario:minhaSenha@cluster0.xxxxx.mongodb.net/estetica_automotiva?retryWrites=true&w=majority
```

Nao e necessario criar manualmente as colecoes. Ao rodar a aplicacao, o Spring Data MongoDB cria e utiliza automaticamente:

- `users`
- `service_types`
- `appointments`

Na primeira execucao, a aplicacao tambem cria:

- usuario gestor inicial `admin@estetica.com`
- senha inicial `admin123`
- servicos padrao de lavagem

## Como configurar o MongoDB Atlas no IntelliJ

1. No IntelliJ, abra a classe `EsteticaAutomotivaApplication`.
2. Clique na seta ao lado do botao de executar.
3. Acesse **Edit Configurations**.
4. No campo **Environment variables**, adicione:

```bash
MONGODB_URI=mongodb+srv://meuUsuario:minhaSenha@cluster0.xxxxx.mongodb.net/estetica_automotiva?retryWrites=true&w=majority
```

5. Salve e execute novamente o projeto.

Se nao configurar essa variavel, o sistema tentara usar um MongoDB local em:

```bash
mongodb://localhost:27017/estetica_automotiva
```

## Como compilar e executar pelo terminal

```bash
mvn clean package
mvn spring-boot:run
```

## Usuario gestor inicial

- E-mail: `admin@estetica.com`
- Senha: `admin123`

Altere essa senha apos o primeiro acesso em uma evolucao de producao.

## Diagramas UML

### Diagrama de Classes

```mermaid
classDiagram
    namespace model {
        class AppUser {
            +String id
            +String name
            +String cpf
            +String phone
            +String email
            +String password
            +List~String~ vehiclePlates
            +Role role
            +boolean lgpdConsent
        }
        class ServiceType {
            +String id
            +String name
            +String description
            +BigDecimal currentPrice
            +boolean active
        }
        class Appointment {
            +String id
            +String clientId
            +String serviceTypeId
            +LocalDate date
            +LocalTime time
            +AppointmentStatus status
        }
    }

    namespace repository {
        class AppUserRepository
        class ServiceTypeRepository
        class AppointmentRepository
    }

    namespace service {
        class ClientService
        class AppointmentService
    }

    AppUser "1" --> "0..*" Appointment : realiza
    ServiceType "1" --> "0..*" Appointment : define
    ClientService --> AppUserRepository
    AppointmentService --> AppointmentRepository
    AppointmentService --> ServiceTypeRepository
```

### Diagrama de Sequencia - Agendamento

```mermaid
sequenceDiagram
    actor Cliente
    participant View as View Thymeleaf
    participant Controller as AppointmentController
    participant Service as AppointmentService
    participant Repo as Mongo Repositories
    participant Mongo as MongoDB Atlas

    Cliente->>View: Seleciona servico, data e hora
    View->>Controller: POST /agendamentos
    Controller->>Service: schedule(cliente, servico, data, hora)
    Service->>Repo: Consulta servico e agenda do dia
    Repo->>Mongo: find service / find appointments
    Mongo-->>Repo: Dados encontrados
    Repo-->>Service: Retorno da consulta
    Service->>Service: Valida horario futuro e conflito
    Service->>Repo: Salva agendamento
    Repo->>Mongo: insert appointment
    Mongo-->>Repo: Agendamento persistido
    Service-->>Controller: Agendamento criado
    Controller-->>View: Redireciona para meus agendamentos
```

## Observacoes de seguranca e LGPD

- Senhas sao armazenadas com hash BCrypt.
- O cadastro exige aceite LGPD.
- O gestor ve CPF, e-mail e telefone de forma parcial nas listas.
- A aplicacao separa permissoes de gestor e cliente.
- Para producao, recomenda-se habilitar HTTPS, rotacao de credenciais, logs auditaveis e politica de retencao de dados.

