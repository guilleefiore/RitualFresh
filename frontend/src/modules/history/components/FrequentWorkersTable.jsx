export function FrequentWorkersTable({ workers }) {
  return (
    <section className="statistics-panel statistics-panel--table" aria-labelledby="frequent-workers-title">
      <div className="statistics-panel__heading">
        <h2 id="frequent-workers-title">Trabajadores frecuentes</h2>
        <p>Los cinco trabajadores con más servicios completados.</p>
      </div>
      <div className="statistics-table-wrap">
        <table className="statistics-table">
          <thead>
            <tr>
              <th scope="col">Posición</th>
              <th scope="col">Trabajador</th>
              <th scope="col">Servicios</th>
            </tr>
          </thead>
          <tbody>
            {workers.map((worker, index) => (
              <tr key={worker.workerId}>
                <td>{index + 1}</td>
                <th scope="row">{worker.workerName}</th>
                <td>{worker.completedServices}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
