package fx.controllers;

import java.io.IOException;

import fx.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import server.Lotto;
import server.Oggetto;

public class VisualizzaLottoController {
    public static Lotto lottoDaVisualizzare = null;
    
    @FXML
    private TableColumn<Oggetto, Oggetto.CATEGORIE> categoriaOggettoColumn;

    @FXML
    private TableColumn<Oggetto, String> descrizioneOggettoColumn;

    @FXML
    private Label indirizzoLottoLabel;

    @FXML
    private Label nomeLottoLabel;

    @FXML
    private TableColumn<Oggetto, String> nomeOggettoColumn;

    @FXML
    private TableView<Oggetto> oggettiTable;

    @FXML
    private Label prezzoBaseLottoLabel;

    @FXML
    private Label rilancioLottoLabel;

    @FXML
    private Label valoreLottoLabel;

    @FXML
    private Label vincitoreLottoLabel;

    @FXML
    public void initialize(){
        categoriaOggettoColumn.setCellValueFactory(new PropertyValueFactory<Oggetto, Oggetto.CATEGORIE>("categoria"));
        descrizioneOggettoColumn.setCellValueFactory(new PropertyValueFactory<Oggetto, String>("descrizione"));
        nomeOggettoColumn.setCellValueFactory(new PropertyValueFactory<Oggetto, String>("nome"));

        setLabels();
        setTableItems();
    }

    @FXML
    public void reset(){
        oggettiTable.getItems().clear();
        nomeLottoLabel.setText("");
        indirizzoLottoLabel.setText("");
        prezzoBaseLottoLabel.setText("");
        rilancioLottoLabel.setText("");
        valoreLottoLabel.setText("");
        vincitoreLottoLabel.setText("");
        try {
            backToHome();
        } catch (IOException ignore) {
        }
    }

    void backToHome() throws IOException{
        App.setRoot("View");
    }

    private void setLabels(){
        if(lottoDaVisualizzare == null){
            nomeLottoLabel.setText("Errore, riprova");
        }else{
            nomeLottoLabel.setText(lottoDaVisualizzare.getNomeLotto());
            indirizzoLottoLabel.setText(lottoDaVisualizzare.getIndirizzoMulticast().toString());
            prezzoBaseLottoLabel.setText(String.valueOf(lottoDaVisualizzare.getPrezzoBase()));
            rilancioLottoLabel.setText(String.valueOf(lottoDaVisualizzare.getRilancioMinimo()));
            valoreLottoLabel.setText(String.valueOf(lottoDaVisualizzare.getValoreAttuale()));
            vincitoreLottoLabel.setText(lottoDaVisualizzare.getVincitoreAttuale());
        }
    }

    private void setTableItems(){
        if(lottoDaVisualizzare != null){
            oggettiTable.getItems().addAll(lottoDaVisualizzare.getOggetti());
        }
    }
}
