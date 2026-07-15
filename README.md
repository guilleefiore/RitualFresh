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
- [Requisitos de M06 - Historial y Estadísticas](docs/requirements/M06_HISTORIAL_ESTADISTICAS.md)
- [Requisitos de M08 - Notificaciones](docs/requirements/M08_NOTIFICACIONES.md)

## Stack base

- Backend: Java 21, Spring Boot 3.5.x, Maven, PostgreSQL.
- Frontend: React 19, Node.js 22, Bootstrap 5.3.

## Guía visual RitualFresh

- Referencia obligatoria: [frontend/docs/UI_GUIDELINES.md](frontend/docs/UI_GUIDELINES.md)
- Cualquier cambio visual futuro debe respetar esa guía antes de modificar componentes o tokens.

## Estado actual

El proyecto ya cuenta con una base backend funcional para:

- `M01`: registro, validación y reenvío de validación de cuenta, login (email/contraseña y Google OAuth), logout, autoeliminación lógica de la cuenta, recuperación de contraseña y sesiones opacas persistidas.
- `M02`: creación, consulta y actualización de perfiles de cliente y trabajador.
- `M05`: base independiente de chat entre cliente y trabajador, con conversaciones reutilizables por pareja, mensajes persistidos, historial paginado, lectura por mensaje, presencia y WebSocket. La habilitación definitiva queda desacoplada de `M04` hasta integrar su contrato.
- `M06`: read model persistente de servicios, historial propio paginado y estadísticas móviles para clientes y trabajadores. No expone altas públicas y queda preparado para recibir eventos internos de `M04`, `M07` y `M09`.
- `M08`: centro de notificaciones in-app persistente para todos los roles, con últimas 20, contador exacto, lectura individual y masiva, WebSocket autenticado e integración interna idempotente preparada para `M04`, `M09` y `M11`.
- administración de cuentas: métricas, directorio paginado, detalle y cambios de estado auditados.

En frontend ya se encuentran implementados los flujos base para:

- autenticación, validación y recuperación de contraseña;
- dashboard mínimo por rol;
- panel administrativo en `/admin/home`, directorio en `/admin/users` y detalle auditado en `/admin/users/:userId`;
- gestión de perfil propio para `CLIENT` y `WORKER` en la ruta protegida `/profiles`.
- chat inicial para `CLIENT` y `WORKER` en la ruta protegida `/chat`.
- historial en `/history` y estadísticas por rol en `/statistics`, ambas rutas protegidas para clientes y trabajadores.
- campana y panel de notificaciones responsive en todas las pantallas autenticadas de cliente, trabajador y administrador.

La autenticación y autorización del backend se resuelven con Spring Security, manteniendo el modelo actual de `UserSession`. Además del login por email/contraseña, el sistema integra inicio de sesión con Google mediante OAuth 2.0, que tras la autenticación externa crea o reutiliza un usuario local y establece la misma cookie `HttpOnly` de sesión. El transporte principal de sesión entre frontend y backend se realiza mediante cookie `HttpOnly`, aunque el backend todavía conserva compatibilidad técnica con `Authorization: Bearer <sessionToken>` para pruebas y debugging.

El modelo de datos general sigue sujeto a revisión para módulos posteriores, pero las entidades actuales de `auth`, `profiles`, `admin`, `chat`, `history` y `notifications` ya se encuentran implementadas y probadas.

## Cobertura de diagramas de clases

Los diagramas distinguen entre clases existentes y diseño previsto. En los módulos implementados se representan las clases y dependencias relevantes del código actual; en los módulos pendientes se propone una estructura inicial para discutir y ajustar antes de implementarla. Los DTOs triviales y adaptadores repetitivos pueden omitirse para conservar la legibilidad.

| Alcance | Paquete o módulo | Estado del diagrama |
|---|---|---|
| M01 | `auth` | Implementado |
| M02 | `profiles` | Implementado |
| M03 | `search` | Diseño propuesto |
| M04 | `contracts` | Diseño propuesto |
| M05 | `chat` | Implementación inicial existente |
| M06 | `history` | Implementado como read model |
| M07 | `ratings` | Diseño propuesto |
| M08 | `notifications` | Implementado para notificaciones in-app |
| M09 | `payments` | Diseño propuesto |
| M10 | `geolocation` | Diseño propuesto |
| M11 | `claims` | Diseño propuesto |
| Soporte transversal | `admin` | Implementado |

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

## Diagrama de clases del módulo `history`

M06 utiliza un read model propio para habilitar consultas reales antes de que existan contratación, pagos y calificaciones completos. El módulo no expone operaciones públicas de escritura: futuros módulos alimentarán `ServiceHistoryRecord` mediante integración interna.

