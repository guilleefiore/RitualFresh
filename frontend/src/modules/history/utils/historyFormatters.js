export function formatAmount(value) {
  if (value === null || value === undefined) return 'Importe no disponible';
  return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(Number(value));
}
