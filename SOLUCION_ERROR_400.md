# Solución al Error HTTP 400 en Registro

## ✅ Cambios Aplicados

He corregido el formato de la petición de registro para que coincida con el formato correcto de la API de Supabase:

### Antes:
```json
{
  "email": "user@example.com",
  "password": "123456",
  "data": {
    "name": "Usuario"
  }
}
```

### Ahora (CORRECTO):
```json
{
  "email": "user@example.com",
  "password": "123456",
  "options": {
    "data": {
      "name": "Usuario"
    }
  }
}
```

## 🔍 Verificaciones Necesarias en Supabase

### 1. Verificar Configuración de Authentication

Ve a tu proyecto Supabase → **Authentication** → **Providers** → **Email**

Asegúrate de que:

- ✅ **Email provider** está **habilitado**
- ✅ **Confirm email** está **DESHABILITADO** (para testing)
  
  Si "Confirm email" está habilitado, los usuarios necesitan confirmar su email antes de poder usar la cuenta. Para desarrollo, es mejor deshabilitarlo:
  
  ```
  Settings → Authentication → Email Auth → 
  Desactiva "Enable email confirmations"
  ```

### 2. Verificar Políticas de la Tabla profiles

Ve a **Table Editor** → **profiles** → **RLS Policies**

Verifica que existe la política:
```
"Users can insert their own profile"
```

Si no existe o está deshabilitada, ejecuta:

```sql
-- Habilitar RLS
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Crear política de inserción
DROP POLICY IF EXISTS "Users can insert their own profile" ON public.profiles;
CREATE POLICY "Users can insert their own profile"
ON public.profiles FOR INSERT
WITH CHECK (auth.uid() = id);
```

### 3. Verificar URL y API Key

Abre: `app/src/main/java/com/example/habitos/data/network/SupabaseClient.kt`

Verifica que:
```kotlin
private const val BASE_URL = "https://pairixgqshzufmtnxqqj.supabase.co/rest/v1/"
private const val AUTH_BASE_URL = "https://pairixgqshzufmtnxqqj.supabase.co/"
```

Y que tu API Key sea la correcta (la "anon/public" key, no la "service_role" key).

### 4. Verificar Rate Limiting

Supabase tiene límites de peticiones. Si has intentado registrarte muchas veces, podrías estar temporalmente bloqueado.

**Solución**: Espera 5-10 minutos antes de intentar de nuevo.

### 5. Verificar Email Válido

Asegúrate de usar un formato de email válido:
- ✅ `usuario@example.com`
- ✅ `test@test.com`
- ❌ `usuario` (sin dominio)
- ❌ `usuario@` (incompleto)

## 🧪 Prueba con estos datos

Intenta registrarte con:
- **Nombre**: `Test Usuario`
- **Email**: `test@example.com`
- **Contraseña**: `123456`
- **Confirmar**: `123456`

## 🔧 Si el error persiste

### Opción 1: Ver el error detallado

La app ahora mostrará más detalles del error. Después de compilar y ejecutar, intenta registrarte y **anota el mensaje de error completo**.

### Opción 2: Probar desde Supabase Dashboard

Ve a **Authentication** → **Users** → **Add User** (botón verde)

Intenta crear un usuario manualmente:
- Email: `test@example.com`
- Password: `123456`
- Auto Confirm User: **Sí** (activa el checkbox)

Si funciona desde el dashboard, el problema es la configuración de la app.
Si NO funciona, el problema está en la configuración de Supabase.

### Opción 3: Revisar Logs de Supabase

Ve a **Logs** → **API Logs**

Busca las peticiones POST a `/auth/v1/signup` y revisa el error específico.

## 📋 Checklist de Configuración

Marca cada item:

- [ ] Email provider habilitado en Authentication
- [ ] "Confirm email" DESHABILITADO (para testing)
- [ ] Tabla `profiles` existe
- [ ] RLS habilitado en tabla `profiles`
- [ ] Política "Users can insert their own profile" existe
- [ ] URL de Supabase correcta en código
- [ ] API Key correcta (anon/public key)
- [ ] Esperado 5-10 minutos si hubo muchos intentos
- [ ] Email con formato válido
- [ ] Contraseña de 6+ caracteres

## 🎯 Código Actualizado

Los siguientes archivos fueron actualizados:

1. **AuthModels.kt**: Estructura correcta con `SignUpOptions`
2. **AuthRepository.kt**: Request con formato correcto
3. **AuthApiService.kt**: Headers Content-Type agregados
4. **RegisterActivity.kt**: Mejor manejo de errores HTTP

## 🚀 Próximos Pasos

1. **Recompila la app**:
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

2. **Ejecuta la app**

3. **Intenta registrarte** con los datos de prueba

4. **Si aparece error**, copia el mensaje completo y verifica la checklist arriba

5. **Revisa los logs** en Supabase Dashboard → Logs → API Logs

## 💡 Configuración Recomendada para Desarrollo

En **Authentication** → **Settings**:

```
Email:
  ✅ Enable email provider
  ❌ Confirm email (deshabilitado para testing)
  ✅ Enable signups
  
Rate Limits:
  - Deja los valores por defecto
  
Password:
  - Minimum password length: 6
```

## ❓ Preguntas Frecuentes

**P: ¿Por qué desactivar "Confirm email"?**
R: Para desarrollo es más rápido. En producción, actívalo y configura el envío de emails.

**P: ¿Puedo usar cualquier email?**
R: Sí, pero debe tener formato válido (user@domain.com). No necesita existir realmente si "Confirm email" está desactivado.

**P: ¿Qué es el error 400?**
R: "Bad Request" - significa que el servidor recibió datos en formato incorrecto o inválidos. Ya lo corregimos.

**P: ¿Cuánto tiempo debo esperar si estoy bloqueado?**
R: Usualmente 5-10 minutos. Los límites de Supabase se resetean automáticamente.

