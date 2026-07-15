export function CategoryBars({ categories }) {
  const maxCount = Math.max(...categories.map((item) => item.count), 1);

  return (
    <section className="statistics-panel" aria-labelledby="categories-title">
      <div className="statistics-panel__heading">
        <h2 id="categories-title">Categorías utilizadas</h2>
        <p>Servicios completados por categoría.</p>
      </div>
      <div className="category-bars">
        {categories.map((item) => (
          <div className="category-bar" key={item.category}>
            <div className="category-bar__label">
              <span>{item.category}</span>
              <strong>{item.count}</strong>
            </div>
            <div
              className="category-bar__track"
              role="meter"
              aria-label={`${item.category}: ${item.count} servicios completados`}
              aria-valuemin="0"
              aria-valuemax={maxCount}
              aria-valuenow={item.count}
            >
              <span style={{ width: `${(item.count / maxCount) * 100}%` }} />
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
