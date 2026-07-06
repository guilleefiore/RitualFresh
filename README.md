# RitualFresh

Plataforma web académica para la gestión de servicios domésticos de limpieza y mantenimiento del hogar.

## Referencias principales

- [Contexto del proyecto](docs/context/PROJECT_CONTEXT.md)
- [Alcance](docs/context/SCOPE.md)
- [Módulos funcionales](docs/context/MODULES.md)
- [Reglas de negocio](docs/context/BUSINESS_RULES.md)
- [Guía de implementación](docs/development/IMPLEMENTATION_GUIDE.md)
- [Estado del modelo de datos](docs/architecture/DATA_MODEL_STATUS.md)
- [Índice de historias](docs/requirements/USER_STORIES_INDEX.md)

## Stack base

- Backend: Java 21, Spring Boot 3.5.x, Maven, PostgreSQL.
- Frontend: React 19, Node.js 22, Bootstrap 5.3.

## Estado actual

El proyecto ya cuenta con una base backend funcional para:

- `M01`: registro, validación y reenvío de validación de cuenta, login (email/contraseña y Google OAuth), logout, autoeliminación lógica de la cuenta, recuperación de contraseña y sesiones opacas persistidas.
- `M02`: creación, consulta y actualización de perfiles de cliente y trabajador.
- administración mínima: listado de usuarios, detalle, cambio de estado y métricas básicas.

En frontend ya se encuentran implementados los flujos base para:

- autenticación, validación y recuperación de contraseña;
- dashboard mínimo por rol;
- panel administrativo inicial en `/admin/home` con detalle por usuario en `/admin/users/:userId`;
- gestión de perfil propio para `CLIENT` y `WORKER` en la ruta protegida `/profiles`.

La autenticación y autorización del backend se resuelven con Spring Security, manteniendo el modelo actual de `UserSession`. Además del login por email/contraseña, el sistema integra inicio de sesión con Google mediante OAuth 2.0, que tras la autenticación externa crea o reutiliza un usuario local y establece la misma cookie `HttpOnly` de sesión. El transporte principal de sesión entre frontend y backend se realiza mediante cookie `HttpOnly`, aunque el backend todavía conserva compatibilidad técnica con `Authorization: Bearer <sessionToken>` para pruebas y debugging.

El modelo de datos general sigue sujeto a revisión para módulos posteriores, pero las entidades actuales de `auth`, `profiles` y `admin` ya se encuentran implementadas y probadas.

## Diagrama de clases del módulo `auth`

Para este módulo conviene priorizar las clases de `model`, pero también resulta útil mostrar cómo se conectan con `controller`, `service` y `repository`. En este primer diagrama se omiten los DTOs para no sobredimensionar la vista y preservar la legibilidad.

