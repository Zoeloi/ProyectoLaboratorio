-- =====================================================================
--  Script reproducible: Control de equipos de laboratorio
--  Base de datos: bd_laboratorio_orm   |   Esquema: laboratorio
--  La estructura se crea MANUALMENTE con este script SQL.
--  Hibernate solo VALIDA que las entidades coincidan (hbm2ddl = validate).
--
--  Cómo ejecutarlo (desde la terminal / psql):
--    1) psql -U postgres -c "CREATE DATABASE bd_laboratorio_orm;"
--    2) psql -U postgres -d bd_laboratorio_orm -f sql/bd_laboratorio_orm.sql
--  En pgAdmin: crear la base bd_laboratorio_orm, abrir Query Tool sobre
--  ella y ejecutar este archivo desde la línea "DROP SCHEMA" hacia abajo.
-- =====================================================================

DROP SCHEMA IF EXISTS laboratorio CASCADE;
CREATE SCHEMA laboratorio;

-- ---------------------------------------------------------------------
-- Tabla tipo_equipo (lado "uno" de la relación)
-- ---------------------------------------------------------------------
CREATE TABLE laboratorio.tipo_equipo (
    id_tipo      SERIAL       PRIMARY KEY,
    nombre       VARCHAR(80)  NOT NULL UNIQUE,
    descripcion  VARCHAR(255)
);

-- ---------------------------------------------------------------------
-- Tabla equipo (lado "muchos"; cada equipo pertenece a UN tipo)
-- ---------------------------------------------------------------------
CREATE TABLE laboratorio.equipo (
    id_equipo  SERIAL        PRIMARY KEY,
    codigo     VARCHAR(30)   NOT NULL UNIQUE,
    nombre     VARCHAR(100)  NOT NULL,
    marca      VARCHAR(60),
    estado     VARCHAR(30)   NOT NULL,
    cantidad   INTEGER       NOT NULL DEFAULT 0,
    id_tipo    INTEGER       NOT NULL,
    CONSTRAINT ck_equipo_cantidad CHECK (cantidad >= 0),
    CONSTRAINT ck_equipo_estado   CHECK (estado IN ('Disponible', 'En mantenimiento', 'Fuera de servicio')),
    -- Sin ON DELETE CASCADE: si un tipo tiene equipos, PostgreSQL rechaza borrarlo.
    CONSTRAINT fk_equipo_tipo FOREIGN KEY (id_tipo)
        REFERENCES laboratorio.tipo_equipo (id_tipo)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ---------------------------------------------------------------------
-- Datos iniciales: 3 tipos y 8 equipos con distintos estados
-- ---------------------------------------------------------------------
INSERT INTO laboratorio.tipo_equipo (nombre, descripcion) VALUES
  ('Computadora', 'Equipos de escritorio y laptops'),
  ('Proyector',   'Proyectores multimedia para aulas'),
  ('Redes',       'Routers, switches y accesorios de red');

INSERT INTO laboratorio.equipo (codigo, nombre, marca, estado, cantidad, id_tipo) VALUES
  ('PC-001',  'Laptop ThinkPad E14',      'Lenovo',   'Disponible',        10, 1),
  ('PC-002',  'Desktop OptiPlex 7090',    'Dell',     'En mantenimiento',   4, 1),
  ('PC-003',  'Laptop Pavilion 15',       'HP',       'Fuera de servicio',  2, 1),
  ('PRO-001', 'Proyector PowerLite X49',  'Epson',    'Disponible',         3, 2),
  ('PRO-002', 'Proyector MS560',          'BenQ',     'En mantenimiento',   1, 2),
  ('RED-001', 'Switch 24 puertos',        'Cisco',    'Disponible',         5, 3),
  ('RED-002', 'Router Archer AX55',       'TP-Link',  'Disponible',         6, 3),
  ('RED-003', 'Access Point UniFi 6',     'Ubiquiti', 'Fuera de servicio',  2, 3);

-- Verificación rápida
SELECT e.codigo, e.nombre, e.estado, t.nombre AS tipo
FROM laboratorio.equipo e JOIN laboratorio.tipo_equipo t ON t.id_tipo = e.id_tipo
ORDER BY e.codigo;
