import '../styles/form-controls.css';

export function FormField({ className = '', label, icon, error, ...props }) {
  const classes = className ? `form-field ${className}` : 'form-field';
  const { multiline, rows = 4, required, ...inputProps } = props;

  return (
    <label className={classes}>
      <span className="form-field__label">{label}{required ? <span className="required-asterisk"> *</span> : null}</span>
      <div className="form-field__control">
        {icon ? <span className="form-field__icon">{icon}</span> : null}
        {multiline ? (
          <textarea className="form-field__input form-field__input--textarea" rows={rows} required={required} {...inputProps} />
        ) : (
          <input className="form-field__input" required={required} {...inputProps} />
        )}
      </div>
      {error ? <span className="form-field__error">{error}</span> : null}
    </label>
  );
}