```mermaid
classDiagram
    direction LR

    class User {
        +Long id
        +String firstName
        +String lastName
        +String documentNumber
        +String phoneNumber
        +String email
        +String passwordHash
        +UserRole role
        +AccountStatus accountStatus
        +LocalDateTime createdAt
        +LocalDateTime deactivatedAt
        +String accountValidationToken
        +LocalDateTime accountValidationTokenExpiresAt
        +String passwordResetToken
        +LocalDateTime passwordResetTokenExpiresAt
        +register(data) User
        +validateAccount()
        +startAccountValidation(token, expiresAt)
        +hasValidAccountValidationToken(now) boolean
        +editData(firstName, lastName, documentNumber, phoneNumber)
        +changePassword(passwordHash)
        +startPasswordReset(token, expiresAt)
        +hasValidPasswordResetToken(now) boolean
        +changeAccountStatus(accountStatus)
        +deactivate()
        +isActive() boolean
    }

    class UserSession {
        +Long id
        +String token
        +LocalDateTime createdAt
        +LocalDateTime expiresAt
        +LocalDateTime closedAt
        +isActive(now) boolean
        +close(closedAt)
    }

    class UserRole {
        <<enumeration>>
        CLIENT
        WORKER
        ADMIN
    }

    class AccountStatus {
        <<enumeration>>
        PENDING_VALIDATION
        ACTIVE
        SUSPENDED
        DELETED
    }

    class UserController {
        +registerUser(request) RegisterUserApiResponse
        +validateAccount(token) AccountValidationApiResponse
        +resendAccountValidation(request) ResendAccountValidationApiResponse
        +login(request, response) LoginApiResponse
        +requestPasswordReset(request) PasswordResetApiResponse
        +confirmPasswordReset(request) MessageApiResponse
        +closeSession(authentication, response) MessageApiResponse
        +deleteMyAccount(authentication, response) MessageApiResponse
    }

    class UserService {
        +registerUser(request) RegisterUserResult
        +validateAccount(token) User
        +resendAccountValidation(email) void
        +login(request) LoginResult
        +loginWithGoogle(profileData) LoginResult
        +updateUserRole(userId, newRole) User
        +getAuthenticatedUser(sessionToken) User
        +getAuthenticatedSession(sessionToken) LoginResult
        +closeSession(sessionToken) void
        +deleteAuthenticatedAccount(sessionToken) void
        +getAuthenticatedUser() User
        +getAuthenticatedSessionToken() String
        +requestPasswordReset(request) PasswordResetResult
        +confirmPasswordReset(request) User
    }

    class UserRepository {
        <<interface>>
        +save(user) User
        +findAll() List~User~
        +findById(id) Optional~User~
        +findByEmail(email) Optional~User~
        +findByAccountValidationToken(token) Optional~User~
        +findByPasswordResetToken(token) Optional~User~
        +existsByEmail(email) boolean
    }

    class UserSessionRepository {
        <<interface>>
        +save(session) UserSession
        +findByToken(token) Optional~UserSession~
    }

    class JpaUserRepository {
        +save(user) User
        +findAll() List~User~
        +findById(id) Optional~User~
        +findByEmail(email) Optional~User~
        +findByAccountValidationToken(token) Optional~User~
        +findByPasswordResetToken(token) Optional~User~
        +existsByEmail(email) boolean
    }

    class JpaUserSessionRepository {
        +save(session) UserSession
        +findByToken(token) Optional~UserSession~
    }

    class InMemoryUserRepository {
        +save(user) User
        +findAll() List~User~
        +findById(id) Optional~User~
        +findByEmail(email) Optional~User~
        +findByAccountValidationToken(token) Optional~User~
        +findByPasswordResetToken(token) Optional~User~
        +existsByEmail(email) boolean
    }

    class InMemoryUserSessionRepository {
        +save(session) UserSession
        +findByToken(token) Optional~UserSession~
    }

    class UserJpaRepository {
        <<interface>>
        +findByEmail(email) Optional~User~
        +findByAccountValidationToken(token) Optional~User~
        +findByPasswordResetToken(token) Optional~User~
        +existsByEmail(email) boolean
    }

    class UserSessionJpaRepository {
        <<interface>>
        +findByToken(token) Optional~UserSession~
    }

    class AccountEmailService {
        <<interface>>
        +sendAccountValidationEmail(user, token, expiresAt) void
        +sendPasswordResetEmail(user, token, expiresAt) void
    }

    class PasswordSecurity {
        <<utility>>
        +generateHash(password) String
        +matches(rawPassword, passwordHash) boolean
    }

    class GoogleOAuth2SuccessHandler {
        +onAuthenticationSuccess(request, response, authentication) void
    }

    class OAuth2ProfileData {
        +String email
        +String firstName
        +String lastName
    }

    User "1" --> "0..*" UserSession : mantiene
    User --> UserRole : usa
    User --> AccountStatus : usa

    UserController ..> UserService : delega
    UserService ..> UserRepository : usa
    UserService ..> UserSessionRepository : usa
    UserService ..> AccountEmailService : notifica
    UserService ..> PasswordSecurity : cifra/valida
    UserService ..> User : gestiona
    UserService ..> UserSession : crea/cierra

    UserRepository <|.. JpaUserRepository
    UserRepository <|.. InMemoryUserRepository
    UserSessionRepository <|.. JpaUserSessionRepository
    UserSessionRepository <|.. InMemoryUserSessionRepository

    JpaUserRepository ..> UserJpaRepository : adapta
    JpaUserSessionRepository ..> UserSessionJpaRepository : adapta
    UserJpaRepository --> User : persiste
    UserSessionJpaRepository --> UserSession : persiste

    GoogleOAuth2SuccessHandler ..> UserService : usa
    GoogleOAuth2SuccessHandler ..> OAuth2ProfileData : extrae
    UserService ..> OAuth2ProfileData : recibe
    UserService ..> User : oauthAccount()
    UserController ..> UserService : updateUserRole()
```
## Diagrama de clases del módulo `profiles`

