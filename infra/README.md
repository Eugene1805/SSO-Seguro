# Infraestructura SSO - Equipo 5

Este directorio contiene la configuración de infraestructura para el proyecto de SSO con OpenID Connect. El entorno está desplegado en un servidor Ubuntu 24.04 virtualizado y expuesto mediante túneles seguros (Ngrok).

## 🏗️ Arquitectura del Sistema

* **OS:** Ubuntu Server 24.04 LTS (Hyper-V).
* **Identity Provider (IdP):** Keycloak 26.0.2 (Quarkus Distribution).
* **Base de Datos:** MySQL 8.0 (Backend compartido para Keycloak y API).
* **Reverse Proxy:** Nginx (Manejo de cabeceras y redirección de puertos).
* **Exposición:** Ngrok (Túnel HTTP/HTTPS y TCP).

## 🌐 Accesos y Credenciales (Desarrollo)

### 1. Keycloak (Identity Provider)
* **URL Base (Auth):** `https://unharrowed-nadene-prefamiliarly.ngrok-free.dev/auth`
* **Realm:** `demo`
* **Consola Admin:** `/auth/admin/`
* **Credenciales Admin:** *(Solicitar al SysAdmin por canal seguro)*

### 2. Configuración OIDC (Para Desarrolladores)

| Servicio | Client ID | Tipo de Acceso | Flujo |
| :--- | :--- | :--- | :--- |
| **Frontend (React)** | `react-portal` | Público | PKCE (Standard) |
| **Backend (Spring)** | `spring-api` | Confidencial | Bearer-only / Client Secret |

> **Nota:** El `Client Secret` del backend se comparte vía `.env` privado.

### 3. Base de Datos (MySQL)
Accesible remotamente vía túnel TCP de Ngrok.

* **Host:** `0.tcp.ngrok.io` (Puede variar, consultar SysAdmin)
* **Puerto:** *(Dinámico según Ngrok, ej. 15432)*
* **Base de Datos:** `portal_notas`
* **Usuario:** `app_user`
* **Password:** ` ` (Consultar con SysAdmin)
* **Driver:** `com.mysql.cj.jdbc.Driver`

---

## 📂 Archivos de Configuración

La infraestructura se basa en los siguientes archivos clave ubicados en este repositorio:

### Nginx (`/etc/nginx/sites-available/default`)
Configurado como Proxy Inverso para manejar el tráfico de Ngrok y evitar bucles de redirección SSL.
* Ver archivo: `./nginx/default.conf`

### Keycloak (`/opt/keycloak/conf/keycloak.conf`)
Configurado para producción con MySQL y soporte de Proxy (X-Forwarded-Proto).
* Ver archivo: `./keycloak/keycloak.conf`
* **Importante:** Habilitar `http-enabled=true` y `proxy-headers=xforwarded`.

### Systemd Service (`/etc/systemd/system/keycloak.service`)
Script para arranque automático y resiliente del servicio.
* Ver archivo: `./keycloak/keycloak.service`

---

## 🚀 Guía de Despliegue (Reproducción)

Para levantar esta infraestructura desde cero en una nueva VM Ubuntu:

1.  **Prerrequisitos:**
    ```bash
    sudo apt update && sudo apt install -y nginx mysql-server openjdk-21-jdk unzip
    ```

2.  **Base de Datos:**
    Ejecutar el script `./mysql/init.sql` para crear bases de datos y usuarios.

3.  **Keycloak:**
    * Descargar Keycloak 26.
    * Instalar conector MySQL en la carpeta `providers`.
    * Copiar `./keycloak/keycloak.conf` a `/opt/keycloak/conf/`.
    * Ejecutar `./kc.sh build`.

4.  **Túneles (Ngrok):**
    Se requieren dos sesiones de terminal (o servicios systemd):
    ```bash
    # Web (HTTPS)
    ngrok http --domain=unharrowed-nadene-prefamiliarly.ngrok-free.dev 80
    
    # Base de Datos (TCP)
    ngrok tcp 3306
    ```

## 🛠️ Comandos Útiles (Troubleshooting)

Verificar estado de servicios:
```bash
sudo systemctl status keycloak
sudo systemctl status nginx
```
Ver logs en tiempo real:
```Bash
sudo journalctl -u keycloak -f
```
Reiniciar pila completa:
```Bash
sudo systemctl restart nginx keycloak
```