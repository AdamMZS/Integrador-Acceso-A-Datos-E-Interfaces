# Manual de estilo — Postula

Versión 1.0 · Prototipo de seguimiento de candidaturas

El prototipo navegable está en [`index.html`](index.html). Ábrelo directamente en el navegador: no requiere servidor ni dependencias.

## Identidad

**Nombre:** Postula  
**Isotipo:** una «P» blanca en un contenedor azul de esquinas redondeadas; representa un camino y un punto de avance.  
**Voz:** clara, positiva y práctica. La interfaz acompaña la búsqueda sin añadir ruido.

## Paleta

| Uso | Color |
|---|---|
| Principal / acción | Azul eléctrico `#3768E5` |
| Secundario | Teal `#12847A` |
| Navegación / azul profundo | `#172B4D` |
| Fondo general | Niebla `#F5F7FA` |
| Superficie | Blanco `#FFFFFF` |
| Texto principal | Tinta `#182230` |
| Texto secundario | Gris `#667085` |
| Bordes | `#E6EAF0` |
| Pendiente | Ámbar `#A9680B` sobre `#FFF4DF` |
| Entrevista | Azul `#3768E5` sobre `#EDF2FF` |
| Aceptado | Verde `#21845A` sobre `#E9F7EF` |
| Rechazado | Rojo `#BD4646` sobre `#FFF0EF` |

Los estados siempre incluyen texto además del color para no depender únicamente de la percepción cromática.

## Tipografía

- **Títulos y cuerpo:** Inter; fallback a system-ui / Segoe UI. Títulos en semibold o bold, cuerpo en regular.
- **Monoespaciada:** IBM Plex Mono para IDs y valores técnicos.
- **Escala:** título de página 26 px; sección 17 px; cuerpo 14 px; etiquetas 11–12 px.

## Componentes y comportamiento

- **Layout:** navegación lateral persistente con marca, secciones y perfil; barra superior con contexto; contenido en una columna centrada. En móvil, navegación compacta superior.
- **Botón principal:** fondo azul, texto blanco y radio de 8 px; reservado a la acción primaria de cada pantalla.
- **Botón secundario:** superficie blanca, borde gris claro y texto tinta. Enlaces para acciones terciarias.
- **Tablas:** encabezado de bajo contraste, filas separadas por bordes finos, hover sutil y acciones visibles al final.
- **Formularios:** etiquetas siempre visibles, campos de 39 px, foco azul con halo y validación nativa.
- **Tarjetas:** superficie blanca, borde `#E6EAF0`, radio de 12 px y sombra ligera.
- **Badges de estado:** píldoras con punto y etiqueta; colores semánticos de la paleta.
- **Iconografía:** símbolos sencillos y consistentes, complementarios al texto; evitar iconos como único indicador de acción.
- **Espaciado:** escala base de 4 px; separación habitual de 16–24 px entre grupos.

## Componentes reutilizables

El prototipo comparte tokens CSS para color, tipografía y sombras; patrón `panel` para tarjetas; `btn` para acciones; `badge` para estados; `field` para controles; y el mismo layout/navegación entre listado, detalle, formularios, empresas, entrevistas, estadísticas y asistente IA.

## Pantallas y navegación

La navegación lateral conecta candidaturas, empresas, entrevistas, estadísticas, asistente IA y manual. Desde el listado se accede al detalle y al alta; desde el detalle se vuelve al listado o se llega a entrevistas. Los filtros, el alta, los formularios de empresa/entrevista y el chat tienen interacciones de demostración locales. Los datos son ficticios y no se persisten.
