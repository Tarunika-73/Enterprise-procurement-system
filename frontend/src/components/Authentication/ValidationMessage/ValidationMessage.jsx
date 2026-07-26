const ValidationMessage = ({ message, id }) => {
  if (!message) return null;

  return (
    <div id={id} className="invalid-feedback d-block auth-validation-msg" role="alert">
      <i className="bi bi-exclamation-circle me-1" aria-hidden="true" />
      {message}
    </div>
  );
};

export default ValidationMessage;
