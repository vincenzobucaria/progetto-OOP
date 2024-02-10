package com.example.nomicosecitta;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerFinePartita implements Initializable {

    Client client;


    String risultatiPartita;

    @FXML
    private TableView<String[]> classifica;

    @FXML
    private Label labvincitore;

    private ObservableList<String[]> data;

    String usernameVincitore;

    public void switchtomenu(ActionEvent actionEvent) throws IOException {
        HelloController.playButtonSound();
        HelloController.flagMusica=false;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        HelloController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
        client.escidallalobby();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = Main.getClient();
        this.risultatiPartita = client.getRisultatiPartita();
        this.usernameVincitore = client.getVincitore();
        data = FXCollections.observableArrayList();
        classifica.setItems(data);
        ArrayList<String> colonne = new ArrayList<>();
        colonne.add("username");
        colonne.add("punteggio");
        aggiungiColonne(2, colonne);
        aggiornarisultati(risultatiPartita);
        labvincitore.setText("Il vincitore è: " + usernameVincitore);
        HelloController.playVictorySound();
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


    private void aggiornarisultati(String risultati)
    {
        String righe[] = risultati.split(",");
        for(String riga: righe)
        {
            String colonna[] = riga.split(":");
            data.add(colonna);
        }

    }




}
