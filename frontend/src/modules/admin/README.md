# Módulo Admin

El módulo `admin` concentra la supervisión operativa de cuentas de RitualFresh. Todas sus rutas requieren rol `ADMIN` y comparten un shell propio con navegación lateral, resumen y directorio de usuarios.

## Rutas

- `/admin/home`: resumen con métricas agregadas y últimos usuarios registrados.
- `/admin/users`: directorio paginado con búsqueda por nombre o correo, filtros de rol y estado y ordenamiento.
- `/admin/users/:userId`: detalle de cuenta, transiciones permitidas e historial de cambios de estado.

El listado y el detalle excluyen cuentas `ADMIN`: un administrador no puede consultar ni modificar a otro administrador desde el panel.

## Estructura

```text
admin/
├── components/
│   ├── AdminLayout.jsx
│   ├── MetricsCard.jsx
│   ├── UsersList.jsx
│   └── UserStatusForm.jsx
├── hooks/
│   └── useAdminData.js
├── pages/
│   ├── AdminDashboard.jsx
│   ├── AdminUsersPage.jsx
│   └── AdminUserDetailsPage.jsx
├── services/
│   └── adminService.js
└── styles/
    ├── adminDashboard.css
    ├── metricsCard.css
    └── usersList.css
```

## API utilizada

- `GET /api/admin/metrics`: métricas agregadas por rol y estado.
- `GET /api/admin/users`: búsqueda, filtros, ordenamiento y paginación del lado del servidor.
- `GET /api/admin/users/{id}`: detalle y transiciones de estado habilitadas.
- `PATCH /api/admin/users/{id}/status`: cambio de estado con motivo obligatorio.
- `GET /api/admin/users/{id}/status-history`: historial paginado de cambios.

Parámetros admitidos por el listado: `query`, `role`, `status`, `page`, `size`, `sort` y `direction`. `useAdminUsers(filters, refreshKey)` encapsula esta consulta y devuelve `{ result, isLoading, error }`.

## Cambio de estado

El frontend no construye las transiciones por su cuenta: utiliza `allowedStatusTransitions` devuelto por el detalle. La confirmación requiere un motivo de hasta 500 caracteres y el backend guarda, en una misma transacción, el nuevo estado y el registro de auditoría con administrador, usuario afectado, estado anterior, estado nuevo y fecha.

Transiciones actuales:

- `PENDING_VALIDATION` → `ACTIVE`, `SUSPENDED` o `DELETED`.
- `ACTIVE` → `SUSPENDED` o `DELETED`.
- `SUSPENDED` → `ACTIVE` o `DELETED`.
- `DELETED` → `ACTIVE`.
