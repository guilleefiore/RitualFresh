const STATUS_LABELS = {
  PENDING: 'Pendiente',
  COMPLETED: 'Completado',
  CANCELLED: 'Cancelado',
};

export function HistoryStatusBadge({ status }) {
  return (
    <span className={`history-status history-status--${status?.toLowerCase() || 'unknown'}`}>
      {STATUS_LABELS[status] || status}
    </span>
  );
}
