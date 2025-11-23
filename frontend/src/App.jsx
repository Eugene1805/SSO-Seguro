import { useAuth } from "react-oidc-context";

function App() {
  const auth = useAuth();

  // 1. Estado de Carga
  if (auth.isLoading) {
    return <div className="flex justify-center items-center h-screen bg-gray-800 text-white">Cargando autenticación...</div>;
  }

  // 2. Estado de Error
  if (auth.error) {
    return <div className="text-red-400 p-8 bg-gray-800 h-screen">Error de conexión: {auth.error.message}</div>;
  }

  // 3. Renderizado Principal
  if (auth.isAuthenticated) {

    const usuarioNombre = auth.user?.profile?.preferred_username || "Usuario";
    const rolesUsuario = auth.user?.profile?.realm_access?.roles || [];
    const esDocente = rolesUsuario.includes("docente");
    const token = auth.user?.access_token || "No disponible";

    return (
      <div className="min-h-screen bg-gray-800 flex flex-col">
        
        <nav className="bg-gray-900 text-white shadow-xl px-6 py-4 flex items-center justify-between z-10 border-b border-gray-700">
          <div className="w-1/3 flex justify-start">
            <img src="/logo.png" alt="Logo" className="h-24 w-auto object-contain" />
          </div>
          <div className="w-1/3 text-center">
            <h1 className="text-xl md:text-2xl font-bold tracking-wide text-white">Portal Escolar</h1>
          </div>
          <div className="w-1/3 flex justify-end">
            <button 
              onClick={() => auth.removeUser()} 
              className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded shadow transition duration-200 border border-red-800"
            >
              Cerrar Sesión
            </button>
          </div>
        </nav>

        <main className="flex-grow p-6 md:p-10">
          <div className="max-w-5xl mx-auto space-y-6">
            
            <div className={`bg-gray-700 rounded-lg shadow-2xl overflow-hidden border-t-4 ${esDocente ? 'border-purple-500' : 'border-blue-500'}`}>
              <div className="p-6">
                <h2 className="text-2xl font-bold text-white mb-2">¡Bienvenido, {usuarioNombre}!</h2>
              </div>
            </div>

            {esDocente && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-gray-700 p-6 rounded-lg shadow-xl border border-gray-600">
                  <h3 className="text-xl font-bold text-purple-400 mb-4 flex items-center gap-2">
                    🛠️ Panel Administrativo
                  </h3>
                  <div className="grid grid-cols-2 gap-4">
                    <button className="bg-gray-600 hover:bg-purple-600 text-white p-4 rounded-lg transition text-center border border-gray-500 hover:border-purple-400">
                      📝 Editar Notas
                    </button>
                    <button className="bg-gray-600 hover:bg-purple-600 text-white p-4 rounded-lg transition text-center border border-gray-500 hover:border-purple-400">
                      👥 Lista Alumnos
                    </button>
                    <button className="bg-gray-600 hover:bg-purple-600 text-white p-4 rounded-lg transition text-center border border-gray-500 hover:border-purple-400">
                      📊 Estadísticas
                    </button>
                    <button className="bg-gray-600 hover:bg-red-500 text-white p-4 rounded-lg transition text-center border border-gray-500 hover:border-red-400">
                      ⚠️ Auditoría
                    </button>
                  </div>
                </div>

                <div className="bg-gray-700 p-6 rounded-lg shadow-xl border border-gray-600">
                  <h3 className="text-xl font-bold text-gray-200 mb-4">Resumen del Curso</h3>
                  <ul className="space-y-3">
                    <li className="flex justify-between border-b border-gray-600 pb-2">
                      <span className="text-gray-400">Total Alumnos:</span>
                      <span className="text-white font-bold">45</span>
                    </li>
                    <li className="flex justify-between border-b border-gray-600 pb-2">
                      <span className="text-gray-400">Promedio General:</span>
                      <span className="text-green-400 font-bold">8.9</span>
                    </li>
                    <li className="flex justify-between pt-2">
                      <span className="text-gray-400">Pendientes de calificar:</span>
                      <span className="text-yellow-400 font-bold">3</span>
                    </li>
                  </ul>
                </div>
              </div>
            )}

            {!esDocente && (
              <div className="bg-gray-700 rounded-lg shadow-2xl p-6 border border-gray-600">
                <h3 className="text-xl font-bold text-blue-400 mb-4 flex items-center gap-2">
                  🎓 Mis Calificaciones
                </h3>
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="border-b border-gray-500">
                        <th className="p-3 text-gray-300">Materia</th>
                        <th className="p-3 text-gray-300">Profesor</th>
                        <th className="p-3 text-gray-300 text-right">Calificación</th>
                        <th className="p-3 text-gray-300 text-center">Estado</th>
                      </tr>
                    </thead>
                    <tbody className="text-gray-200">
                      <tr className="border-b border-gray-600 hover:bg-gray-600/50">
                        <td className="p-3">Seguridad de Software</td>
                        <td className="p-3 text-sm text-gray-400">Eugenio</td>
                        <td className="p-3 text-right font-bold text-green-400">10.0</td>
                        <td className="p-3 text-center"><span className="bg-green-900 text-green-200 text-xs px-2 py-1 rounded">Aprobado</span></td>
                      </tr>
                      <tr className="border-b border-gray-600 hover:bg-gray-600/50">
                        <td className="p-3">Desarrollo Web</td>
                        <td className="p-3 text-sm text-gray-400">Uriel</td>
                        <td className="p-3 text-right font-bold text-blue-400">9.5</td>
                        <td className="p-3 text-center"><span className="bg-blue-900 text-blue-200 text-xs px-2 py-1 rounded">Cursando</span></td>
                      </tr>
                      <tr className="hover:bg-gray-600/50">
                        <td className="p-3">Base de Datos</td>
                        <td className="p-3 text-sm text-gray-400">Arturo</td>
                        <td className="p-3 text-right font-bold text-yellow-400">--</td>
                        <td className="p-3 text-center"><span className="bg-gray-600 text-gray-300 text-xs px-2 py-1 rounded">Pendiente</span></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            <div className="bg-gray-700 rounded-lg shadow-xl p-6 border border-gray-600 opacity-70 hover:opacity-100 transition">
              <h3 className="text-sm font-semibold text-gray-400 mb-2">Debug: Token de Acceso</h3>
              <div className="bg-gray-900 text-green-400 p-3 rounded text-xs font-mono overflow-auto h-24 border border-gray-700">
                {token}
              </div>
            </div>

          </div>
        </main>
      </div>
    );
  }

  // 4. Pantalla Login (Estado No Autenticado)
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-800">
      <img src="/logo.png" alt="Logo Escuela" className="w-48 h-auto mb-8 drop-shadow-2xl" />
      <div className="bg-gray-700 p-8 rounded-lg shadow-2xl max-w-md w-full text-center border border-gray-600">
        <h1 className="text-4xl font-bold mb-2 text-white">¡Bienvenido!</h1>
        <p className="text-gray-300 mb-8 text-lg">Sistema de Notas Escolar</p>
        <button 
          onClick={() => auth.signinRedirect()}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded transition shadow-lg border border-blue-500"
        >
          <span>🔐</span> Iniciar Sesión
        </button>
        <p className="mt-6 text-xs text-gray-400">Acceso Seguro</p>
      </div>
    </div>
  );
}

export default App;