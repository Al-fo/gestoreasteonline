package fx.controllers;

import java.io.IOException;

import fx.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private Button annullaButton, registratiButton;

    @FXML
    private Label cognomeErrorLabel, emailErrorLabel, erroreLabel, nomeErrorLabel, passwordErrorLabel, telefonoErrorLabel;

    @FXML
    private TextField cognomeField, telField, emailField, nomeField;

    @FXML
    private PasswordField passwordField, password2Field;

    @FXML
    private void initialize(){
        devisibilizzaLabels();
    }

    @FXML
    void backToAccedi(ActionEvent event) throws IOException {
        annullaTutto();
        App.setRoot("LoginView");
    }

    @FXML
    void registraAction(ActionEvent event) {
        boolean sbagliato = false;
        devisibilizzaLabels();
        if(!password2Field.getText().equals(passwordField.getText())){
            passwordErrorLabel.setVisible(true);        
            sbagliato = true;
        }
        
        String[] cose = {
            nomeField.getText(),
            cognomeField.getText(),
            emailField.getText(),
            passwordField.getText(),
            telField.getText()
        };

        String[] patterns = {
            "^([A-Z]*[a-z]+)+$", "^([A-Z]*[a-z]+)+$", 
            "^[^.\\r\\n\\t\\f @()<>,;:\"]+\\.?[\\w]+\\@([a-z0-9]+-*[a-z0-9]+\\.)+[a-z-*]{2,6}$", 
            "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9]).{6,})\\S$", 
            "^([+][0-9]{2} )?[0-9]{10}$"
        };

        Label[] labels = {
            nomeErrorLabel, 
            cognomeErrorLabel, 
            emailErrorLabel, 
            passwordErrorLabel, 
            telefonoErrorLabel
        };

        for(int i = 0; i < patterns.length; i++){
            if(!cose[i].matches(patterns[i])){
                labels[i].setVisible(true);
                sbagliato = true;
            }
        }

        if(!sbagliato)
            try {
                App.client.registrati(nomeField.getText(), cognomeField.getText(), emailField.getText(), passwordField.getText(), telField.getText());
                backToAccedi(event);
            } catch (IOException e) {
                switch(e.getMessage()){
                    case "Dati":
                        erroreLabel.setText("Dati inseriti errati");
                        erroreLabel.setVisible(true);
                        break;
                    case "Esistente":
                        erroreLabel.setText("Utente già registrato");
                        erroreLabel.setVisible(true);
                        break;
                    default:
                        erroreLabel.setText("Errore dal server");
                        erroreLabel.setVisible(true);
                }
            }
    }

    private void devisibilizzaLabels(){
        cognomeErrorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
        erroreLabel.setVisible(false);
        nomeErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        telefonoErrorLabel.setVisible(false);
    }

    private void annullaTutto(){
        emailField.clear();
        cognomeField.clear();
        nomeField.clear();
        telField.clear();

        passwordField.clear();
        password2Field.clear();
    }

    

}
