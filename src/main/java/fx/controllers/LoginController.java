package fx.controllers;

import java.io.IOException;

import fx.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    Button accediButton, registraButton;

    @FXML
    Label erroreLabel;

    @FXML
    TextField userField;

    @FXML
    PasswordField passField;

    @FXML
    public void initialize(){
        erroreLabel.setVisible(false);
    }

    @FXML
    public void accediAction(){
        String email = userField.getText();
        String password = passField.getText();
        try {
            App.client.admin(App.client.accedi(email, password));
            erroreLabel.setVisible(false);
            App.setRoot(App.client.isAdmin() ? "AdminView" : "View");
        } catch (IOException e) {
            switch (e.getMessage()){
                case "Dati":
                    erroreLabel.setText("Email o password errate");
                    break;
                case "Connected":
                    erroreLabel.setText("Utente già connesso, riprova l'accesso");
                    break;
                default:
                    erroreLabel.setText("Errore, riprova");
                    e.printStackTrace();
                    break;
            }
            erroreLabel.setVisible(true);
        }
    }

    @FXML
    public void registraAction() throws IOException{
        App.setRoot("RegisterView");
    }
    
}