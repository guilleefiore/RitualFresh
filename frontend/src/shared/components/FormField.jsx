import '../styles/form-controls.css';

export function FormField({ className = '', label, icon, ...props }) {
  const classes = className ? `form-field ${className}` : 'form-field';

  return (
    <label className={classes}>
      <span className="form-field__label">{label}</span>
      <div className="form-field__control">
        {icon ? <span className="form-field__icon">{icon}</span> : null}
        <input className="form-field__input" {...props} />
      </div>
    </label>
  );
}
