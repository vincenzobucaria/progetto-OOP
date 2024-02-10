package com.example.nomicosecitta;
import static java.lang.Thread.sleep;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerNewPartita implements Initializable {

    @FXML
    private Spinner<Integer> SpinnerTurni;
    @FXML
    private Spinner<Integer> SpinnerGiocatori;
    @FXML
    private Spinner<Integer> SpinnerTempo;
    private int ValoreTurni;
    private int ValoreGiocatori;
    private int ValoreTempo;

    Client client;

    volatile ArrayList<String> ListaCategorie= new ArrayList<>();
    @FXML
    private ListView ListViewCategorie;
    @FXML
    private TextField CategoriaField;
    @FXML
    private TextField NomePartita;




    public void initialize(URL location, ResourceBundle resources) {
        this.client = Main.getClient();
        SpinnerMethodTurni();
        SpinnerMethodGiocatori();
        SpinnerMethodTempo();
        //Aggiungo i valori di default alla lista caricandogli l'arraylist con i 3 valori di default
        ListaCategorie.add("Nomi");
        ListaCategorie.add("Cose");
        ListaCategorie.add("Città");
        ListViewCategorie.getItems().addAll(ListaCategorie);

    }





    public void PopolaListViewCategorie(ActionEvent event) throws InterruptedException {
        HelloController.playButtonSound();
        String CategoriaString=CategoriaField.getText();
        if(CategoriaString.isEmpty())
        {
            return;
        }
        CategoriaField.setText("");
        for( String categoria : ListaCategorie ){
            if (CategoriaString.equalsIgnoreCase(categoria)){
                return;
            }

        }
        //Aggiungo comunque il nuovo elemento nell'arraylist(ci è utile per creare la partita)
        ListaCategorie.add(CategoriaString);
        //qui aggiungo il singolo elemento nuovo alla lista(non tutto l'array)
        ListViewCategorie.getItems().add(CategoriaString);

    }
    public void SpinnerMethodTurni(){
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
        valueFactory.setValue(1);
        SpinnerTurni.setValueFactory(valueFactory);
    }
    public void SpinnerMethodGiocatori(){
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6);
        valueFactory.setValue(1);
        SpinnerGiocatori.setValueFactory(valueFactory);
    }
    public void SpinnerMethodTempo(){
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 90);
        valueFactory.setValue(1);
        SpinnerTempo.setValueFactory(valueFactory);
    }

    public void CreaPartita(ActionEvent event) throws IOException {
        HelloController.playButtonSound();
        String NomePartitaString=NomePartita.getText();
        ValoreTurni=SpinnerTurni.getValue();
        ValoreGiocatori=SpinnerGiocatori.getValue();
        ValoreTempo=SpinnerTempo.getValue();

        if(!NomePartitaString.isEmpty()) {
            client.creaPartita(NomePartitaString, ValoreTurni, ValoreGiocatori, ValoreTempo, ListaCategorie);
            switchtoLobby();
        }


    }


    public void switchtoLobby() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatGUI.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) NomePartita.getScene().getWindow();
        Scene scene = new Scene(root);

        //ControllerLobby controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
    }


    public void Indietro(ActionEvent event) throws IOException {
        HelloController.playButtonSound();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        HelloController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
    }
}