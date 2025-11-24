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
  
  metadata: {
    issuer: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo",
    authorization_endpoint: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo/protocol/openid-connect/auth",
    token_endpoint: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo/protocol/openid-connect/token",
    userinfo_endpoint: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo/protocol/openid-connect/userinfo",
    end_session_endpoint: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo/protocol/openid-connect/logout",
    jwks_uri: "https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth/realms/demo/protocol/openid-connect/certs",
  }
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </React.StrictMode>,
)