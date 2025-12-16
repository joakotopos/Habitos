# 📱 Hábitos - App de Gestión de Tareas


## ✨ Características

### 🎯 Gestión de Hábitos
- **Tareas Diarias**: Organiza rutinas que realizas cada día
- **Tareas Semanales**: Planifica actividades semanales
- **Creación Rápida**: Interfaz simple para agregar nuevas tareas
- **Edición Intuitiva**: Marca, desmarca y elimina con facilidad

### 🎨 Interfaz Moderna
- **Tema Oscuro**: Diseño elegante que cuida tus ojos
- **Material Design 3**: Componentes modernos y animados
- **Notificaciones Burbuja**: Sistema personalizado de alertas flotantes
- **Animaciones Fluidas**: Transiciones suaves sin parpadeos

### 🎉 Experiencia de Usuario
- **Celebraciones con Confeti**: Efectos visuales al completar tareas
- **Organización Automática**: Separación inteligente de tareas pendientes y completadas
- **Sincronización en Tiempo Real**: Tus datos siempre actualizados
- **Actualizaciones Optimistas**: UI instantánea sin esperas

### 🔐 Seguridad
- **Autenticación Robusta**: Sistema de login/registro con Supabase
- **Sesiones Persistentes**: Mantén tu sesión activa de forma segura
- **Tokens JWT**: Autorización segura para todas las operaciones

---

## 🚀 Tecnologías

### Frontend & UI
- **Kotlin** - Lenguaje principal
- **Material Design 3** - Componentes UI modernos
- **View Binding** - Acceso seguro a vistas
- **RecyclerView** - Listas eficientes con DiffUtil
- **ConstraintLayout** - Layouts flexibles y responsive

### Backend & Datos
- **Supabase** - Backend as a Service (BaaS)
- **PostgreSQL** - Base de datos relacional
- **Retrofit 2** - Cliente HTTP para API REST
- **Gson** - Serialización/deserialización JSON
- **OkHttp** - Logging e interceptores

### Arquitectura & Patrones
- **MVVM** - Separación de responsabilidades
- **Repository Pattern** - Capa de abstracción de datos
- **Kotlin Coroutines** - Programación asíncrona
- **Lifecycle Components** - Gestión del ciclo de vida

### Animaciones & Efectos
- **Konfetti** - Efectos de celebración
- **Custom Animations** - Notificaciones burbuja personalizadas
- **Translate/Alpha Animations** - Transiciones fluidas

---

## 📋 Requisitos

- **Android 7.0 (API 24)** o superior
- **~50 MB** de espacio libre
- **Conexión a Internet** para sincronización

---

## 📥 Instalación

### Para Usuarios

1. **Descarga el APK** desde [Releases](https://github.com/joakotopos/Habitos/releases)
2. **Habilita instalación de fuentes desconocidas**:
   ```
   Configuración → Seguridad → Instalar apps desconocidas → Habilitar
   ```
3. **Instala el APK** descargado
4. **Abre la app** y comienza a crear tus hábitos

### Para Desarrolladores

```bash
# Clonar el repositorio
git clone https://github.com/joakotopos/Habitos.git

# Abrir en Android Studio
cd Habitos
# Abrir el proyecto en Android Studio

# Configurar Supabase (opcional)
# Edita SupabaseClient.kt con tus credenciales

# Compilar y ejecutar
./gradlew assembleDebug
```

---

## 🎯 Uso Rápido

### 1️⃣ Registro e Inicio de Sesión
```
1. Abre la app
2. Toca "Registrarse"
3. Ingresa email, nombre y contraseña
4. Inicia sesión con tus credenciales
```

### 2️⃣ Crear una Tarea
```
1. Toca el botón "Crear" en el menú inferior
2. Completa título y descripción
3. Selecciona tipo: Diaria o Semanal
4. Toca "Crear Tarea"
```

### 3️⃣ Gestionar Tareas
```
✓ Marcar como completada: Toca el checkbox
✗ Desmarcar: Toca el checkbox nuevamente
🗑️ Eliminar: Toca el botón rojo de basurero
```

---

## 📁 Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/example/habitos/
│   │   ├── data/
│   │   │   ├── model/          # Modelos de datos
│   │   │   ├── network/        # API y servicios
│   │   │   ├── AuthRepository.kt
│   │   │   └── TaskRepository.kt
│   │   ├── DailyTasksFragment.kt
│   │   ├── WeeklyTasksFragment.kt
│   │   ├── CreateTaskFragment.kt
│   │   ├── LoginActivity.kt
│   │   ├── RegisterActivity.kt
│   │   ├── MainActivity.kt
│   │   ├── TaskAdapter.kt
│   │   ├── BubbleToast.kt     # Notificaciones personalizadas
│   │   └── SessionManager.kt
│   └── res/
│       ├── layout/             # Layouts XML
│       ├── drawable/           # Recursos gráficos
│       ├── values/             # Colores, strings, temas
│       └── menu/               # Menús de navegación
└── build.gradle.kts
```

---

## 🎨 Capturas de Pantalla

<div align="center">

| Login | Tareas Diarias | Crear Tarea |
|:---:|:---:|:---:|
| <img src="screenshots/login.png" width="250"/> | <img src="screenshots/daily.png" width="250"/> | <img src="screenshots/create.png" width="250"/> |

</div>

---

## 🔧 Configuración de Supabase

Para usar tu propia instancia de Supabase:

1. **Crea un proyecto** en [supabase.com](https://supabase.com)

2. **Crea las tablas**:

```sql
-- Tabla de perfiles
CREATE TABLE profiles (
  id UUID REFERENCES auth.users PRIMARY KEY,
  email TEXT,
  name TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla de tareas
CREATE TABLE tasks (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id UUID REFERENCES auth.users NOT NULL,
  title TEXT NOT NULL,
  description TEXT,
  type TEXT CHECK (type IN ('daily', 'weekly')),
  is_completed BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Habilitar RLS (Row Level Security)
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;

-- Política: Los usuarios solo ven sus propias tareas
CREATE POLICY "Users can view own tasks"
  ON tasks FOR SELECT
  USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own tasks"
  ON tasks FOR INSERT
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own tasks"
  ON tasks FOR UPDATE
  USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own tasks"
  ON tasks FOR DELETE
  USING (auth.uid() = user_id);
```



## 📝 Roadmap

- [ ] Widget de pantalla principal
- [ ] Recordatorios y notificaciones programadas
- [ ] Estadísticas y gráficos de progreso
- [ ] Temas personalizables
- [ ] Exportar/importar datos
- [ ] Modo offline
- [ ] Sincronización multi-dispositivo mejorada

---


## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.



**@joakotopos**

- GitHub: [@joakotopos](https://github.com/joakotopos)
- Proyecto: [Hábitos](https://github.com/joakotopos/Habitos)

---




