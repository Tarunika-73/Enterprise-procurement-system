import api from './api';

export const askProcurementAssistant = (question, history = []) =>
  api.post('/v1/procurement-assistant/ask', { question, history }).then((response) => response.data);
