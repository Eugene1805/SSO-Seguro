import React, { useState, useEffect } from "react";
import { useAuth } from "react-oidc-context";

function App() {
  const auth = useAuth();
  
  // Estados para almacenar la información que vendrá del Backend
  // Se inicializan vacíos o nulos hasta que la API responda
  const [datosProfesor, setDatosProfesor] = useState({ materiaImpartida: "", alumnos: [] });
  const [datosAlumno, setDatosAlumno] = useState([]);
  const [cargandoDatos, setCargandoDatos] = useState(false);

  // 1. Estado de Carga (Autenticación)
  if (auth.isLoading) {
    return <div className="flex justify-center items-center h-screen bg-gray-800 text-white">Cargando autenticación...</div>;
  }

  // 2. Estado de Error
  if (auth.error) {
    return <div className="text-red-400 p-8 bg-gray-800 h-screen">Error de conexión: {auth.error.message}</div>;
  }

  // 3. Renderizado Principal (Solo si está autenticado)
  if (auth.isAuthenticated) {
    
    // Extracción de datos reales del token OIDC
    const usuarioNombre = auth.user?.profile?.preferred_username || auth.user?.profile?.name || "Usuario";
    const rolesUsuario = auth.user?.profile?.realm_access?.roles || [];
    const esDocente = rolesUsuario.includes("docente");
    const token = auth.user?.access_token;

    /* --- ZONA DE INTEGRACIÓN CON BACKEND ---
       Aquí es donde se usa el 'token' para pedir los datos reales a la API 
    */
    // eslint-disable-next-line react-hooks/rules-of-hooks
/* --- ZONA DE INTEGRACIÓN CON BACKEND --- */
    useEffect(() => {
        if (token) {
            setCargandoDatos(true);
            
            // TU BACKEND ESTÁ CORRIENDO EN LOCALHOST:8080
            const backendUrl = "http://localhost:8080";
            
            const endpoint = esDocente ? '/api/profesor/mis-alumnos' : '/api/alumno/mis-calificaciones';
            const urlCompleta = `${backendUrl}${endpoint}`;

            fetch(urlCompleta, { headers: { Authorization: `Bearer ${token}` } })
                .then(res => {
                    if (!res.ok) throw new Error("Error en la respuesta del servidor");
                    return res.json();
                })
                .then(data => {
                    if (esDocente) setDatosProfesor(data);
                    else setDatosAlumno(data);
                })
                .catch(err => {
                    console.error(err);
                    // Opcional: mostrar error en UI
                })
                .finally(() => setCargandoDatos(false));
        }
    }, [token, esDocente]);

    return (
      <div className="min-h-screen bg-gray-800 flex flex-col">
        
        {/* --- NAVBAR --- */}
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

        {/* --- CONTENIDO --- */}
        <main className="flex-grow p-6 md:p-10">
          <div className="max-w-5xl mx-auto space-y-6">
            
            {/* Tarjeta de Bienvenida */}
            <div className={`bg-gray-700 rounded-lg shadow-2xl overflow-hidden border-t-4 ${esDocente ? 'border-purple-500' : 'border-blue-500'}`}>
              <div className="p-6 flex justify-between items-center">
                <div>
                  <h2 className="text-2xl font-bold text-white">¡Hola, {usuarioNombre}!</h2>
                  <p className="text-gray-400 text-sm mt-1">
                    {esDocente 
                        ? `Gestionando materia: ${datosProfesor.materiaImpartida || "Cargando..."}` 
                        : "Consulta tus calificaciones parciales"
                    }
                  </p>
                </div>
                {/* Icono decorativo según rol */}
                <div className="text-4xl opacity-20">
                  {esDocente ? "👨‍🏫" : "👨‍🎓"}
                </div>
              </div>
            </div>

            {/* Spinner de carga de datos (opcional) */}
            {cargandoDatos && <div className="text-white text-center">Obteniendo información...</div>}

            {/* --- VISTA DE PROFESOR --- */}
            {esDocente && !cargandoDatos && (
              <div className="bg-gray-700 rounded-lg shadow-xl border border-gray-600">
                <div className="p-6 border-b border-gray-600 flex justify-between items-center">
                  <h3 className="text-xl font-bold text-purple-400 flex items-center gap-2">
                    📋 Lista de Estudiantes
                  </h3>
                  <span className="bg-gray-800 text-gray-300 px-3 py-1 rounded text-sm border border-gray-600">
                    Materia: {datosProfesor.materiaImpartida || "---"}
                  </span>
                </div>
                
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="bg-gray-800 text-gray-400 text-sm uppercase tracking-wider">
                        <th className="p-4 border-b border-gray-600">ID</th>
                        <th className="p-4 border-b border-gray-600">Nombre del Estudiante</th>
                        <th className="p-4 border-b border-gray-600 text-center">Calificación</th>
                        <th className="p-4 border-b border-gray-600 text-center">Estado</th>
                        <th className="p-4 border-b border-gray-600 text-right">Acciones</th>
                      </tr>
                    </thead>
                    <tbody className="text-gray-200 divide-y divide-gray-600">
                      {datosProfesor.alumnos.length === 0 ? (
                          <tr>
                              <td colSpan="5" className="p-8 text-center text-gray-400">No hay alumnos registrados o no se pudo cargar la lista.</td>
                          </tr>
                      ) : (
                          datosProfesor.alumnos.map((alumno) => (
                            <tr key={alumno.id} className="hover:bg-gray-600/50 transition">
                              <td className="p-4 font-mono text-gray-500">#{alumno.id}</td>
                              <td className="p-4 font-medium">{alumno.nombre}</td>
                              <td className="p-4 text-center">
                                <span className={`font-bold ${alumno.calificacion < 6 ? 'text-red-400' : 'text-white'}`}>
                                  {alumno.calificacion}
                                </span>
                              </td>
                              <td className="p-4 text-center">
                                {alumno.aprobado ? (
                                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-900 text-green-200 border border-green-700">Aprobado</span>
                                ) : (
                                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-900 text-red-200 border border-red-700">Reprobado</span>
                                )}
                              </td>
                              <td className="p-4 text-right">
                                <button className="text-purple-400 hover:text-purple-300 text-sm font-medium hover:underline">
                                  Editar Nota
                                </button>
                              </td>
                            </tr>
                          ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* --- VISTA DE ALUMNO --- */}
            {!esDocente && !cargandoDatos && (
              <div className="bg-gray-700 rounded-lg shadow-xl border border-gray-600">
                <div className="p-6 border-b border-gray-600">
                  <h3 className="text-xl font-bold text-blue-400 flex items-center gap-2">
                    🎓 Boleta de Calificaciones
                  </h3>
                </div>
                
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="bg-gray-800 text-gray-400 text-sm uppercase tracking-wider">
                        <th className="p-4 border-b border-gray-600">Materia</th>
                        <th className="p-4 border-b border-gray-600">Profesor</th>
                        <th className="p-4 border-b border-gray-600 text-right">Calificación</th>
                      </tr>
                    </thead>
                    <tbody className="text-gray-200 divide-y divide-gray-600">
                      {datosAlumno.length === 0 ? (
                           <tr>
                               <td colSpan="3" className="p-8 text-center text-gray-400">No tienes materias asignadas o calificaciones registradas.</td>
                           </tr>
                      ) : (
                          datosAlumno.map((item) => (
                            <tr key={item.id} className="hover:bg-gray-600/50 transition">
                              <td className="p-4 font-medium text-white">{item.materia}</td>
                              <td className="p-4 text-gray-400 text-sm">{item.profesor}</td>
                              <td className="p-4 text-right">
                                <span className={`text-lg font-bold ${item.calificacion < 6 ? 'text-red-400' : 'text-green-400'}`}>
                                  {item.calificacion}
                                </span>
                              </td>
                            </tr>
                          ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

          </div>
        </main>
      </div>
    );
  }

  // 4. Pantalla Login (No autenticado)
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-800">
      <img src="/logo.png" alt="Logo Escuela" className="w-48 h-auto mb-8 drop-shadow-2xl" />
      <div className="bg-gray-700 p-8 rounded-lg shadow-2xl max-w-md w-full text-center border border-gray-600">
        <h1 className="text-4xl font-bold mb-2 text-white">¡Bienvenido!</h1>
        <p className="text-gray-300 mb-8 text-lg">Sistema de Notas Escolar</p>
        <button 
          onClick={() => auth.signinRedirect()}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded transition shadow-lg border border-blue-500 transform hover:-translate-y-0.5"
        >
          <span>🔐</span> Iniciar Sesión
        </button>
        <p className="mt-6 text-xs text-gray-400">Acceso Seguro OIDC</p>
      </div>
    </div>
  );
}

export default App;