Siguiendo el mismo criterio que en `auth`, en este diagrama se priorizan las clases de `model` y se agregan `controller`, `service` y `repository` para mostrar cómo se articula el módulo. Los DTOs también se omiten para conservar una vista más clara del flujo principal.

```mermaid
classDiagram
    direction LR

    class User {
        +Long id
        +UserRole role
        +isActive() boolean
    }

    class ClientProfile {
        +Long id
        +String photoUrl
        +int clientRating
        +String contactPhone
        +String streetName
        +String streetNumber
        +String floor
        +String apartment
        +String postalCode
        +String city
        +String province
        +String hiringPreferences
        +edit(photoUrl, contactPhone, streetName, streetNumber, floor, apartment, postalCode, city, province, hiringPreferences)
    }

    class WorkerProfile {
        +Long id
        +String photoUrl
        +int rankingPosition
        +String description
        +int yearsOfExperience
        +String offeredServices
        +String workArea
        +String availability
        +BigDecimal hourlyRate
        +edit(photoUrl, description, yearsOfExperience, offeredServices, workArea, availability, hourlyRate)
    }

    class ProfileType {
        <<enumeration>>
        CLIENT
        WORKER
    }

    class ProfileController {
        +createClientProfile(authentication, request) ProfileOperationApiResponse
        +createWorkerProfile(authentication, request) ProfileOperationApiResponse
        +getMyProfile(authentication) ProfileApiResponse
        +updateClientProfile(authentication, request) ProfileOperationApiResponse
        +updateWorkerProfile(authentication, request) ProfileOperationApiResponse
    }

    class ProfileService {
        +createClientProfile(request) UserProfileResult
        +createWorkerProfile(request) UserProfileResult
        +getMyProfile() UserProfileResult
        +updateClientProfile(request) UserProfileResult
        +updateWorkerProfile(request) UserProfileResult
    }

    class UserService {
        +getAuthenticatedUser() User
    }

    class ClientProfileRepository {
        <<interface>>
        +save(profile) ClientProfile
        +findByUserId(userId) Optional~ClientProfile~
        +existsByUserId(userId) boolean
    }

    class WorkerProfileRepository {
        <<interface>>
        +save(profile) WorkerProfile
        +findByUserId(userId) Optional~WorkerProfile~
        +existsByUserId(userId) boolean
    }

    class JpaClientProfileRepository {
        +save(profile) ClientProfile
        +findByUserId(userId) Optional~ClientProfile~
        +existsByUserId(userId) boolean
    }

    class JpaWorkerProfileRepository {
        +save(profile) WorkerProfile
        +findByUserId(userId) Optional~WorkerProfile~
        +existsByUserId(userId) boolean
    }

    class InMemoryClientProfileRepository {
        +save(profile) ClientProfile
        +findByUserId(userId) Optional~ClientProfile~
        +existsByUserId(userId) boolean
    }

    class InMemoryWorkerProfileRepository {
        +save(profile) WorkerProfile
        +findByUserId(userId) Optional~WorkerProfile~
        +existsByUserId(userId) boolean
    }

    class ClientProfileJpaRepository {
        <<interface>>
        +findByUser_Id(userId) Optional~ClientProfile~
        +existsByUser_Id(userId) boolean
    }

    class WorkerProfileJpaRepository {
        <<interface>>
        +findByUser_Id(userId) Optional~WorkerProfile~
        +existsByUser_Id(userId) boolean
    }

    User "1" --> "0..1" ClientProfile : posee
    User "1" --> "0..1" WorkerProfile : posee
    User --> UserRole : usa
    ClientProfile --> User : pertenece a
    WorkerProfile --> User : pertenece a
    ClientProfile --> ProfileType : representa CLIENT
    WorkerProfile --> ProfileType : representa WORKER

    ProfileController ..> ProfileService : delega
    ProfileService ..> UserService : obtiene usuario autenticado
    ProfileService ..> ClientProfileRepository : usa
    ProfileService ..> WorkerProfileRepository : usa
    ProfileService ..> User : valida rol y ownership
    ProfileService ..> ClientProfile : crea/edita
    ProfileService ..> WorkerProfile : crea/edita

    ClientProfileRepository <|.. JpaClientProfileRepository
    ClientProfileRepository <|.. InMemoryClientProfileRepository
    WorkerProfileRepository <|.. JpaWorkerProfileRepository
    WorkerProfileRepository <|.. InMemoryWorkerProfileRepository

    JpaClientProfileRepository ..> ClientProfileJpaRepository : adapta
    JpaWorkerProfileRepository ..> WorkerProfileJpaRepository : adapta
    ClientProfileJpaRepository --> ClientProfile : persiste
    WorkerProfileJpaRepository --> WorkerProfile : persiste

    class StorageService {
        +save(file) String
        +load(filename) Resource
    }

    class FileUploadController {
        +uploadFile(file) Map~String, String~
    }

    StorageService ..> FileUploadController : usado por
    ProfileService ..> StorageService : sube foto de perfil
```

