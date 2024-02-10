package com.example.nomicosecitta;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerProfilo implements Initializable {
    @FXML
    private Label NomeProfilo;
    @FXML
    private Label Rank;
    @FXML
    private Label PartiteGiocate;
    @FXML
    private Label PartiteVinte;
    @FXML
    private Label WinRatio;
    @FXML
    private Label SuperScore;
    @FXML
    private Label FaiParte;
    @FXML
    private ProgressBar ProgressRank;

    private ObservableList<String[]> data;
    private ArrayList<Utente> classificatop100;
    @FXML
    private TableView classifica;

    @FXML
    private TableColumn username;
    @FXML
    private TableColumn punteggio;
    private Client client;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = Main.getClient();
        data = FXCollections.observableArrayList();
        ArrayList<String> colonne = new ArrayList<>();
        colonne.add("username");
        colonne.add("punteggio");
        aggiungiColonne(2, colonne);
        classifica.setItems(data);
        try {
            aggiornaTabella();
            aggiornaProfilo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    private void aggiornaTabella() throws IOException, ClassNotFoundException {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            classificatop100 = client.richiediStatisticheTop100();


            for (Utente stats : classificatop100) {
                String[] popolatabella = new String[2];
                System.out.println("qui");
                System.out.println(stats.getUsername());
                popolatabella[0] = stats.getUsername();
                popolatabella[1] = stats.getPunteggioGlobale();
                data.addAll(popolatabella);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    public void aggiungiColonne(int numeroColonne, ArrayList<String> categorie) {
        for (int i = 0; i < numeroColonne; i++) {
            final int colonnaIndex = i;
            TableColumn<String[], String> nuovaColonna = new TableColumn<>(categorie.get(i));
            nuovaColonna.setCellValueFactory(cellData -> {
                String[] rowData = cellData.getValue();
                if (rowData != null && colonnaIndex < rowData.length) {
                    return new SimpleStringProperty(rowData[colonnaIndex]);
                } else {
                    return new SimpleStringProperty("");
                }
            });
            classifica.getColumns().add(nuovaColonna);
        }
    }

    public void aggiornaProfilo() throws IOException, ClassNotFoundException {

            Utente utente = null;
            try {
                utente = client.richiedistats();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            NomeProfilo.setText(utente.getUsername());
            PartiteGiocate.setText(String.valueOf(utente.getPartitegiocate()));
            PartiteVinte.setText(String.valueOf(utente.getPartiteVinte()));
            setWinRatio(Integer.valueOf(utente.getPartitegiocate()), Integer.valueOf(utente.getPartiteVinte()));
            setProgressBar(utente.getRango(), utente.getPunteggioGlobale());


    }



    public void setWinRatio(int partitegiocate, int partitevinte) {
        if (partitegiocate == 0) {
            WinRatio.setText(String.valueOf(0));
        }
        else {

            System.out.println("partite vinte: " + partitevinte + ", partite giocate: " + partitegiocate);
            double calcolo = ((double) partitevinte / partitegiocate) * 100;
            double arrotondato = Math.round(calcolo * 100.0) / 100.0;
            System.out.println("DEBUG WINRATIO: " + arrotondato);
            WinRatio.setText(String.valueOf(arrotondato));
        }
    }




    public void indietro(ActionEvent actionEvent) throws IOException {
        HelloController.playButtonSound();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        HelloController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();

    }


    public void setProgressBar(String rango, String punteggioGiocatore)
    {
        System.out.println("DEBUG: SONO QUIIIIIIIIIIIIIIIIIIIIIIII!");
        int rangoInt = Integer.parseInt(rango);
        int punteggioGiocatoreInt = Integer.parseInt(punteggioGiocatore);

        float punteggioRango = 200;
        for(int i = 1; i<=rangoInt; i++)
        {
            punteggioRango = (float) (punteggioRango + (punteggioRango*0.5));
        }
        float calcolo = punteggioGiocatoreInt/punteggioRango;
        ProgressRank.setProgress(calcolo);
        System.out.println("DEBUG: " + calcolo);

    }
}

