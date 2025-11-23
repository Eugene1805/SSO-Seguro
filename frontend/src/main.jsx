import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { AuthProvider } from "react-oidc-context";

const oidcConfig = {
  authority: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo",
  client_id: "react-portal",
  redirect_uri: window.location.origin,
  response_type: "code", 
  scope: "openid profile email",
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </React.StrictMode>,
)