```mermaid
classDiagram
    direction LR

    class ServiceHistoryRecord {
        +Long id
        +User client
        +User worker
        +String serviceName
        +String category
        +LocalDateTime scheduledAt
        +ServiceHistoryStatus status
        +BigDecimal amountArs
        +Integer workerRating
    }

    class ServiceHistoryStatus {
        <<enumeration>>
        PENDING
        COMPLETED
        CANCELLED
    }

    class StatisticsPeriod {
        <<enumeration>>
        LAST_7_DAYS
        LAST_30_DAYS
        LAST_365_DAYS
    }

    class WorkerStatisticsResponse {
        +long completedJobs
        +BigDecimal averageRating
        +List completedJobsTimeline
    }

    class ClientStatisticsResponse {
        +long hiredServices
        +long pendingServices
        +long completedServices
        +BigDecimal totalSpentArs
        +List spendingTimeline
        +List categories
        +List frequentWorkers
    }

    class HistoryController {
        +getMyServiceHistory(status, from, to, page, size) HistoryPageResponse
    }

    class StatisticsController {
        +getMyWorkerStatistics(period) WorkerStatisticsResponse
        +getMyClientStatistics(period) ClientStatisticsResponse
    }

    class HistoryService {
        +getMyHistory(status, from, to, page, size) HistoryPageResponse
    }

    class StatisticsService {
        +getMyWorkerStatistics(period) WorkerStatisticsResponse
        +getMyClientStatistics(period) ClientStatisticsResponse
    }

    class ServiceHistoryRecordRepository {
        <<interface>>
        +findHistory(userId, role, status, from, to, pageable) Page
        +findForStatistics(userId, role, from, to) List
    }

    ServiceHistoryRecord --> ServiceHistoryStatus : posee
    ServiceHistoryRecord --> User : cliente y trabajador
    HistoryController ..> HistoryService : delega
    StatisticsController ..> StatisticsService : delega
    HistoryService ..> ServiceHistoryRecordRepository : consulta
    StatisticsService ..> ServiceHistoryRecordRepository : agrega métricas
    StatisticsService ..> StatisticsPeriod : calcula ventana
    StatisticsController ..> WorkerStatisticsResponse : responde
    StatisticsController ..> ClientStatisticsResponse : responde
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
        +ServiceFrequency serviceFrequency
        +Set~PreferredTimeSlot~ preferredTimeSlots
        +Set~ServiceInterest~ serviceInterests
        +String otherServiceInterest
        +String additionalNotes
        +edit(photoUrl, contactPhone, address, preferences)
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

    class ServiceFrequency {
        <<enumeration>>
        ONE_TIME
        WEEKLY
        BIWEEKLY
        MONTHLY
        AS_NEEDED
    }

    class PreferredTimeSlot {
        <<enumeration>>
        MORNING
        AFTERNOON
        FLEXIBLE
    }

    class ServiceInterest {
        <<enumeration>>
        GENERAL_CLEANING
        DEEP_CLEANING
        HOME_MAINTENANCE
        ORGANIZATION
        LAUNDRY_AND_IRONING
        PLANT_CARE
        SMALL_REPAIRS
        OTHER
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
    ClientProfile --> ServiceFrequency : selecciona
    ClientProfile --> PreferredTimeSlot : prefiere
    ClientProfile --> ServiceInterest : solicita
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

El módulo reutiliza `User` para las cuentas y agrega una entidad de auditoría para conservar cada cambio administrativo de estado. El listado se resuelve mediante un repositorio de consulta con filtros y paginación; los DTOs se omiten para mantener el diagrama legible.

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
        +listUsers(filters, pageable) AdminUsersPageResponse
        +getUser(authentication, id) AdminUserDetailResponse
        +updateUserStatus(authentication, id, request) AdminUserDetailResponse
        +getStatusHistory(authentication, id, pageable) AdminStatusHistoryResponse
        +getMetrics(authentication) AdminMetricsResponse
    }

    class AdminService {
        +listUsers(filters, pageable) AdminUsersPageResponse
        +getUser(userId) AdminUserDetailResponse
        +updateUserStatus(userId, status, reason) AdminUserDetailResponse
        +getStatusHistory(userId, pageable) AdminStatusHistoryResponse
        +getMetrics() AdminMetricsResponse
    }

    class AdminUserStatusChange {
        +Long id
        +Long actorAdminId
        +String actorEmail
        +Long targetUserId
        +String targetEmail
        +AccountStatus previousStatus
        +AccountStatus newStatus
        +String reason
        +LocalDateTime changedAt
    }

    class AdminUserQueryRepository {
        <<interface>>
        +search(query, role, status, pageable) Page~User~
        +countAll() long
        +countByRole(role) long
        +countByStatus(status) long
    }

    class AdminStatusChangeRepository {
        <<interface>>
        +save(change) AdminUserStatusChange
        +findByTargetUserId(id, pageable) Page~AdminUserStatusChange~
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
    AdminService ..> AdminUserQueryRepository : busca/pagina
    AdminService ..> AdminStatusChangeRepository : audita cambios
    AdminService ..> User : lista/edita
    AdminService ..> UserRole : cuenta por rol
    AdminService ..> AccountStatus : valida transiciones

    User --> UserRole : usa
    User --> AccountStatus : usa
    AdminUserStatusChange --> AccountStatus : registra

    AdminBootstrapSeeder ..> UserRepository : verifica/guarda
    AdminBootstrapSeeder ..> PasswordSecurity : cifra clave inicial
    AdminBootstrapSeeder ..> User : crea admin inicial
    AdminBootstrapSeeder ..> UserRole : asigna ADMIN
```

