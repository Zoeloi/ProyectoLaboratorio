# Appregistro – Control de equipos de laboratorio
JavaFX + Hibernate ORM + PostgreSQL · Programación de Aplicaciones de Escritorio · Corte III

## Requisitos
- JDK 17 o superior · IntelliJ IDEA · Maven (incluido en IntelliJ / `mvnw`)
- PostgreSQL 14+ (probado en 16)

## 1. Base de datos
La estructura se creó **manualmente con SQL** (`sql/bd_laboratorio_orm.sql`). Hibernate usa
`hibernate.hbm2ddl.auto = validate`: no crea ni borra tablas, solo verifica que las entidades coincidan.

```
psql -U postgres -c "CREATE DATABASE bd_laboratorio_orm;"
psql -U postgres -d bd_laboratorio_orm -f sql/bd_laboratorio_orm.sql
```
(En pgAdmin: crear la base `bd_laboratorio_orm`, abrir Query Tool y ejecutar el script.)

Crea el esquema `laboratorio`, las tablas `tipo_equipo` y `equipo`, sus restricciones
(PK, UNIQUE, NOT NULL, CHECK de cantidad y estado, FK sin cascada) y datos iniciales (3 tipos, 8 equipos).

## 2. Configuración de Hibernate
- Archivo: `src/main/resources/META-INF/persistence.xml`
- Persistence-unit: **`laboratorioPU`** · Proveedor: `org.hibernate.jpa.HibernatePersistenceProvider`
- URL: `jdbc:postgresql://localhost:5432/bd_laboratorio_orm` · Usuario: `postgres` · Esquema: `laboratorio`
- Credenciales locales del laboratorio definidas en `persistence.xml`. Para otra máquina, cambiar
  usuario/clave ahí o definir las variables de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD` (tienen prioridad).

## 3. Ejecutar
Ejecutar la clase `Launcher` desde IntelliJ, o `mvn javafx:run` (con la variable de entorno definida).

## 4. Estructura (packages)
| Package | Responsabilidad |
|---|---|
| `model` | Entidades JPA `TipoEquipo` y `Equipo` |
| `persistence` | `JPAUtil`: un `EntityManagerFactory` para toda la app; un `EntityManager` por operación |
| `dao` | `EquipoDAO`, `TipoEquipoDAO`: CRUD, consultas JPQL y transacciones |
| `controller` | `EquipoController`: eventos y componentes de la vista |
| `util` | `Validador` (reglas de entrada) y `Alertas` (Alert de JavaFX) |

## 5. Entidades y asociación
- `Equipo` → `@ManyToOne` + `@JoinColumn(name = "id_tipo")`: lado **propietario**; guarda el objeto `TipoEquipo`, no solo el id.
- `TipoEquipo` → `@OneToMany(mappedBy = "tipoEquipo")`: lado inverso (relación bidireccional).
- **Sin cascada de borrado**: si un tipo tiene equipos, la FK (`ON DELETE RESTRICT`) rechaza eliminarlo.

## 6. Operaciones
| Operación | Cómo |
|---|---|
| Crear | `em.persist(equipo)` + `commit()` |
| Leer | JPQL `SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo` |
| Actualizar | `em.merge(equipo)` (objeto detached de la UI → managed) + `commit()` |
| Eliminar | `em.find(...)` + `em.remove(...)` + `commit()`, con confirmación previa |
| Buscar / filtrar | JPQL con `LOWER(e.nombre) LIKE :patron` y `t.idTipo = :idTipo` usando `setParameter()`; combinables |
| Transacciones | `begin()` → operación → `commit()`; si hay excepción antes de confirmar → `rollback()`; siempre `em.close()` |

Nunca se concatena texto del usuario en una consulta.

## 7. Integridad y rollback
Al guardar un código repetido, la app no lo revisa antes: deja que PostgreSQL lo rechace
(`equipo_codigo_key`). La excepción se captura en el DAO, se ejecuta `rollback()` y se muestra un Alert.
Se comprueba en la BD que no quedó ningún registro parcial (ver P2 en `docs/matriz_pruebas.md`).
# ProyectoLaboratorio
