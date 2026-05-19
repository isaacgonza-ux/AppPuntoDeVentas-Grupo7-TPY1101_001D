# 📊 MATRIZ DE VALIDACIONES

**Documento de Referencia** para documentar todas las validaciones presentes en la UI (Controllers) y la capa de negocio (Services) del sistema.

---

## 📋 Leyenda

| Símbolo | Significado |
|---------|-------------|
| ✅ | Validación presente |
| ❌ | Validación faltante |
| ⚠️ | Validación incompleta |
| 🔄 | Validación parcial (UI o Service, no ambas) |

---

## 1️⃣ MÓDULO: PRODUCTOS

### 1.1 Registrar Producto

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Producto nulo | Validar que el objeto no sea null | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre obligatorio | Nombre no vacío ni nulo | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre único | El nombre no debe repetirse | ✅ | ✅ | ✅ | Validado en ambas capas |
| Precio compra > 0 | El precio de compra debe ser mayor a cero | ❌ | ✅ | ⚠️ | Solo en Service |
| Unidad de medida | Debe ser asignada | ❌ | ✅ | ⚠️ | Solo en Service |
| Margen mínimo 10% | Precio venta ≥ (Precio compra × 1.10) | ❌ | ✅ | ⚠️ | Solo en Service; excepto SOLO_INVENTARIO |
| Tipo producto SOLO_INVENTARIO | Fuerza precio venta a 0 | ❌ | ✅ | ⚠️ | Solo en Service (lógica automática) |
| Cantidad obligatoria | Si unidad es GRAMOS/MILILITROS, cantidad > 0 | ✅ | ❌ | 🔄 | Solo en UI (validación visual) |
| Stock actual ≥ 0 | No permitir stock negativo | ❌ | ❌ | ❌ | **FALTANTE** |
| Stock mínimo ≥ 0 | Stock mínimo no puede ser negativo | ❌ | ❌ | ❌ | **FALTANTE** |
| Auditoría registrada | Se debe registrar el evento en la tabla auditoria | ❌ | ✅ | ⚠️ | Solo en Service |

---

### 1.2 Actualizar Producto

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Producto existe | Verificar que el ID corresponda a un producto existente | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre obligatorio | Nombre no vacío ni nulo | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre único (excepto el propio) | No duplicar nombre con otros productos | ✅ | ❌ | 🔄 | Solo en UI (verifica idAExcluir) |
| Precio compra > 0 | Precio de compra debe ser positivo | ❌ | ✅ | ⚠️ | Solo en Service |
| Unidad de medida | Debe ser asignada | ❌ | ✅ | ⚠️ | Solo en Service |
| Auditoría registrada | Se debe registrar el evento de actualización | ❌ | ✅ | ⚠️ | Solo en Service |

---

### 1.3 Eliminar Producto

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| ID válido (> 0) | Producto seleccionado debe tener ID > 0 | ❌ | ✅ | ⚠️ | Solo en Service |
| Producto existe | Verificar que el producto exista antes de eliminarlo | ❌ | ✅ | ⚠️ | Solo en Service |
| Verificar asociación a venta/platillo | Si está asociado, desactivar; si no, eliminar | ❌ | ✅ | ✅ | Solo en Service (lógica completa) |
| Auditoría registrada | Registrar evento de eliminación | ❌ | ✅ | ⚠️ | Solo en Service |

---

## 2️⃣ MÓDULO: PLATILLOS

### 2.1 Registrar Platillo

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Platillo nulo | Objeto platillo no puede ser null | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre obligatorio | Nombre no vacío ni nulo | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre único | El nombre del platillo no debe repetirse | ❌ | ✅ | ⚠️ | Solo en Service |
| Precio > 0 | Precio de venta debe ser mayor a cero | ❌ | ✅ | ⚠️ | Solo en Service |
| Tipo producto = PLATILLO | Solo se pueden registrar platillos con tipo PLATILLO | ❌ | ✅ | ⚠️ | Solo en Service |
| Ingredientes no vacíos | Debe tener al menos un ingrediente | ❌ | ❌ | ❌ | **FALTANTE** |
| Ingredientes válidos | Ningún ingrediente puede ser null | ❌ | ✅ | ⚠️ | Solo en Service |
| Cantidad ingrediente > 0 | Cantidad de ingrediente debe ser positiva | ❌ | ✅ | ⚠️ | Solo en Service |
| Auditoría registrada | Se debe registrar el evento en la tabla auditoria | ❌ | ✅ | ⚠️ | Solo en Service |

---

### 2.2 Actualizar Platillo

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Platillo existe | Verificar que el ID corresponda a un platillo existente | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Nombre único (excepto propio) | No duplicar nombre con otros platillos | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Auditoría registrada | Se debe registrar el evento de actualización | ❌ | ⚠️ | ⚠️ | Implementación incompleta |

---

