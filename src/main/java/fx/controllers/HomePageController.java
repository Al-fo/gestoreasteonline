package fx.controllers;

import java.io.IOException;
import java.util.List;

import fx.App;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import server.GestoreAste;
import server.Lotto;
import server.GestoreAste.Asta;

public class HomePageController {

    @FXML
    private TableColumn<Asta, Integer> IDAstaColumn;

    @FXML
    private TableColumn<Lotto, String> IndirizzoLottoColumn;

    @FXML
    private TableColumn<Lotto, String> NomeLottoColumn;

    @FXML
    private TableColumn<Lotto, Double> PrezzoLottoColumn;

    @FXML
    private Button rilancioButton;

    @FXML
    private TableColumn<Lotto, String> VincitoreLottoColumn;

    @FXML
    private TableColumn<Asta, Boolean> astaApertaColumn;

    @FXML
    private TableView<Asta> asteList;

    @FXML
    private Button aggiungiButton;

    @FXML
    private Button entraButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TableView<Lotto> lottiTable;

    @FXML
    private TextField rilancioField;

    @FXML
    private Label erroreDisconnessioneLabel;

    @FXML
    private Label erroreRilancioLabel;

    @FXML
    public void initialize(){
        erroreDisconnessioneLabel.setVisible(false);
        lottiTable.setVisible(false);
        entraButton.setVisible(false);
        rilancioButton.setVisible(false);
        erroreRilancioLabel.setVisible(false);
        rilancioField.setVisible(false);

        IDAstaColumn.setCellValueFactory(new PropertyValueFactory<Asta, Integer>("ID"));
        astaApertaColumn.setCellValueFactory(new PropertyValueFactory<Asta, Boolean>("aperta"));

        NomeLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, String>("nomeLotto"));
        PrezzoLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, Double>("valoreAttuale"));
        VincitoreLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, String>("vincitoreAttuale"));
        IndirizzoLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, String>("indirizzoMulticast"));
        refreshAste();
    }

    private void refreshAste(){
        asteList.getItems().clear();
        lottiTable.getItems().clear();
        GestoreAste gestore = new GestoreAste();
        gestore.deserializza();
        for(Asta a : gestore.getAste()){
            asteList.getItems().add(a);
            List<Lotto> list = a.getLotti();
            for(Lotto l : list){
                lottiTable.getItems().add(l);
            }
        }
    }

    @FXML
    public void rilancioAction(){
        erroreRilancioLabel.setVisible(false);
        if(lottiTable.getSelectionModel().getSelectedItem() != null){
            Lotto l = lottiTable.getSelectionModel().getSelectedItem();
            Asta a = asteList.getSelectionModel().getSelectedItem();
            if(a == null) System.out.println("Pipo");
            else{
                double rilancio = 0;
                try{
                   rilancio = Double.parseDouble(rilancioField.getText());
                }catch(NumberFormatException | NullPointerException e){
                    erroreRilancioLabel.setVisible(true);
                    erroreRilancioLabel.setText("Valore inserito non valido");
                    return;
                }
                try {
                    App.client.effettuaRilancio(l.getID(), a.getID(), rilancio);
                    refreshAste();
                } catch (IOException e) {
                    switch(e.getMessage()){
                        case "Rilancio":
                            erroreRilancioLabel.setVisible(true);
                            erroreRilancioLabel.setText("Il rilancio minimo è di " + l.getRilancioMinimo());
                            break;
                        case "Asta":
                            erroreRilancioLabel.setVisible(true);
                            erroreRilancioLabel.setText("Asta o lotto non disponibile");
                            break;
                        case "Errore":
                            erroreRilancioLabel.setVisible(true);
                            erroreRilancioLabel.setText("Errore");
                            break;
                        default: break;
                    }
                }
            }
        }
    }

    @FXML
    public void visualizza(){
        if(asteList.getSelectionModel().getSelectedItem() != null){
            Asta a = asteList.getSelectionModel().getSelectedItem();
            System.out.println(a.toString());
            lottiTable.setVisible(true);
            lottiTable.getItems().clear();
            ObservableList<Lotto> list;
                list = FXCollections.observableArrayList(a.getLotti());
                System.out.println(list.toString());
            lottiTable.getItems().addAll(list);
            entraButton.visibleProperty().bind(new SimpleBooleanProperty(a.isAperta()));
            rilancioButton.visibleProperty().bind(new SimpleBooleanProperty(a.isAperta()));
            rilancioField.visibleProperty().bind(new SimpleBooleanProperty(a.isAperta()));
        }
    }

    @FXML
    public void logoutAction(){
        try {
            erroreDisconnessioneLabel.setVisible(false);
            App.client.disconnetti();
            App.setRoot("LoginView");
        } catch (IOException e) {
            erroreDisconnessioneLabel.setVisible(true);
            erroreDisconnessioneLabel.setText("Errore durante la disconnessione");
        }
        
    }
    
    @FXML
    public void aggiungiAction() throws IOException{
        Asta a = asteList.getSelectionModel().getSelectedItem();
        if(a != null && a.isAperta()){
            AggiungiLottoController.selectedAsta = a.getID();  
            App.setRoot("AggiungiLotto");
        }
    }

}
