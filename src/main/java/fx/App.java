package fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import client.Client;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public final static Client client = new Client();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("LoginView"), 640, 480);
        stage.setScene(scene);
        stage.show();
        //stage.setResizable(false); QUALCHE ERRORE A CAZZO
        scene.getWindow().setWidth(780);
        scene.getWindow().setHeight(600);
        stage.setOnCloseRequest(event -> {
            try {
                client.disconnetti();
            } catch (IOException ignore) {
            }
            client.die();
        });
        
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        scene.getWindow().setWidth(780);
        scene.getWindow().setHeight(475);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}