### 2.3 Eliminar Platillo

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| ID válido | Platillo seleccionado debe tener ID > 0 | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Platillo existe | Verificar que el platillo exista | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Verificar asociación a venta | Si está asociado, desactivar; si no, eliminar | ❌ | ⚠️ | ⚠️ | Implementación incompleta |

---

## 3️⃣ MÓDULO: CATEGORÍAS

### 3.1 Registrar Categoría

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Categoría nula | Objeto categoría no puede ser null | ❌ | ⚠️ | ⚠️ | Implementación incompleta/faltante |
| Nombre obligatorio | Nombre no vacío ni nulo | ❌ | ⚠️ | ⚠️ | Implementación incompleta/faltante |
| Nombre único | El nombre de la categoría no debe repetirse | ❌ | ⚠️ | ⚠️ | Implementación incompleta/faltante |
| Auditoría registrada | Se debe registrar el evento | ❌ | ✅ | ⚠️ | Solo en Service |

---

### 3.2 Actualizar Categoría

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Categoría existe | Verificar que la categoría exista | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Nombre único (excepto propio) | No duplicar nombre | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Auditoría registrada | Registrar evento de actualización | ❌ | ✅ | ⚠️ | Solo en Service |

---

### 3.3 Eliminar Categoría

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| ID válido | Categoría seleccionada debe tener ID > 0 | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Categoría existe | Verificar que la categoría exista | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| No asociada a productos | No permitir eliminar si tiene productos asociados | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Auditoría registrada | Registrar evento de eliminación | ❌ | ✅ | ⚠️ | Solo en Service |

---

## 4️⃣ MÓDULO: USUARIOS

### 4.1 Registrar Usuario

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Usuario nulo | Objeto usuario no puede ser null | ❌ | ✅ | ⚠️ | Solo en Service |
| Nombre obligatorio | Nombre no vacío ni nulo | ❌ | ✅ | ⚠️ | Solo en Service |
| Apellido obligatorio | Apellido no vacío ni nulo | ❌ | ✅ | ⚠️ | Solo en Service |
| RUT obligatorio | RUT no vacío ni nulo | ❌ | ✅ | ⚠️ | Solo en Service |
| RUT válido | Formato de RUT correcto (XX.XXX.XXX-X) | ❌ | ❌ | ❌ | **FALTANTE** |
| Contraseña obligatoria | Contraseña no vacía ni nula | ❌ | ✅ | ⚠️ | Solo en Service |
| Contraseña fuerte | Mín. 8 caracteres, mayúscula, número, símbolo | ❌ | ❌ | ❌ | **FALTANTE** |
| RUT único | No se pueden registrar dos usuarios con el mismo RUT | ❌ | ✅ | ⚠️ | Solo en Service |

---

### 4.2 Iniciar Sesión

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Campos no vacíos | RUT y contraseña no deben estar vacíos | ✅ | ✅ | ✅ | Validado en ambas capas |
| Usuario existe | Verificar que el usuario exista con ese RUT | ❌ | ✅ | ⚠️ | Solo en Service (implícito en repository) |
| Contraseña correcta | Validar que la contraseña coincida | ❌ | ✅ | ⚠️ | Solo en Service (implícito en repository) |
| Usuario activo | Solo usuarios con estado = 1 pueden iniciar sesión | ❌ | ❌ | ❌ | **FALTANTE** |

---

### 4.3 Eliminar Usuario

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| RUT no vacío | RUT debe ser proporcionado | ❌ | ✅ | ⚠️ | Solo en Service |
| Usuario existe | Verificar que el usuario exista antes de eliminar | ❌ | ✅ | ⚠️ | Solo en Service |
| Usuario no es admin | No permitir eliminar el usuario administrador | ❌ | ❌ | ❌ | **FALTANTE** |

---

## 5️⃣ MÓDULO: VENTAS

### 5.1 Guardar Venta

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Venta no nula | Objeto venta no puede ser null | ❌ | ✅ | ⚠️ | Solo en Service |
| Lista de productos no vacía | Debe tener al menos un producto | ❌ | ✅ | ⚠️ | Solo en Service |
| Productos existen | Todos los productos deben existir en BD | ❌ | ❌ | ❌ | **FALTANTE** |
| Stock disponible | Validar stock antes de registrar venta | ❌ | ❌ | ❌ | **FALTANTE** (InventarioService existe pero no se usa) |
| Cantidad > 0 | Cantidad de cada producto debe ser positiva | ❌ | ❌ | ❌ | **FALTANTE** |
| Precio unitario > 0 | Precio de cada item debe ser válido | ❌ | ❌ | ❌ | **FALTANTE** |

---

