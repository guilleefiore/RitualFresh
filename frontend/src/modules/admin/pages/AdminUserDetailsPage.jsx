import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getUser } from '../services/adminService.js';
import { UserStatusForm } from '../components/UserStatusForm.jsx';

const ROLE_LABELS = {
  ADMIN: 'Administrador',
  CLIENT: 'Cliente',
  WORKER: 'Trabajador',
};

const STATUS_LABELS = {
  ACTIVE: 'Activo',
  PENDING_VALIDATION: 'Pendiente de validación',
  SUSPENDED: 'Suspendido',
  DELETED: 'Eliminado',
};

export function AdminUserDetailsPage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showStatusForm, setShowStatusForm] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        setIsLoading(true);
        const data = await getUser(userId);
        setUser(data);
        setError(null);
      } catch (err) {
        setError(err.message || 'Error al cargar el usuario');
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUser();
  }, [userId]);

  const handleStatusUpdated = async () => {
    // Recargar los datos del usuario
    try {
      const updatedUser = await getUser(userId);
      setUser(updatedUser);
      setShowStatusForm(false);
    } catch (err) {
      setError('Error al actualizar los datos');
    }
  };

  const handleBack = () => {
    navigate('/admin/home');
  };

  if (isLoading) {
    return <main className="admin-page"><p>Cargando...</p></main>;
  }

  if (error) {
    return (
      <main className="admin-page">
        <p className="error-message">{error}</p>
        <button onClick={handleBack}>Volver</button>
      </main>
    );
  }

  if (!user) {
    return (
      <main className="admin-page">
        <p>Usuario no encontrado</p>
        <button onClick={handleBack}>Volver</button>
      </main>
    );
  }

  return (
    <main className="admin-page">
      <button onClick={handleBack} className="btn-back">
        ← Volver
      </button>

      <section className="user-details">
        <h1>Detalles del Usuario</h1>

        <div className="user-info">
          <div className="info-group">
            <label>ID:</label>
            <p>{user.id}</p>
          </div>

          <div className="info-group">
            <label>Nombre:</label>
            <p>{user.firstName} {user.lastName}</p>
          </div>

          <div className="info-group">
            <label>Email:</label>
            <p>{user.email}</p>
          </div>

          <div className="info-group">
            <label>Rol:</label>
            <p>{ROLE_LABELS[user.role] || user.role}</p>
          </div>

          <div className="info-group">
            <label>Estado:</label>
            <p>{STATUS_LABELS[user.accountStatus] || user.accountStatus}</p>
          </div>

          <div className="info-group">
            <label>Creado:</label>
            <p>{new Date(user.createdAt).toLocaleString()}</p>
          </div>

          {user.deactivatedAt && (
            <div className="info-group">
              <label>Desactivado:</label>
              <p>{new Date(user.deactivatedAt).toLocaleString()}</p>
            </div>
          )}
        </div>

        <section className="user-actions">
          <h2>Cambiar Estado de Cuenta</h2>
          {showStatusForm ? (
            <UserStatusForm
              user={user}
              onStatusUpdated={handleStatusUpdated}
              onCancel={() => setShowStatusForm(false)}
            />
          ) : (
            <button onClick={() => setShowStatusForm(true)} className="btn btn-primary">
              Cambiar estado
            </button>
          )}
        </section>
      </section>
    </main>
  );
}
