# Matriz de pruebas P1–P8

| ID | Prueba | Entrada | Resultado esperado | Resultado obtenido | Evidencia |
|---|---|---|---|---|---|
| P1 | Registrar equipo válido | PC-010, "Laptop Prueba", Dell, 2, Computadora, Disponible | Registro guardado y visible | | captura |
| P2 | Código duplicado | Código PC-001 (ya existe) | Rechazado, Alert con rollback, sin duplicados | | captura + `SELECT count(*) FROM laboratorio.equipo WHERE codigo='PC-001';` = 1 |
| P3 | Editar nombre/estado/tipo | Seleccionar PC-010, cambiar estado a "En mantenimiento" y tipo a Redes | Cambios persistidos | | captura app + SELECT |
| P4 | Eliminar con confirmación | Seleccionar PC-010 → Eliminar → Aceptar | Desaparece del listado y de PostgreSQL | | captura + SELECT vacío |
| P5 | Buscar por nombre parcial | "lap" | Solo equipos cuyo nombre contiene "lap" | | captura |
| P6 | Filtro por tipo + búsqueda | Tipo Computadora + "lap" | Solo PC-001 y PC-003 | | captura |
| P7 | Cantidad negativa | Cantidad -5 | Validación rechaza el dato | | captura del Alert |
| P8 | Reiniciar aplicación | Cerrar y abrir la app | Datos confirmados siguen ahí | | captura |
