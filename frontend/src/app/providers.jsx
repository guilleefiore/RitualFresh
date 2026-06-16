import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../modules/auth/context/AuthContext.jsx';

// Global providers live here: router, theme, query client, auth context, etc.
export function AppProviders({ children }) {
  return (
    <BrowserRouter>
      <AuthProvider>{children}</AuthProvider>
    </BrowserRouter>
  );
}