## Diagrama de clases propuesto para M03 `search`

M03 todavía no está implementado. El siguiente diseño propone separar los criterios de búsqueda, la consulta optimizada y el cálculo de ranking, reutilizando el perfil del trabajador de M02. `ServiceOffering`, `ServiceCategory` y `GeoLocation` deberán ajustarse cuando se definan sus catálogos y contratos definitivos.

```mermaid
classDiagram
    direction LR

    class SearchController {
        +searchWorkers(criteria, page, size) WorkerSearchPageResponse
        +getWorkerDetail(workerId) WorkerSearchResult
    }

    class WorkerSearchService {
        +search(criteria, pageable) WorkerSearchPageResponse
        +getDetail(workerId) WorkerSearchResult
    }

    class WorkerSearchCriteria {
        +String query
        +Long categoryId
        +String city
        +String province
        +BigDecimal minimumPrice
        +BigDecimal maximumPrice
        +SearchSort sort
    }

    class SearchSort {
        <<enumeration>>
        RELEVANCE
        RATING_DESC
        PRICE_ASC
        PRICE_DESC
    }

    class WorkerSearchResult {
        +Long workerId
        +String displayName
        +String photoUrl
        +List services
        +String workArea
        +BigDecimal hourlyRate
        +BigDecimal averageRating
        +BigDecimal rankingScore
    }

    class WorkerSearchRepository {
        <<interface>>
        +search(criteria, pageable) Page~WorkerSearchResult~
        +findDetail(workerId) Optional~WorkerSearchResult~
    }

    class RankingCalculator {
        +calculate(workerProfile, reputation) BigDecimal
    }

    class WorkerProfile {
        +Long id
        +String description
        +String workArea
        +BigDecimal hourlyRate
    }

    class ServiceOffering {
        +Long id
        +String name
        +String description
        +BigDecimal referencePrice
        +boolean active
    }

    class ServiceCategory {
        +Long id
        +String name
        +String slug
        +boolean active
    }

    class GeoLocation {
        +BigDecimal latitude
        +BigDecimal longitude
        +String city
        +String province
    }

    SearchController ..> WorkerSearchService : delega
    WorkerSearchService ..> WorkerSearchRepository : consulta
    WorkerSearchService ..> RankingCalculator : ordena
    WorkerSearchService ..> WorkerSearchCriteria : recibe
    WorkerSearchRepository ..> WorkerSearchResult : proyecta
    WorkerSearchCriteria --> SearchSort : ordena por
    WorkerProfile "1" --> "0..*" ServiceOffering : ofrece
    ServiceOffering "*" --> "1" ServiceCategory : pertenece a
    WorkerProfile --> GeoLocation : trabaja en
    RankingCalculator ..> WorkerProfile : pondera
```

## Diagrama de clases propuesto para M04 `contracts`

M04 todavía no está implementado. El modelo propuesto concentra el ciclo de vida de la contratación en `ServiceContract` y publica eventos internos para que chat, historial, notificaciones, calificaciones y pagos reaccionen sin acoplar sus tablas.

