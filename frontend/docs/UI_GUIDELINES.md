# Guía visual RitualFresh

Esta guía define las reglas obligatorias de UI para todo el frontend de RitualFresh. Cualquier cambio visual futuro debe partir de este documento.

## 1. Estética general
- SaaS moderno basado en Material Design 3.
- Interfaz clara, accesible, consistente y profesional.
- No cambiar la identidad visual sin justificarlo.

## 2. Paleta oficial
- Color primario: `#1565C0`
- Primario oscuro: `#0D47A1`
- Secundario: `#26A69A`
- Color de acento: `#FFA726`
- Fondo general: `#F5F7FA`
- Error: `#B3261E`

## 3. Tipografía
- Usar Roboto, Inter o Poppins.
- Diseño limpio y legible.
- Jerarquía visual clara.

## 4. Componentes reutilizables
Respetar y reutilizar:
- Navbar y sidebar
- Formularios Material Design
- Cards de servicios y trabajadores
- Botones primarios y secundarios
- Modales y mensajes de validación
- Tablas y reportes
- Dashboard e indicadores visuales

## 5. Lineamientos UX/UI
- Navegación intuitiva.
- Diseño responsive.
- Validaciones visuales inmediatas.
- Consistencia visual entre módulos.
- Interfaz clara para usuarios no técnicos.

## 6. Estado actual del frontend
Este apartado no propone una modernización completa. Solo marca diferencias visibles entre la UI actual y la guía oficial:
- La paleta actual todavía conserva tonos cálidos y verdosos heredados del estilo anterior.
- La tipografía base usa `Segoe UI` como primera opción y no prioriza Roboto, Inter o Poppins.
- Algunos fondos, sombras y focos visuales todavía responden a la identidad previa.

## 7. Criterio de implementación
- Cualquier ajuste visual futuro debe tomar esta guía como referencia obligatoria.
- Antes de introducir nuevos colores, componentes o variantes, revisar si ya existe un token o componente reutilizable.
- Si hace falta un cambio global, centralizarlo en variables CSS o theme, no en valores duplicados dentro de componentes.
