module com.example.appregistro {
    // Interfaz gráfica
    requires javafx.controls;
    requires javafx.fxml;

    // ORM: JPA (jakarta.persistence) + Hibernate + driver de PostgreSQL
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires org.postgresql.jdbc;
    requires java.sql;
    requires java.naming;

    // FXML necesita acceso por reflexión a la app y al controller
    opens com.example.appregistro to javafx.fxml;
    opens com.example.appregistro.controller to javafx.fxml;
    // Hibernate necesita reflexión sobre las entidades; javafx.base para PropertyValueFactory
    opens com.example.appregistro.model to org.hibernate.orm.core, javafx.base;

    exports com.example.appregistro;
}
