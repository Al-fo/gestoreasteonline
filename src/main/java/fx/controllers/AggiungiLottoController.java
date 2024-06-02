package fx.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fx.App;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import server.Oggetto;

public class AggiungiLottoController {

    final List<Oggetto> listaOggetti = new ArrayList<>();

    static public int selectedAsta = 0;

    @FXML
    private TextField nomeLottoField;

    @FXML
    private TextField prezzoBaseField;

    @FXML
    private TextField rilancioMinimoField;

    @FXML
    private Label erroreNomeLottoLabel;

    @FXML
    private Label errorePrezzoBaseLabel;

    @FXML
    private Label erroreRilancioMinimoLabel;

    @FXML
    private Button aggiungiOggettoButton;

    @FXML
    private Button annullaButton;

    @FXML
    private ChoiceBox<String> categoriaBox;

    @FXML
    private TableColumn<Oggetto, Oggetto.CATEGORIE> categoriaOggettoColumn;

    @FXML
    private TextArea descrizioneOggettoArea;

    @FXML
    private Label erroreCategoriaLabel;

    @FXML
    private Label erroreInserimentoLabel;

    @FXML
    private Label erroreNomeLabel;

    @FXML
    private Button fattoButton;

    @FXML
    private Label max4Label;

    @FXML
    private TableColumn<Oggetto, String> nomeOggettoColumn;

    @FXML
    private TextField nomeOggettoTextField;

    @FXML
    private Button rimuoviOggettoButton;

    @FXML
    private TableView<Oggetto> tabellaOggetti;

    @FXML
    public void initialize(){
        final List<String> categorie = new ArrayList<>();
            categorie.add("Altro");
            categorie.add("Elettronica");
            categorie.add("Abbigliamento");
            categorie.add("Elettrodomestici");
            categorie.add("Bambini");
            categorie.add("Giardinaggio");
            categorie.add("Scolastico");
            categorie.add("Lavoro");
            categorie.add("Cucina");
            categorie.add("Altro");
        categoriaBox.getItems().addAll(categorie);

        nomeOggettoColumn.setCellValueFactory(new PropertyValueFactory<Oggetto, String>("nome"));
        categoriaOggettoColumn.setCellValueFactory(new PropertyValueFactory<Oggetto, Oggetto.CATEGORIE>("categoria"));

        devisibilizzaLabels();
        tabellaOggetti.setVisible(false);
        rimuoviOggettoButton.setVisible(false);

        nomeLottoField.setVisible(false);
        rilancioMinimoField.setVisible(false);
        prezzoBaseField.setVisible(false);
    }

    @FXML
    void aggiungiAction() {
        boolean aggiungi = true;
        devisibilizzaLabels();
        if(nomeLottoField.getText().equals("") && prezzoBaseField.getText().equals("") && rilancioMinimoField.getText().equals("")){
            erroreNomeLottoLabel.setVisible(true);
            errorePrezzoBaseLabel.setVisible(true);
            erroreRilancioMinimoLabel.setVisible(true);
            nomeLottoField.setVisible(true);
            rilancioMinimoField.setVisible(true);
            prezzoBaseField.setVisible(true);
            return;
        }
        if(nomeLottoField.getText().equals("")){
            erroreNomeLottoLabel.setVisible(true);
            aggiungi = false;
        }
        if(rilancioMinimoField.getText().equals("")){
            erroreRilancioMinimoLabel.setVisible(true);
            aggiungi = false;
        }
        if(prezzoBaseField.getText().equals("")){
            errorePrezzoBaseLabel.setVisible(true);
            aggiungi = false;
        }
        int[] categorie = new int[listaOggetti.size()];
        String[] nomi = new String[listaOggetti.size()];
        String[] descrizioni = new String[listaOggetti.size()];

        String nomeLotto = nomeLottoField.getText();
        double prezzoBase = Double.parseDouble(prezzoBaseField.getText());
        double rilancioMinimo = Double.parseDouble(rilancioMinimoField.getText());

        for(int i = 0; i < listaOggetti.size(); i++){
            categorie[i] = listaOggetti.get(i).getCategoriaValue();
            nomi[i] = listaOggetti.get(i).getNome();
            descrizioni[i] = listaOggetti.get(i).getDescrizione();
        }
        if(aggiungi)
            try {
                App.client.inserisciLotto(listaOggetti.size(), selectedAsta, categorie, nomi, descrizioni, nomeLotto, prezzoBase, rilancioMinimo);
                backToHome();
            } catch (IOException e) {
                switch(e.getMessage()){
                    case "Errore": case "Non connesso":
                        erroreInserimentoLabel.setVisible(true);
                        break;
                    case "Categoria":
                        erroreCategoriaLabel.setVisible(true);
                        break;
                    case "Troppi":
                        break;
                    case "Id":
                        break;
                    case "Asta":
                        break;
                    default: break;
                }
            }
    }

    @FXML
    void inserisciOggettoAction() {
        devisibilizzaLabels();
        boolean inserisci = true;
        String nome = nomeOggettoTextField.getText();
        String descrizione = descrizioneOggettoArea.getText();
        int categoria = categoriaBox.getSelectionModel().getSelectedIndex();
        if(nome == null){
            erroreNomeLabel.setVisible(true);
            inserisci = false;
        }
        if(descrizione == null){
            erroreNomeLabel.setVisible(true);
            inserisci = false;  
        }
        if(categoria == -1){
            erroreCategoriaLabel.setVisible(true);
            inserisci = false;
        }
        if(inserisci)
            try {
                Oggetto oggetto = new Oggetto(categoria, nome, descrizione);
                if(listaOggetti.size() < 4){
                    listaOggetti.add(oggetto);
                    refresh();
                }
            } catch (IOException ignore) {
            }
    }

    @FXML
    void reset(){
        nomeOggettoTextField.clear();
        descrizioneOggettoArea.clear();
        categoriaBox.getSelectionModel().clearSelection();
        tabellaOggetti.getItems().clear();
        devisibilizzaLabels();
        try {
            backToHome();
        } catch (IOException ignore) {
        }
        refresh();
    }

    @FXML
    void rimuoviOggettoAction() {
        Oggetto oggetto = tabellaOggetti.getSelectionModel().getSelectedItem();
        listaOggetti.remove(oggetto);
        refresh();
    }

    public void backToHome() throws IOException{
        App.setRoot("View");
    }

    private void devisibilizzaLabels(){
        erroreCategoriaLabel.setVisible(false);
        erroreInserimentoLabel.setVisible(false);
        erroreNomeLabel.setVisible(false);
        erroreNomeLottoLabel.setVisible(false);
        errorePrezzoBaseLabel.setVisible(false);
        erroreRilancioMinimoLabel.setVisible(false);
    }

    private void refresh(){
        tabellaOggetti.getItems().clear();
        tabellaOggetti.visibleProperty().bind(new SimpleBooleanProperty(!listaOggetti.isEmpty()));
        if(!listaOggetti.isEmpty())
            tabellaOggetti.getItems().setAll(listaOggetti);
        rimuoviOggettoButton.visibleProperty().bind(new SimpleBooleanProperty(!listaOggetti.isEmpty()));
        aggiungiOggettoButton.disableProperty().bind(new SimpleBooleanProperty(listaOggetti.size() == 4));
        fattoButton.disableProperty().bind(new SimpleBooleanProperty(listaOggetti.isEmpty()));
    }
}