```mermaid
classDiagram
    direction LR

    class ContractController {
        +requestContract(request) ContractResponse
        +listMyContracts(status, page, size) ContractPageResponse
        +accept(contractId) ContractResponse
        +reject(contractId, request) ContractResponse
        +confirmCompletion(contractId) ContractResponse
        +cancel(contractId, request) ContractResponse
    }

    class ContractService {
        +requestContract(request) ServiceContract
        +listMine(status, pageable) Page~ServiceContract~
        +accept(contractId) ServiceContract
        +reject(contractId, reason) ServiceContract
        +confirmCompletion(contractId) ServiceContract
        +cancel(contractId, reason) ServiceContract
    }

    class ServiceContract {
        +Long id
        +User client
        +User worker
        +ServiceOffering service
        +LocalDateTime scheduledAt
        +BigDecimal agreedAmountArs
        +ContractStatus status
        +CancellationActor cancelledBy
        +String cancellationReason
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +accept(now)
        +reject(reason, now)
        +confirmCompletion(actor, now)
        +cancel(actor, reason, now)
    }

    class ContractStatus {
        <<enumeration>>
        REQUESTED
        ACCEPTED
        IN_PROGRESS
        COMPLETION_PENDING
        COMPLETED
        REJECTED
        CANCELLED
    }

    class CancellationActor {
        <<enumeration>>
        CLIENT
        WORKER
        SYSTEM
    }

    class ContractRepository {
        <<interface>>
        +save(contract) ServiceContract
        +findById(id) Optional~ServiceContract~
        +findByParticipant(userId, status, pageable) Page~ServiceContract~
    }

    class ContractAccessPolicy {
        <<interface>>
        +validateRequest(client, worker, service) void
        +validateTransition(contract, actor, action) void
    }

    class ContractEventPublisher {
        <<interface>>
        +publish(event) void
    }

    class ContractEvent {
        +Long contractId
        +ContractStatus previousStatus
        +ContractStatus currentStatus
        +LocalDateTime occurredAt
    }

    class User {
        +Long id
        +UserRole role
        +AccountStatus accountStatus
    }

    class ServiceOffering {
        +Long id
        +String name
        +ServiceCategory category
    }

    class ServiceCategory {
        +Long id
        +String name
    }

    ContractController ..> ContractService : delega
    ContractService ..> ContractRepository : persiste
    ContractService ..> ContractAccessPolicy : autoriza
    ContractService ..> ContractEventPublisher : publica
    ContractEventPublisher ..> ContractEvent : emite
    ServiceContract --> ContractStatus : posee
    ServiceContract --> CancellationActor : registra
    ServiceContract --> User : cliente y trabajador
    ServiceContract --> ServiceOffering : contrata
    ServiceOffering --> ServiceCategory : clasifica
```

## Diagrama de clases del módulo `chat` (M05)

M05 cuenta con una implementación inicial independiente de M04. El contrato `ChatAccessPolicy` permite reemplazar la regla actual entre cliente y trabajador por una validación basada en contrataciones cuando M04 esté disponible.

```mermaid
classDiagram
    direction LR

    class ChatController {
        +listConversations() List~ConversationApiResponse~
        +createConversation(request) ConversationApiResponse
        +listMessages(conversationId, beforeMessageId) List~MessageApiResponse~
        +sendMessage(conversationId, request) MessageApiResponse
        +markMessagesRead(conversationId, request) ReadMessagesApiResponse
        +unreadCount() UnreadCountApiResponse
        +heartbeat() PresenceApiResponse
    }

    class ChatService {
        +createOrReactivateConversation(otherUserId) ConversationApiResponse
        +listConversations() List~ConversationApiResponse~
        +listMessages(conversationId, beforeMessageId) List~MessageApiResponse~
        +sendMessage(conversationId, content, clientMessageId) MessageApiResponse
        +markMessagesRead(conversationId, messageIds) ReadMessagesApiResponse
        +countUnreadMessages() long
        +heartbeat() PresenceApiResponse
    }

    class Conversation {
        +Long id
        +User client
        +User worker
        +ConversationStatus status
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +LocalDateTime lastMessageAt
        +active(client, worker, now) Conversation
        +reactivate(now)
        +markReadOnly(now)
        +registerMessage(sentAt)
        +hasParticipant(userId) boolean
        +otherParticipant(userId) User
    }

    class ChatMessage {
        +Long id
        +Conversation conversation
        +User sender
        +String content
        +LocalDateTime createdAt
        +LocalDateTime readAt
        +create(conversation, sender, content, now) ChatMessage
        +markRead(now)
    }

    class ChatPresence {
        +Long id
        +User user
        +LocalDateTime lastSeenAt
        +create(user, now) ChatPresence
        +heartbeat(now)
    }

    class ConversationStatus {
        <<enumeration>>
        ACTIVE
        READ_ONLY
    }

    class ConversationJpaRepository {
        <<interface>>
        +findByClientIdAndWorkerId(clientId, workerId) Optional~Conversation~
        +findByParticipantOrderByActivity(userId) List~Conversation~
    }

    class ChatMessageJpaRepository {
        <<interface>>
        +findPage(conversationId, beforeMessageId, pageable) List~ChatMessage~
        +countUnread(conversationId, userId) long
    }

    class ChatPresenceJpaRepository {
        <<interface>>
        +findByUserId(userId) Optional~ChatPresence~
    }

    class ChatAccessPolicy {
        <<interface>>
        +validateCanCreateOrReactivate(currentUser, otherUser) void
        +validateCanSendMessage(conversation, sender) void
    }

    class DefaultChatAccessPolicy {
        +validateCanCreateOrReactivate(currentUser, otherUser) void
        +validateCanSendMessage(conversation, sender) void
    }

    class ChatWebSocketConfig {
        +registerWebSocketHandlers(registry) void
    }

    class ChatHandshakeInterceptor {
        +beforeHandshake(request, response, handler, attributes) boolean
    }

    class ChatWebSocketHandler {
        +afterConnectionEstablished(session) void
        +afterConnectionClosed(session, status) void
    }

    class ChatWebSocketHub {
        +register(userId, session) void
        +remove(userId, session) void
        +sendToUser(userId, type, payload) void
        +sendToUsers(firstUserId, secondUserId, type, payload) void
    }

    class User {
        +Long id
        +UserRole role
        +isActive() boolean
    }

    ChatController ..> ChatService : delega
    ChatService ..> ConversationJpaRepository : usa
    ChatService ..> ChatMessageJpaRepository : usa
    ChatService ..> ChatPresenceJpaRepository : usa
    ChatService ..> ChatAccessPolicy : valida
    ChatService ..> ChatWebSocketHub : publica eventos
    ChatAccessPolicy <|.. DefaultChatAccessPolicy
    Conversation --> ConversationStatus : posee
    Conversation --> User : cliente y trabajador
    Conversation "1" --> "0..*" ChatMessage : contiene
    ChatMessage --> User : remitente
    ChatPresence "1" --> "1" User : representa
    ChatWebSocketConfig ..> ChatWebSocketHandler : registra
    ChatWebSocketConfig ..> ChatHandshakeInterceptor : protege
    ChatWebSocketHandler ..> ChatWebSocketHub : administra sesiones
```

