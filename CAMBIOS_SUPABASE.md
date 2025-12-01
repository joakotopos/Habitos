# Resumen de Cambios - Integración con Supabase

## Cambios Realizados

### 1. Actualización de Layouts
- **activity_register.xml**: Agregado campo para nombre de usuario (`etRegisterName`) y correo electrónico (`etRegisterEmail`)
- **activity_login.xml**: Cambiado campo de usuario a correo electrónico (`etLoginEmail`)

### 2. Nuevos Modelos de Datos
- **Profile.kt**: Modelo para el perfil de usuario (id, email, name, created_at)
- **AuthModels.kt**: Modelos para autenticación:
  - `SignUpRequest`: Para registro de usuarios
  - `SignInRequest`: Para inicio de sesión
  - `AuthResponse`: Respuesta de autenticación con tokens
  - `AuthUser`: Información del usuario autenticado
  - `UserMetadata`: Metadatos del usuario (nombre)

### 3. Servicios de API
- **AuthApiService.kt**: Interfaz Retrofit para autenticación
  - `signUp()`: Registro de nuevos usuarios
  - `signIn()`: Inicio de sesión
  - `getProfile()`: Obtener perfil de usuario
  - `createProfile()`: Crear perfil en tabla profiles

### 4. Actualización de TaskApiService
- Agregado método `updateTask()`: Actualizar tareas existentes
- Agregado método `deleteTask()`: Eliminar tareas

### 5. Repositorios
- **AuthRepository.kt**: Manejo de operaciones de autenticación
  - Registro de usuarios con creación automática de perfil
  - Inicio de sesión
  - Obtención de perfil de usuario

- **TaskRepository.kt**: Actualizado con nuevas operaciones
  - `markTaskAsCompleted()`: Marcar tareas como completadas
  - `updateTask()`: Actualizar tareas
  - `deleteTask()`: Eliminar tareas

### 6. Gestión de Sesión
- **SessionManager.kt**: Clase para gestión centralizada de sesión
  - Guardar tokens de acceso y refresh
  - Almacenar información del usuario (ID, email, nombre)
  - Verificar estado de sesión
  - Limpiar sesión al cerrar sesión

### 7. Actualización de Activities
- **RegisterActivity.kt**: 
  - Integración completa con Supabase Auth
  - Validación de email
  - Registro con nombre, email y contraseña
  - Guardado automático de sesión tras registro exitoso

- **LoginActivity.kt**:
  - Integración completa con Supabase Auth
  - Login con email y contraseña
  - Obtención automática del perfil del usuario
  - Guardado de sesión tras login exitoso

- **MainActivity.kt**:
  - Uso de SessionManager para verificar sesión
  - Obtención del userId real de Supabase
  - Cierre de sesión con limpieza de tokens

### 8. Actualización de Fragments
- **CreateTaskFragment.kt**:
  - Uso del userId real de la sesión
  - Validación de sesión antes de crear tareas

- **DailyTasksFragment.kt**:
  - Uso del userId real de la sesión
  - Implementación de marcado de tareas como completadas
  - Actualización en tiempo real con Supabase
  - Eliminado método `newInstance()` ya no necesario

- **WeeklyTasksFragment.kt**:
  - Uso del userId real de la sesión
  - Implementación de marcado de tareas como completadas
  - Actualización en tiempo real con Supabase
  - Eliminado método `newInstance()` ya no necesario

### 9. Actualización de SupabaseClient
- Agregado Retrofit separado para Auth API
- Exposición de `authApiService` además de `taskApiService`

### 10. Limpieza de Código
- Eliminado `Task.kt` duplicado de la raíz
- Eliminado `PlaceholderFragment.kt` no utilizado
- Eliminadas dependencias de SharedPreferences local para usuarios

## Configuración de Supabase Requerida

### Tablas en Supabase:
1. **profiles** (debe existir o crearse):
   - `id` (uuid, primary key) - Referencia a auth.users
   - `email` (text)
   - `name` (text)
   - `created_at` (timestamptz)

2. **tasks** (ya existente):
   - `id` (uuid, primary key)
   - `user_id` (uuid) - Referencia a auth.users
   - `title` (text)
   - `description` (text)
   - `type` (text) - "daily" o "weekly"
   - `is_completed` (boolean)
   - `created_at` (timestamptz)
   - `updated_at` (timestamptz)

### Políticas de Seguridad (RLS):
Asegúrate de que las siguientes políticas estén configuradas:

**Para tabla profiles:**
```sql
-- Permitir insertar perfil propio
CREATE POLICY "Users can insert their own profile"
ON profiles FOR INSERT
WITH CHECK (auth.uid() = id);

-- Permitir ver perfil propio
CREATE POLICY "Users can view their own profile"
ON profiles FOR SELECT
USING (auth.uid() = id);

-- Permitir actualizar perfil propio
CREATE POLICY "Users can update their own profile"
ON profiles FOR UPDATE
USING (auth.uid() = id);
```

**Para tabla tasks:**
```sql
-- Permitir ver tareas propias
CREATE POLICY "Users can view their own tasks"
ON tasks FOR SELECT
USING (auth.uid() = user_id);

-- Permitir crear tareas propias
CREATE POLICY "Users can create their own tasks"
ON tasks FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Permitir actualizar tareas propias
CREATE POLICY "Users can update their own tasks"
ON tasks FOR UPDATE
USING (auth.uid() = user_id);

-- Permitir eliminar tareas propias
CREATE POLICY "Users can delete their own tasks"
ON tasks FOR DELETE
USING (auth.uid() = user_id);
```

## Dependencias
Todas las dependencias ya estaban en el proyecto:
- Retrofit 2.9.0
- Gson Converter 2.9.0
- OkHttp Logging Interceptor 4.12.0
- Kotlin Coroutines 1.7.3
- Lifecycle Runtime KTX 2.7.0

## Notas Importantes
1. La aplicación ahora requiere conexión a internet para funcionar
2. Todos los datos se almacenan en Supabase, no localmente
3. La autenticación usa JWT tokens de Supabase
4. El permiso `INTERNET` ya está configurado en AndroidManifest.xml

## Pruebas Recomendadas
1. Registrar nuevo usuario con nombre, email y contraseña
2. Verificar que el perfil se crea en Supabase
3. Cerrar sesión y volver a iniciar sesión
4. Crear tareas diarias y semanales
5. Marcar tareas como completadas
6. Verificar que las tareas se actualicen en Supabase
7. Cerrar la app y volver a abrirla (debe mantener sesión)

## Compilación
Para compilar el proyecto, ábrelo en Android Studio y:
1. Sync Gradle
2. Build > Make Project
3. Run en un dispositivo o emulador

Si hay problemas con Gradle desde terminal, compila desde Android Studio.

