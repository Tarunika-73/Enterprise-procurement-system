import { useEffect, useRef, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { askProcurementAssistant } from '../services/procurementAssistantService';
import { unwrapApiData } from '../utils/employeeHelpers';
import { getApiErrorMessage } from '../utils/apiErrors';

const ProcurementAssistant = () => {
  const { isAuthenticated } = useAuth();
  const [open, setOpen] = useState(false);
  const [question, setQuestion] = useState('');
  const [messages, setMessages] = useState([]);
  const [sending, setSending] = useState(false);
  const endRef = useRef(null);
  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, sending]);

  if (!isAuthenticated) return null;

  const send = async (event) => {
    event.preventDefault();
    const text = question.trim();
    if (!text || sending) return;
    setQuestion('');
    setMessages((current) => [...current, { sender: 'user', text }]);
    setSending(true);
    try {
      const history = messages.slice(-6).map((message) => ({
        role: message.sender === 'user' ? 'user' : 'assistant',
        content: String(message.text ?? '').slice(0, 500),
      }));
      const response = unwrapApiData(await askProcurementAssistant(text, history));
      const assistantText = typeof response?.message === 'string' && response.message.trim()
        ? response.message
        : 'Unable to get a response right now. Please try again.';
      setMessages((current) => [...current, { sender: 'assistant', text: assistantText }]);
    } catch (error) {
      const errorText = getApiErrorMessage(error, 'Unable to get a response right now. Please try again.');
      setMessages((current) => [...current, {
        sender: 'assistant',
        text: typeof errorText === 'string' ? errorText : 'Unable to get a response right now. Please try again.',
      }]);
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="procurement-assistant">
      {open && (
        <section className="procurement-assistant-panel" aria-label="AI Procurement Assistant">
          <header className="procurement-assistant-header">
            <span><i className="bi bi-robot me-2" aria-hidden="true" />Procurement Assistant</span>
            <button type="button" className="btn-close btn-close-white" aria-label="Close assistant" onClick={() => setOpen(false)} />
          </header>
          <div className="procurement-assistant-messages" aria-live="polite">
            {messages.length === 0 && <p className="assistant-intro">Ask about your authorized procurement activity.</p>}
            {messages.map((message, index) => <div key={`${message.sender}-${index}`} className={`assistant-message ${message.sender}`}>{String(message.text ?? '')}</div>)}
            {sending && <div className="assistant-message assistant"><span className="spinner-border spinner-border-sm me-2" />Checking…</div>}
            <div ref={endRef} />
          </div>
          <form className="procurement-assistant-form" onSubmit={send}>
            <label className="visually-hidden" htmlFor="assistant-question">Ask the procurement assistant</label>
            <input id="assistant-question" value={question} onChange={(event) => setQuestion(event.target.value)} maxLength="500" placeholder="Ask something…" disabled={sending} />
            <button type="submit" className="btn btn-primary" disabled={!question.trim() || sending} aria-label="Send question"><i className="bi bi-send" /></button>
          </form>
        </section>
      )}
      <button type="button" className="procurement-assistant-toggle" onClick={() => setOpen((value) => !value)} aria-label={open ? 'Close procurement assistant' : 'Open procurement assistant'} aria-expanded={open}>
        <i className={`bi ${open ? 'bi-x-lg' : 'bi-robot'}`} aria-hidden="true" />
      </button>
    </div>
  );
};

export default ProcurementAssistant;