## Diagrama de clases propuesto para M07 `ratings`

M07 todavía no está implementado. La propuesta garantiza una sola calificación por contratación y por autor, conserva el comentario original y actualiza una proyección de reputación sin guardar el promedio dentro de `User`.

```mermaid
classDiagram
    direction LR

    class RatingController {
        +createRating(contractId, request) RatingResponse
        +getContractRatings(contractId) List~RatingResponse~
        +getUserReputation(userId) ReputationResponse
    }

    class RatingService {
        +rate(contractId, score, comment) ServiceRating
        +findByContract(contractId) List~ServiceRating~
        +getReputation(userId) UserReputation
    }

    class ServiceRating {
        +Long id
        +ServiceContract contract
        +User author
        +User ratedUser
        +int score
        +String comment
        +LocalDateTime createdAt
    }

    class UserReputation {
        +Long userId
        +BigDecimal averageScore
        +long ratingCount
        +LocalDateTime updatedAt
        +recalculate(ratings)
    }

    class RatingRepository {
        <<interface>>
        +save(rating) ServiceRating
        +existsByContractAndAuthor(contractId, authorId) boolean
        +findByContractId(contractId) List~ServiceRating~
        +findByRatedUserId(userId) List~ServiceRating~
    }

    class ReputationRepository {
        <<interface>>
        +save(reputation) UserReputation
        +findByUserId(userId) Optional~UserReputation~
    }

    class RatingAccessPolicy {
        <<interface>>
        +validateCanRate(contract, author, ratedUser) void
    }

    class ServiceContract {
        +Long id
        +ContractStatus status
        +User client
        +User worker
    }

    class ContractStatus {
        <<enumeration>>
        COMPLETED
    }

    class User {
        +Long id
        +UserRole role
    }

    RatingController ..> RatingService : delega
    RatingService ..> RatingRepository : persiste
    RatingService ..> ReputationRepository : actualiza
    RatingService ..> RatingAccessPolicy : valida
    ServiceRating --> ServiceContract : califica
    ServiceContract --> ContractStatus : debe estar COMPLETED
    ServiceRating --> User : autor y calificado
    UserReputation "1" --> "1" User : resume
    User "1" --> "0..*" ServiceRating : recibe
```

## Diagrama de clases de M08 `notifications`

M08 implementa un read model persistente separado del correo de autenticación de M01. El panel consulta las últimas 20 notificaciones y el contador total de pendientes; los cambios de lectura y las altas internas se propagan por un WebSocket dedicado. Los DTOs y adaptadores triviales se omiten para mantener legible el flujo principal.

