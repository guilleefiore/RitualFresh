import { BrowserRouter } from 'react-router-dom';

// Global providers live here: router, theme, query client, auth context, etc.
export function AppProviders({ children }) {
  return <BrowserRouter>{children}</BrowserRouter>;
}
