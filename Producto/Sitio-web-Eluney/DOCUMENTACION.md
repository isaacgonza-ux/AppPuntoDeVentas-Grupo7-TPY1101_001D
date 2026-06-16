# 📄 Documentación Frontend — Eluney

---

## Navbar

**Archivo:** `src/Componets/Navbar.jsx`  
**Estilos:** `src/css/Navbar.css`  
**Descripción:** Barra de navegación fija en la parte superior de la página.
Incluye navegación suave entre secciones, botón de WhatsApp y menú hamburguesa en móvil.

---

###  Estado interno

| Variable | Tipo | Valor inicial | Para qué sirve |
|---|---|---|---|
| `active` | string | `"Inicio"` | Guarda qué link está activo/seleccionado |
| `menuAbierto` | boolean | `false` | Controla si el menú móvil está abierto o cerrado |

---

###  Links de navegación

Los links están definidos como un array de objetos dentro del componente:

```js
const links = [
  { label: "Inicio",    sectionId: "hero-section" },
  { label: "Carta",     sectionId: "carta"         },
  { label: "Nosotros",  sectionId: "nosotros"      },
  { label: "Servicios", sectionId: "servicios"     },
];
```

| Propiedad | Para qué sirve |
|---|---|
| `label` | Texto visible del link |
| `sectionId` | ID del `<section>` al que hace scroll |

>  Para agregar un nuevo link, basta con agregar un objeto al array
> y asegurarse de que exista un `<section id="...">` con ese mismo id en la página.

---

###  Funciones

#### `handleNavClick(label, sectionId)`
Se ejecuta al hacer click en cualquier link del navbar.

Hace tres cosas:
1. Marca el link como activo (`setActive`)
2. Cierra el menú móvil si estaba abierto (`setMenuAbierto(false)`)
3. Hace scroll suave hasta la sección, descontando la altura del navbar para que no tape el contenido

```js
const sectionTop = section.getBoundingClientRect().top + window.scrollY;
window.scrollTo({ top: sectionTop - navbarHeight, behavior: "smooth" });
```

---

###  Estructura del JSX
<nav>
  ├── .navbar__logo          → Logo "Eluney"
  ├── <ul>.navbar__links     → Lista de links + WhatsApp móvil
  │     ├── <li> x4          → Links de navegación
  │     └── <li> WhatsApp    → Solo visible en móvil
  └── .navbar__derecha       → Contenedor derecho
        ├── .navbar__whatsapp     → Botón WhatsApp (escritorio)
        └── .navbar__hamburguesa  → Botón ☰ / ✕ (móvil)



###  Clases CSS principales

| Clase                        | Elemento           | Para qué sirve |
|---|---|---|
| `.navbar` | `                 <nav>`              | Contenedor principal, sticky |
| `.navbar__logo` |            `<div>`              | Logo del local |
| `.navbar__links` |           `<ul>`               | Lista de links |
| `.navbar__links--abierto` |  `<ul>`               | Muestra el menú en móvil |
| `.navbar__link` |            `<button>`           | Cada link de navegación |
| `.navbar__link--active` |    `<button>`           | Link actualmente seleccionado |
| `.navbar__underline` |       `<span>`             | Línea debajo del link activo |
| `.navbar__whatsapp` |        `<a>`                | Botón de WhatsApp |
| `.navbar__hamburguesa` |     `<button>`           | Botón abrir/cerrar menú móvil |
| `.navbar__derecha` |         `<div>`              | Agrupa WhatsApp y hamburguesa |



###  Cómo modificar

**Cambiar el número de WhatsApp:**
```jsx
// Busca esta línea y cambia el número
href="https://wa.me/56912345678"
```

**Agregar un nuevo link:**
```js
const links = [
  ...
  { label: "Contacto", sectionId: "contacto" }, //  agrega aquí
];
```

**Cambiar el tiempo de animación del menú:**
```css
/* En Navbar.css */
.navbar__links {
  transition: all 0.3s ease; /* ajusta la duración */
}
```