```mermaid
classDiagram
    direction LR

    class NotificationController {
        +getMyRecentNotifications() NotificationPanelResponse
        +markAsRead(notificationId) NotificationInteractionResponse
        +markAllAsRead() MarkAllNotificationsReadResponse
    }

    class NotificationService {
        +getMyRecentNotifications() NotificationPanelResponse
        +markAsRead(notificationId) NotificationInteractionResponse
        +markAllAsRead() MarkAllNotificationsReadResponse
        +createFromEvent(command) Optional~NotificationItemResponse~
    }

    class InAppNotification {
        +Long id
        +User recipient
        +NotificationType type
        +String title
        +String message
        +NotificationResourceType resourceType
        +Long resourceId
        +String eventKey
        +LocalDateTime createdAt
        +LocalDateTime readAt
        +markRead(now)
        +isRead() boolean
    }

    class NotificationType {
        <<enumeration>>
        SERVICE_CONFIRMED
        PAYMENT_APPROVED
        CLAIM_RESOLVED
    }

    class NotificationResourceType {
        <<enumeration>>
        CONTRACT
        PAYMENT
        CLAIM
    }

    class NotificationRepository {
        <<interface>>
        +save(notification) InAppNotification
        +findRecentByRecipientId(recipientId, limit) List~InAppNotification~
        +countUnreadByRecipientId(recipientId) long
        +findByIdAndRecipientId(notificationId, recipientId) Optional~InAppNotification~
        +findByRecipientIdAndEventKey(recipientId, eventKey) Optional~InAppNotification~
        +markAllRead(recipientId, readAt) int
    }

    class NotificationCommand {
        +String eventKey
        +Long recipientId
        +NotificationType type
        +String title
        +String message
        +NotificationResourceType resourceType
        +Long resourceId
        +LocalDateTime occurredAt
    }

    class NotificationPublisher {
        <<interface>>
        +publish(command) void
    }

    class NotificationEventListener {
        +onNotificationCommand(command) void
    }

    class NotificationDestinationResolver {
        <<interface>>
        +supports(resourceType) boolean
        +resolve(recipient, resourceId) Optional~String~
    }

    class NotificationDestinationService {
        +resolve(notification, recipient) NotificationDestinationResponse
    }

    class NotificationRealtimeDispatcher {
        +notificationCreated(recipientId, notification, unreadCount) void
        +notificationRead(recipientId, notificationId, readAt, unreadCount) void
        +notificationsReadAll(recipientId, readAt, updatedCount, unreadCount) void
    }

    class NotificationRealtimePublisher {
        <<interface>>
        +publish(recipientId, type, payload) void
    }

    class NotificationWebSocketHub {
        +register(userId, session) void
        +unregister(userId, session) void
        +publish(recipientId, type, payload) void
    }

    class NotificationWebSocketHandler {
        +afterConnectionEstablished(session) void
        +afterConnectionClosed(session, status) void
    }

    class NotificationHandshakeInterceptor {
        +beforeHandshake(request, response, handler, attributes) boolean
    }

    class NotificationWebSocketConfig {
        +registerWebSocketHandlers(registry) void
    }

    class AccountEmailService {
        <<interface>>
        +sendAccountValidationEmail(user, token, expiresAt) void
        +sendPasswordResetEmail(user, token, expiresAt) void
    }

    class User {
        +Long id
        +UserRole role
    }

    NotificationController ..> NotificationService : delega
    NotificationEventListener ..> NotificationService : crea aviso
    NotificationEventListener ..> NotificationCommand : consume
    NotificationPublisher ..> NotificationCommand : publica
    NotificationService ..> NotificationRepository : persiste
    NotificationService ..> NotificationDestinationService : resuelve destino
    NotificationDestinationService ..> NotificationDestinationResolver : delega por recurso
    NotificationService ..> NotificationRealtimeDispatcher : difiere hasta commit
    NotificationRealtimeDispatcher ..> NotificationRealtimePublisher : publica tras commit
    NotificationWebSocketHub ..|> NotificationRealtimePublisher : implementa
    NotificationWebSocketHandler ..> NotificationWebSocketHub : registra sesiones
    NotificationHandshakeInterceptor ..> User : autentica destinatario
    NotificationWebSocketConfig ..> NotificationWebSocketHandler : registra
    NotificationWebSocketConfig ..> NotificationHandshakeInterceptor : protege
    InAppNotification --> NotificationType : clasifica
    InAppNotification --> NotificationResourceType : referencia
    InAppNotification --> User : destinatario
    AccountEmailService ..> User : notifica por email
```

## Diagrama de clases propuesto para M09 `payments`

M09 todavía no está implementado. El diseño separa checkout, recepción idempotente de webhooks, reembolsos, sanciones y liquidaciones. Los estados de Mercado Pago deberán traducirse a enumeraciones propias antes de persistirse.

