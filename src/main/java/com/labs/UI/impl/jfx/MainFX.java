package com.labs.UI.impl.jfx;

import java.io.IOException;

import com.labs.core.service.DependenciesInjector;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.*;

public class MainFX extends Application {
 
    private static final String MAIN_PAGE = "layout/main.fxml";
    private static final String AUTH_PAGE = "layout/auth.fxml";
    private static final String ICON = "layout/resources/icon.png";
    private static final String TITLE = "Tax Managing System";
    static DependenciesInjector DI;
    static AsynchronousMachine Flow;

    @Override
    public void start(Stage stage) {
        Parent main = null;
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
        try {
            main = FXMLLoader.load(getClass().getResource(MAIN_PAGE));
        } catch (IOException e) { e.printStackTrace(); }
        Scene scene = new Scene(main);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        Flow = new AsynchronousMachine();
        launch();
    }

    public static void setDI(DependenciesInjector Injector){
        DI = Injector;
    }

    static Stage CreateAuthStage(){
        final Stage dialog = new Stage();
        dialog.getIcons().add(new Image(MainFX.class.getResourceAsStream(ICON)));
        dialog.initModality(Modality.APPLICATION_MODAL);
        Parent auth = null;
        try {
            auth = FXMLLoader.load(MainFX.class.getResource(AUTH_PAGE));
        } catch (IOException e) { e.printStackTrace(); }
        Scene s = new Scene(auth, 224, 172);
        dialog.setScene(s);
        return dialog;
    }

}