## Diagrama de clases del módulo `admin`

En este módulo también conviene concentrarse en las clases que sostienen el flujo principal de administración. Se muestran `controller`, `service`, el modelo reutilizado de `auth`, el repositorio compartido y el seeder de bootstrap; los DTOs se omiten para mantener el diagrama legible.

```mermaid
classDiagram
    direction LR

    class User {
        +Long id
        +String firstName
        +String lastName
        +String email
        +UserRole role
        +AccountStatus accountStatus
        +LocalDateTime createdAt
        +LocalDateTime deactivatedAt
        +register(data) User
        +validateAccount()
        +changeAccountStatus(accountStatus)
        +deactivate()
        +setRole(role)
        +isActive() boolean
    }

    class UserRole {
        <<enumeration>>
        CLIENT
        WORKER
        ADMIN
    }

    class AccountStatus {
        <<enumeration>>
        PENDING_VALIDATION
        ACTIVE
        SUSPENDED
        DELETED
    }

    class AdminController {
        +listUsers(authentication) List~AdminUserResponse~
        +getUser(authentication, id) AdminUserResponse
        +updateUserStatus(authentication, id, request) AdminUserResponse
        +getMetrics(authentication) AdminMetricsResponse
    }

    class AdminService {
        +listUsers() List~AdminUserResponse~
        +getUser(userId) AdminUserResponse
        +updateUserStatus(userId, request) AdminUserResponse
        +getMetrics() AdminMetricsResponse
    }

    class UserService {
        +getAuthenticatedUser() User
    }

    class UserRepository {
        <<interface>>
        +save(user) User
        +findAll() List~User~
        +findById(id) Optional~User~
        +existsByEmail(email) boolean
    }

    class AdminBootstrapSeeder {
        +run(args) void
    }

    class PasswordSecurity {
        <<utility>>
        +generateHash(password) String
    }

    AdminController ..> AdminService : delega
    AdminService ..> UserService : valida admin autenticado
    AdminService ..> UserRepository : consulta/persiste
    AdminService ..> User : lista/edita
    AdminService ..> UserRole : cuenta por rol
    AdminService ..> AccountStatus : valida transiciones

    User --> UserRole : usa
    User --> AccountStatus : usa

    AdminBootstrapSeeder ..> UserRepository : verifica/guarda
    AdminBootstrapSeeder ..> PasswordSecurity : cifra clave inicial
    AdminBootstrapSeeder ..> User : crea admin inicial
    AdminBootstrapSeeder ..> UserRole : asigna ADMIN
```
