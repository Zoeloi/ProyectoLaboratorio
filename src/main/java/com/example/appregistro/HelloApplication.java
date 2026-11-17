package com.example.appregistro;

import com.example.appregistro.persistence.JPAUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("equipo-view.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 640);
        stage.setTitle("Control de equipos de laboratorio");
        stage.setScene(scene);
        stage.show();
    }

    /** Al cerrar la ventana se cierra el EntityManagerFactory. */
    @Override
    public void stop() {
        JPAUtil.cerrar();
    }
}