```mermaid
classDiagram
    direction LR

    class PaymentController {
        +createCheckout(contractId) CheckoutResponse
        +getPayment(contractId) PaymentResponse
        +getWorkerIncome(period) WorkerIncomeResponse
    }

    class PaymentWebhookController {
        +receiveMercadoPagoNotification(request) void
    }

    class PaymentService {
        +createCheckout(contractId) CheckoutSession
        +registerApprovedPayment(event) PaymentTransaction
        +registerPendingPayment(event) PaymentTransaction
        +registerRejectedPayment(event) PaymentTransaction
        +refundForCancellation(contractId, actor) Refund
    }

    class SettlementService {
        +calculateWorkerSettlement(period) List~WorkerSettlement~
        +settle(settlementId) WorkerSettlement
    }

    class PenaltyService {
        +registerWorkerCancellation(contractId) WorkerStrike
        +expireMonthlyStrikes(now) int
    }

    class PaymentTransaction {
        +Long id
        +ServiceContract contract
        +String externalPaymentId
        +BigDecimal grossAmountArs
        +BigDecimal platformFeeArs
        +BigDecimal workerNetAmountArs
        +PaymentStatus status
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    class PaymentStatus {
        <<enumeration>>
        CREATED
        PENDING
        APPROVED
        REJECTED
        REFUNDED
        PARTIALLY_REFUNDED
    }

    class Refund {
        +Long id
        +PaymentTransaction payment
        +BigDecimal amountArs
        +RefundStatus status
        +String reason
        +LocalDateTime createdAt
    }

    class RefundStatus {
        <<enumeration>>
        REQUESTED
        APPROVED
        REJECTED
    }

    class WorkerSettlement {
        +Long id
        +User worker
        +LocalDate periodFrom
        +LocalDate periodTo
        +BigDecimal amountArs
        +SettlementStatus status
        +LocalDateTime settledAt
    }

    class SettlementStatus {
        <<enumeration>>
        PENDING
        PROCESSING
        SETTLED
        FAILED
    }

    class WorkerStrike {
        +Long id
        +User worker
        +ServiceContract contract
        +String reason
        +LocalDateTime createdAt
        +LocalDateTime expiresAt
        +boolean active
    }

    class PaymentRepository {
        <<interface>>
        +save(payment) PaymentTransaction
        +findByContractId(contractId) Optional~PaymentTransaction~
        +findByExternalPaymentId(externalId) Optional~PaymentTransaction~
    }

    class RefundRepository {
        <<interface>>
        +save(refund) Refund
    }

    class SettlementRepository {
        <<interface>>
        +save(settlement) WorkerSettlement
        +findPending() List~WorkerSettlement~
    }

    class StrikeRepository {
        <<interface>>
        +save(strike) WorkerStrike
        +findActiveByWorkerId(workerId) List~WorkerStrike~
    }

    class MercadoPagoGateway {
        <<interface>>
        +createPreference(payment) CheckoutSession
        +getPayment(externalId) ExternalPayment
        +refund(externalId, amount) ExternalRefund
        +transfer(worker, amount) ExternalTransfer
    }

    class ServiceContract {
        +Long id
        +BigDecimal agreedAmountArs
        +ContractStatus status
    }

    class ContractStatus {
        <<enumeration>>
        ACCEPTED
        COMPLETED
        CANCELLED
    }

    class User {
        +Long id
        +UserRole role
    }

    PaymentController ..> PaymentService : delega
    PaymentWebhookController ..> PaymentService : procesa idempotente
    PaymentService ..> PaymentRepository : persiste
    PaymentService ..> RefundRepository : reembolsa
    PaymentService ..> MercadoPagoGateway : integra
    SettlementService ..> SettlementRepository : liquida
    SettlementService ..> MercadoPagoGateway : transfiere
    PenaltyService ..> StrikeRepository : sanciona
    PaymentTransaction --> PaymentStatus : posee
    PaymentTransaction --> ServiceContract : paga
    ServiceContract --> ContractStatus : condiciona operación
    PaymentTransaction "1" --> "0..*" Refund : origina
    Refund --> RefundStatus : posee
    WorkerSettlement --> SettlementStatus : posee
    WorkerSettlement --> User : trabajador
    WorkerStrike --> User : trabajador
    WorkerStrike --> ServiceContract : cancelación
```

## Diagrama de clases propuesto para M10 `geolocation`

M10 todavía no está implementado. La propuesta usa una entidad de ubicación reutilizable y un valor `GeoPoint`; el perfil y la contratación conservan la referencia que corresponda sin depender de una biblioteca de mapas específica.