### 5.2 Anular Venta

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| ID válido | ID de venta debe ser > 0 | ❌ | ❌ | ❌ | **FALTANTE** |
| Venta existe | Verificar que la venta exista | ❌ | ❌ | ❌ | **FALTANTE** |
| Venta activa | Solo se pueden anular ventas activas (estado=1) | ❌ | ❌ | ❌ | **FALTANTE** |
| Devolver stock | Al anular, devolver stock al inventario | ❌ | ❌ | ❌ | **FALTANTE** |

---

## 6️⃣ MÓDULO: INVENTARIO

### 6.1 Disponibilidad de Stock

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| ID producto válido | ID debe ser > 0 | ❌ | ✅ | ⚠️ | Solo en Service |
| Cantidad requerida > 0 | Cantidad debe ser positiva | ❌ | ❌ | ❌ | **FALTANTE** |
| Stock suficiente | Stock >= cantidad requerida | ❌ | ✅ | ⚠️ | Solo en Service (método validarDisponibilidad) |
| Para platillos: explosión de receta | Validar disponibilidad de ingredientes | ❌ | ⚠️ | ⚠️ | Incompleto, solo comentario TODO |

---

## 7️⃣ MÓDULO: ESTADÍSTICAS / IA PREDICTIVA

### 7.1 Predicción de Stock

| **Validación** | **Descripción** | **UI (Controller)** | **Service** | **Estado** | **Observaciones** |
|---|---|:---:|:---:|:---:|---|
| Datos suficientes (7+) | Mínimo 7 registros de historial | ❌ | ⚠️ | ⚠️ | Documentado en diagramas, estado implementación incompleto |
| Productos existen | Todos los IDs deben ser válidos | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| CSV válido | Archivo generado debe ser legible | ❌ | ⚠️ | ⚠️ | Implementación incompleta |
| Python disponible | Script Python debe ejecutarse sin errores | ❌ | ⚠️ | ⚠️ | Hay errores de integración reportados |

---

## 📈 RESUMEN GENERAL

### Estadísticas

| Métrica | Valor | Observación |
|---------|-------|------------|
| **Total de validaciones documentadas** | 80+ | Incluye todos los módulos |
| **Validaciones completas (UI + Service)** | ~15 | Solo validación de nombre único en productos |
| **Validaciones en Service solo** | ~40 | Mayoría de validaciones están en Service |
| **Validaciones en UI solo** | ~8 | Pocas validaciones solo en UI |
| **Validaciones faltantes** | ~20 | Críticas para seguridad de datos |

---

## 🎯 RECOMENDACIONES PRIORITARIAS

### Prioridad 🔴 CRÍTICA
1. **Stock y Ventas**: Implementar validaciones de stock disponible antes de procesar venta
2. **RUT de usuarios**: Validar formato de RUT (XX.XXX.XXX-X)
3. **Contraseñas**: Implementar validación de contraseña fuerte
4. **Eliminación de categorías**: Validar que no tenga productos asociados antes de eliminar

### Prioridad 🟠 ALTA
5. **Valores negativos**: Validar en UI que stock y precios no sean negativos
6. **Platillos sin ingredientes**: No permitir guardar platillos sin al menos un ingrediente
7. **Auditoría**: Registrar auditoría en **todas** las operaciones CRUD (falta en categorías)
8. **Usuario activo**: Validar que solo usuarios activos puedan iniciar sesión

### Prioridad 🟡 MEDIA
9. **Distribución de validaciones**: Pasar validaciones de formato (RUT, teléfono) a UI
10. **IA Predictiva**: Validar disponibilidad de datos antes de ejecutar predicción
11. **Anulación de ventas**: Implementar lógica para devolver stock al anular

---

## 📝 PATRONES OBSERVADOS

### Validaciones en Service ✅
- Verificación de nulidad
- Validaciones de negocio (duplicados, asociaciones)
- Auditoría
- Lógica de desactivación vs eliminación

### Validaciones en UI ⚠️
- Campos no vacíos (antes de parse)
- Selección de combos
- Habilitación/deshabilitación de campos según tipo de producto

### Validaciones Faltantes ❌
- Validación de **formato** (RUT, email, teléfono)
- Validación de **rango** (stock negativo, precio negativo)
- Validación de **disponibilidad** (stock para venta, capacidad de sistema)
- **Auditoría completa** en todos los módulos

---

## 🔗 REFERENCIAS INTERNAS

- Método `ProductoService.registrarProducto()` — Línea 33-103
- Método `ProductoController.registrarProducto()` — Línea 186-220
- Método `UsuarioService.iniciarSesion()` — Línea 44-48
- Método `LoginController.handleLogin()` — Línea 30-51
- Método `InventarioService.validarDisponibilidad()` — Línea 17-26
- Método `PlatilloService.calcularStockDisponibleTiempoReal()` — Línea 130-160

---

**Última actualización**: Mayo 17, 2026  
**Responsable**: Equipo de Desarrollo  
**Estado**: Documento Vivo (requiere actualización con cada cambio en validaciones)

