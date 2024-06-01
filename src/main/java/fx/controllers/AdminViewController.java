package fx.controllers;

import java.io.IOException;
import java.util.List;

import fx.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import server.GestoreAste;
import server.GestoreAste.Asta;
import server.Lotto;

public class AdminViewController {

    @FXML
    private TableColumn<Asta, Integer> IDAstaColumn;

    @FXML
    private TableColumn<Lotto, String> IndirizzoLottoColumn;

    @FXML
    private TableColumn<Lotto, String> NomeLottoColumn;

    @FXML
    private TableColumn<Lotto, Double> PrezzoLottoColumn;

    @FXML
    private TableColumn<Lotto, String> VincitoreLottoColumn;

    @FXML
    private Button aggiungiButton;

    @FXML
    private TableColumn<Asta, Boolean> astaApertaColumn;

    @FXML
    private TableView<Asta> asteList;

    @FXML
    private Button chiudiAstaButton;

    @FXML
    private Label erroreDisconnessioneLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private TableView<Lotto> lottiTable;

    @FXML
    public void initialize(){
        erroreDisconnessioneLabel.setVisible(false);
        lottiTable.setVisible(false);

        IDAstaColumn.setCellValueFactory(new PropertyValueFactory<Asta, Integer>("ID"));
        astaApertaColumn.setCellValueFactory(new PropertyValueFactory<Asta, Boolean>("aperta"));

        NomeLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, String>("nomeLotto"));
        PrezzoLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, Double>("valoreAttuale"));
        VincitoreLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, String>("vincitoreAttuale"));
        IndirizzoLottoColumn.setCellValueFactory(new PropertyValueFactory<Lotto, String>("indirizzoMulticast"));
        refreshAste();
    }

    @FXML
    void aggiungiAction(ActionEvent event) {
        try {
            App.client.creaAsta();
            refreshAste();
        } catch (IOException e) {
            erroreDisconnessioneLabel.setVisible(true);
            erroreDisconnessioneLabel.setText("Errore, riprova");
        }
    }

    @FXML
    void chiudiAstaAction(ActionEvent event) {
        Asta a = asteList.getSelectionModel().getSelectedItem();
        if(a != null){
            try {
                App.client.chiudiAsta(a.getID());
                refreshAste();
            } catch (IOException e) {
                erroreDisconnessioneLabel.setVisible(true);
                erroreDisconnessioneLabel.setText("Errore, riprova");
            }
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
        }
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

}
