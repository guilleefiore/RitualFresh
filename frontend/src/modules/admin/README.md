# Módulo Admin

El módulo `admin` contiene todas las funcionalidades de administración de la plataforma RitualFresh.

## Estructura

```
admin/
├── components/          # Componentes reutilizables
│   ├── UserStatusForm.jsx        # Formulario para cambiar estado de usuario
│   ├── UsersList.jsx             # Tabla de listado de usuarios
│   └── MetricsCard.jsx           # Tarjetas de métricas
├── hooks/              # Hooks personalizados
│   └── useAdminData.js          # Hooks para obtener datos de admin
├── pages/              # Páginas principales
│   ├── AdminDashboard.jsx        # Dashboard principal
│   └── AdminUserDetailsPage.jsx  # Detalles de usuario
├── services/           # Servicios de API
│   └── adminService.js          # Llamadas a endpoints de admin
└── styles/            # Estilos CSS
    ├── adminDashboard.css       # Estilos generales
    ├── usersList.css            # Estilos de tabla de usuarios
    └── metricsCard.css          # Estilos de métricas
```

## Funcionalidades

### Dashboard Principal (`/admin/home`)
- **Vista general** con estadísticas de usuarios
- **Métricas** por rol y estado de cuenta
- **Tabla de usuarios** con acciones rápidas

### Gestión de Usuarios
- **Listar usuarios** - GET `/api/admin/users`
- **Ver detalles** - GET `/api/admin/users/{userId}`
- **Cambiar estado** - PATCH `/api/admin/users/{userId}/status`

### Estadísticas
- Total de usuarios
- Distribución por rol (Admin, Cliente, Trabajador)
- Distribución por estado (Activo, Pendiente, Suspendido, Eliminado)

## Componentes

### `UserStatusForm`
Formulario para cambiar el estado de cuenta de un usuario.

```jsx
<UserStatusForm
  user={user}
  onStatusUpdated={handleRefresh}
  onCancel={handleCancel}
/>
```

### `UsersList`
Tabla con listado de usuarios y acciones.

```jsx
<UsersList
  users={users}
  onUserUpdated={handleRefresh}
/>
```

### `MetricsCard`
Tarjetas mostrando las métricas de usuarios.

```jsx
<MetricsCard
  metrics={metrics}
  isLoading={isLoading}
  error={error}
/>
```

## Hooks

### `useAdminUsers()`
Obtiene la lista de usuarios.

```jsx
const { users, isLoading, error } = useAdminUsers();
```

### `useAdminMetrics()`
Obtiene las métricas de usuarios.

```jsx
const { metrics, isLoading, error } = useAdminMetrics();
```

## Endpoints de API utilizados

- `GET /api/admin/users` - Lista todos los usuarios
- `GET /api/admin/users/{id}` - Obtiene un usuario específico
- `PATCH /api/admin/users/{id}/status` - Cambia el estado de un usuario
- `GET /api/admin/metrics` - Obtiene métricas

## Rutas

- `/admin/home` - Dashboard principal (protegida por rol ADMIN)
- `/admin/users/:userId` - Detalles del usuario (protegida por rol ADMIN)