```mermaid
classDiagram
    direction LR

    class GeolocationController {
        +saveProfileLocation(request) GeolocationResponse
        +saveContractLocation(contractId, request) GeolocationResponse
        +getLocation(locationId) GeolocationResponse
    }

    class GeolocationService {
        +saveForProfile(userId, request) GeoLocation
        +saveForContract(contractId, request) GeoLocation
        +get(locationId) GeoLocation
    }

    class GeoLocation {
        +Long id
        +GeoPoint point
        +String formattedAddress
        +String street
        +String streetNumber
        +String city
        +String province
        +String postalCode
        +LocalDateTime createdAt
        +update(point, address)
    }

    class GeoPoint {
        <<valueObject>>
        +BigDecimal latitude
        +BigDecimal longitude
        +validate() void
    }

    class GeolocationRepository {
        <<interface>>
        +save(location) GeoLocation
        +findById(id) Optional~GeoLocation~
    }

    class GeocodingGateway {
        <<interface>>
        +reverse(point) AddressData
        +search(query) List~AddressCandidate~
    }

    class ClientProfile {
        +Long id
        +GeoLocation location
    }

    class WorkerProfile {
        +Long id
        +GeoLocation workLocation
        +BigDecimal serviceRadiusKm
    }

    class ServiceContract {
        +Long id
        +GeoLocation serviceLocation
    }

    GeolocationController ..> GeolocationService : delega
    GeolocationService ..> GeolocationRepository : persiste
    GeolocationService ..> GeocodingGateway : normaliza dirección
    GeoLocation *-- GeoPoint : contiene
    ClientProfile --> GeoLocation : domicilio
    WorkerProfile --> GeoLocation : zona de trabajo
    ServiceContract --> GeoLocation : lugar del servicio
```

## Diagrama de clases propuesto para M11 `claims`

M11 todavía no está implementado. El diseño vincula cada reclamo con una contratación, conserva evidencias y una resolución auditable, y separa las acciones del usuario de la gestión administrativa.

```mermaid
classDiagram
    direction LR

    class ClaimController {
        +createClaim(contractId, request) ClaimResponse
        +listMyClaims(status, page, size) ClaimPageResponse
        +getMyClaim(claimId) ClaimResponse
        +addComment(claimId, request) ClaimCommentResponse
    }

    class AdminClaimController {
        +searchClaims(filters, page, size) ClaimPageResponse
        +assignClaim(claimId, adminId) ClaimResponse
        +changeStatus(claimId, request) ClaimResponse
        +resolveClaim(claimId, request) ClaimResponse
    }

    class ClaimService {
        +create(contractId, type, description, attachments) Claim
        +addComment(claimId, author, content) ClaimComment
        +assign(claimId, admin) Claim
        +changeStatus(claimId, status) Claim
        +resolve(claimId, resolution) Claim
    }

    class Claim {
        +Long id
        +ServiceContract contract
        +User reporter
        +User reportedUser
        +ClaimType type
        +ClaimStatus status
        +String description
        +User assignedAdmin
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
        +assign(admin, now)
        +changeStatus(status, now)
        +resolve(resolution, now)
    }

    class ClaimType {
        <<enumeration>>
        SERVICE_QUALITY
        NO_SHOW
        PAYMENT
        INAPPROPRIATE_BEHAVIOR
        PROPERTY_DAMAGE
        OTHER
    }

    class ClaimStatus {
        <<enumeration>>
        OPEN
        IN_REVIEW
        WAITING_FOR_USER
        RESOLVED
        REJECTED
    }

    class ClaimComment {
        +Long id
        +Claim claim
        +User author
        +String content
        +LocalDateTime createdAt
    }

    class ClaimAttachment {
        +Long id
        +Claim claim
        +String storageKey
        +String originalName
        +String contentType
        +long sizeBytes
    }

    class ClaimResolution {
        +Long id
        +Claim claim
        +User resolvedBy
        +ResolutionType type
        +String detail
        +LocalDateTime resolvedAt
    }

    class ResolutionType {
        <<enumeration>>
        NO_ACTION
        WARNING
        REFUND
        PARTIAL_REFUND
        ACCOUNT_SUSPENSION
    }

    class ClaimRepository {
        <<interface>>
        +save(claim) Claim
        +findById(id) Optional~Claim~
        +findByParticipant(userId, status, pageable) Page~Claim~
        +search(filters, pageable) Page~Claim~
    }

    class ClaimNotificationPublisher {
        <<interface>>
        +publishClaimUpdated(claim) void
    }

    class ServiceContract {
        +Long id
        +User client
        +User worker
        +ContractStatus status
    }

    class ContractStatus {
        <<enumeration>>
        COMPLETED
        CANCELLED
    }

    class User {
        +Long id
        +UserRole role
    }

    ClaimController ..> ClaimService : delega usuario
    AdminClaimController ..> ClaimService : delega administración
    ClaimService ..> ClaimRepository : persiste
    ClaimService ..> ClaimNotificationPublisher : notifica
    Claim --> ClaimType : clasifica
    Claim --> ClaimStatus : posee
    Claim --> ServiceContract : corresponde a
    ServiceContract --> ContractStatus : contexto
    Claim --> User : reportante, reportado y admin
    Claim "1" --> "0..*" ClaimComment : contiene
    Claim "1" --> "0..*" ClaimAttachment : adjunta
    Claim "1" --> "0..1" ClaimResolution : cierra con
    ClaimResolution --> ResolutionType : aplica
```
