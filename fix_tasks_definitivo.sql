-- ============================================================
-- SCRIPT DEFINITIVO PARA ARREGLAR TABLA TASKS
-- ============================================================
-- Este script elimina TODOS los constraints y los recrea
-- ============================================================

-- PASO 1: Ver todos los constraints actuales (para debug)
SELECT 
    conname as constraint_name,
    contype as constraint_type,
    pg_get_constraintdef(oid) as definition
FROM pg_constraint
WHERE conrelid = 'public.tasks'::regclass;

-- PASO 2: Eliminar TODOS los constraints de tipo CHECK
DO $$ 
DECLARE 
    constraint_name TEXT;
BEGIN
    FOR constraint_name IN 
        SELECT conname 
        FROM pg_constraint 
        WHERE conrelid = 'public.tasks'::regclass 
        AND contype = 'c'  -- 'c' = CHECK constraint
    LOOP
        EXECUTE format('ALTER TABLE public.tasks DROP CONSTRAINT IF EXISTS %I', constraint_name);
        RAISE NOTICE 'Eliminado constraint: %', constraint_name;
    END LOOP;
END $$;

-- PASO 3: Hacer description nullable y con default
ALTER TABLE public.tasks 
ALTER COLUMN description DROP NOT NULL;

ALTER TABLE public.tasks 
ALTER COLUMN description SET DEFAULT '';

-- PASO 4: Hacer type nullable temporalmente para poder modificarlo
ALTER TABLE public.tasks 
ALTER COLUMN type DROP NOT NULL;

-- PASO 5: Actualizar cualquier valor inválido que pueda existir
UPDATE public.tasks 
SET type = 'daily' 
WHERE type NOT IN ('daily', 'weekly') OR type IS NULL;

-- PASO 6: Ahora hacer type NOT NULL de nuevo
ALTER TABLE public.tasks 
ALTER COLUMN type SET NOT NULL;

-- PASO 7: Agregar el CHECK constraint correcto SIN nombre específico
-- (PostgreSQL generará un nombre automático único)
ALTER TABLE public.tasks
ADD CHECK (type IN ('daily', 'weekly'));

-- PASO 8: Verificar que funcionó
SELECT 
    conname as constraint_name,
    pg_get_constraintdef(oid) as definition
FROM pg_constraint
WHERE conrelid = 'public.tasks'::regclass
AND contype = 'c';

-- PASO 9: Prueba manual (reemplaza con tu userId)
-- Primero obtén tu userId:
SELECT id, email FROM auth.users ORDER BY created_at DESC LIMIT 5;

-- Luego intenta insertar (REEMPLAZA el UUID):
-- INSERT INTO public.tasks (user_id, title, description, type, is_completed)
-- VALUES (
--     'TU-USER-ID-AQUI',
--     'Tarea de prueba',
--     'Descripción de prueba',
--     'daily',
--     false
-- );

-- Si funciona, elimínala:
-- DELETE FROM public.tasks WHERE title = 'Tarea de prueba';

-- PASO 10: Ver estructura final de la tabla
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public' 
AND table_name = 'tasks'
ORDER BY ordinal_position;

