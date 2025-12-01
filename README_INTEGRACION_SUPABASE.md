# App Hábitos - Integración con Supabase

## 🎯 Descripción
Aplicación de gestión de hábitos desarrollada en Kotlin para Android, totalmente integrada con Supabase para autenticación y almacenamiento de datos.

## ✅ Funcionalidades Implementadas

### Autenticación
- ✅ Registro de usuarios con nombre, email y contraseña
- ✅ Validación de email
- ✅ Inicio de sesión con email y contraseña
- ✅ Gestión de sesión con tokens JWT
- ✅ Persistencia de sesión (auto-login)
- ✅ Cierre de sesión

### Gestión de Tareas
- ✅ Crear tareas diarias y semanales
- ✅ Ver tareas por tipo (diarias/semanales)
- ✅ Marcar tareas como completadas
- ✅ Animación de confeti al completar tareas
- ✅ Sincronización en tiempo real con Supabase

## 🚀 Configuración

### 1. Configurar Supabase

1. **Ejecutar el script SQL**:
   - Ve a tu proyecto en Supabase
   - Abre el Editor SQL
   - Copia y ejecuta el contenido de `supabase_setup.sql`

2. **Verificar configuración**:
   - Asegúrate de que las tablas `profiles` y `tasks` estén creadas
   - Verifica que Row Level Security (RLS) esté habilitado
   - Confirma que las políticas de seguridad estén activas

### 2. Configurar la App

La app ya está configurada con:
- **URL de Supabase**: `https://pairixgqshzufmtnxqqj.supabase.co`
- **API Key**: Ya incluida en el código

Si necesitas cambiar estas credenciales:
1. Abre `app/src/main/java/com/example/habitos/data/network/SupabaseClient.kt`
2. Modifica `BASE_URL` y `supabaseKey`

### 3. Compilar y Ejecutar

1. **Abrir en Android Studio**:
   ```
   File > Open > Selecciona la carpeta del proyecto
   ```

2. **Sync Gradle**:
   ```
   File > Sync Project with Gradle Files
   ```

3. **Compilar**:
   ```
   Build > Make Project
   ```

4. **Ejecutar**:
   - Conecta un dispositivo Android o inicia un emulador
   - Click en Run (▶️)

## 📱 Uso de la Aplicación

### Primer Uso

1. **Registrarse**:
   - Abre la app
   - Click en "no tienes cuenta? crea una aqui"
   - Ingresa:
     - Nombre de usuario
     - Correo electrónico
     - Contraseña (mínimo 6 caracteres)
     - Confirmar contraseña
   - Click en "Registrarse"
   - Serás redirigido automáticamente a la pantalla principal

2. **Crear Tareas**:
   - En la pantalla principal, usa el menú inferior
   - Selecciona el ícono central "+"
   - Ingresa:
     - Título de la tarea
     - Descripción (opcional)
     - Tipo: Diaria o Semanal
   - Click en "Crear Tarea"

3. **Ver Tareas**:
   - **Tareas Diarias**: Primer ícono del menú inferior
   - **Tareas Semanales**: Tercer ícono del menú inferior

4. **Completar Tareas**:
   - En la lista de tareas, marca el checkbox
   - ¡Disfruta del confeti! 🎉

5. **Cerrar Sesión**:
   - Click en el botón "Cerrar Sesión" en la parte superior

### Sesiones Posteriores

- La app recordará tu sesión
- Al abrir la app, irás directo a la pantalla principal
- No necesitas iniciar sesión nuevamente

## 🏗️ Arquitectura del Proyecto

```
app/src/main/java/com/example/habitos/
├── data/
│   ├── model/
│   │   ├── Task.kt           # Modelo de tarea
│   │   ├── Profile.kt        # Modelo de perfil
│   │   └── AuthModels.kt     # Modelos de autenticación
│   ├── network/
│   │   ├── SupabaseClient.kt    # Cliente Retrofit
│   │   ├── TaskApiService.kt    # API de tareas
│   │   └── AuthApiService.kt    # API de autenticación
│   ├── TaskRepository.kt     # Repositorio de tareas
│   └── AuthRepository.kt     # Repositorio de autenticación
├── SessionManager.kt         # Gestión de sesión
├── LoginActivity.kt          # Pantalla de login
├── RegisterActivity.kt       # Pantalla de registro
├── MainActivity.kt           # Pantalla principal
├── CreateTaskFragment.kt     # Fragmento crear tarea
├── DailyTasksFragment.kt     # Fragmento tareas diarias
├── WeeklyTasksFragment.kt    # Fragmento tareas semanales
└── TaskAdapter.kt            # Adaptador RecyclerView
```

## 🔐 Seguridad

- ✅ Autenticación JWT con Supabase
- ✅ Row Level Security (RLS) en todas las tablas
- ✅ Usuarios solo pueden ver y modificar sus propios datos
- ✅ Contraseñas hasheadas por Supabase (nunca se almacenan en texto plano)
- ✅ Tokens de acceso y refresh almacenados de forma segura

## 🗄️ Estructura de Base de Datos

### Tabla: `profiles`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | ID del usuario (FK a auth.users) |
| email | TEXT | Email del usuario |
| name | TEXT | Nombre del usuario |
| created_at | TIMESTAMPTZ | Fecha de creación |

### Tabla: `tasks`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | ID de la tarea |
| user_id | UUID | ID del usuario (FK a auth.users) |
| title | TEXT | Título de la tarea |
| description | TEXT | Descripción de la tarea |
| type | TEXT | Tipo: "daily" o "weekly" |
| is_completed | BOOLEAN | Estado de completitud |
| created_at | TIMESTAMPTZ | Fecha de creación |
| updated_at | TIMESTAMPTZ | Fecha de actualización |

## 🔧 Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **UI**: XML Layouts, ViewBinding
- **Async**: Kotlin Coroutines
- **Networking**: Retrofit, OkHttp
- **JSON**: Gson
- **Backend**: Supabase (Auth + Database)
- **Animaciones**: Konfetti

## 📝 Notas Importantes

1. **Conexión a Internet**: La app requiere conexión a internet para funcionar
2. **Primer Registro**: Al registrarte, se crea automáticamente tu perfil en Supabase
3. **Persistencia**: La sesión se mantiene incluso si cierras la app
4. **Actualización en Tiempo Real**: Las tareas se sincronizan inmediatamente con Supabase

## ❓ Solución de Problemas

### Error: "No se pudo crear la tarea"
- Verifica tu conexión a internet
- Asegúrate de que las políticas RLS estén configuradas correctamente

### Error: "Error de sesión"
- Cierra sesión y vuelve a iniciar
- Verifica que tu token no haya expirado

### La app no compila
- Sync Gradle: `File > Sync Project with Gradle Files`
- Clean Build: `Build > Clean Project`
- Rebuild: `Build > Rebuild Project`

### Error de autenticación
- Verifica que la URL y API Key de Supabase sean correctas
- Confirma que el servicio de Auth esté habilitado en Supabase

## 📄 Archivos de Referencia

- `CAMBIOS_SUPABASE.md`: Detalle completo de todos los cambios realizados
- `supabase_setup.sql`: Script SQL para configurar la base de datos

## 🎉 ¡Listo!

Tu app de hábitos está completamente integrada con Supabase y lista para usar.

