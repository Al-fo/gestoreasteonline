package fx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class InfoController {

    @FXML
    private TextArea emailArea;

    @FXML
    private TextArea nomeArea;

    @FXML
    private TextArea passwordArea;

    @FXML
    private TextArea telArea;

    @FXML
    public void initialize(){
        emailArea.setVisible(false);
        nomeArea.setVisible(false);
        passwordArea.setVisible(false);
        telArea.setVisible(false);
    }

    @FXML
    public void showAreas() {
        emailArea.setVisible(true);
        nomeArea.setVisible(true);
        passwordArea.setVisible(true);
        telArea.setVisible(true);
    }

}
