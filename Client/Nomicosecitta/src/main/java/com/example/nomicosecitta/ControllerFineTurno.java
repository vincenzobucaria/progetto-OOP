package com.example.nomicosecitta;

import javafx.application.Platform;
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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerFineTurno implements Initializable {
    @FXML
    public Button Invia;
    @FXML
    private VBox chatBox;
    private List<Label> messages = new ArrayList<>();
    @FXML
    private ScrollPane container = new ScrollPane();
    private int index = 0;
    @FXML
    private TextField ChatText;
    @FXML
    private TableView<String[]> tabella;

    private ObservableList<String[]> data;

    private ArrayList<String> categorie = new ArrayList<>();

    private Client client;

    private String risultati;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        data = FXCollections.observableArrayList();
        tabella.setItems(data);
        this.client = Main.getClient();

        categorie = client.getCategoriePartita();
        categorie.add(0, "Giocatore");
        categorie.add("Punteggio");
        aggiungiColonne(categorie.size(), categorie);
        aggiornaRisultati();

    }


    public TableView recuperaTabella()
    {
        return tabella;
    }
    public void AggiungiColonna(ActionEvent event) {
        aggiungiColonne(categorie.size(), categorie);
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
            tabella.getColumns().add(nuovaColonna);
        }
    }


    public void aggiornaRisultati() {
        risultati = client.getRisultatiTurno();
        estraiRisultati(risultati);
    }

    private void estraiRisultati(String input) {

        Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
        Matcher matcher = pattern.matcher(input);

        ArrayList<ArrayList<String>> resultList = new ArrayList<>();

        while (matcher.find()) {
            String match = matcher.group(1);
            String[] elements = match.split(", ");
            ArrayList<String> sottolista = new ArrayList<>(Arrays.asList(elements));
            resultList.add(sottolista);
        }
        data.clear();
        for (ArrayList<String> result : resultList) {
            data.add(result.toArray(new String[result.size()]));
        }
    }


    public void riceviChat(String username, String message) {


        System.out.println("metodo ricevIChat di FineTurno");

        System.out.println("xd Ho ricevuto dalla chat " + username + message);

        Label label = new Label(username + " : " + message);

        label.setTextFill(Color.web("blue"));

        messages.add(label);

        System.out.println(messages.get(index));
        chatBox.getChildren().add(messages.get(index));

        index++;


    }


    public void InviaChat(ActionEvent event) {

            HelloController.playButtonSound();
            String ChatString = ChatText.getText();
        if (!ChatString.isEmpty()) {
            ChatText.setText("");
            Label label = new Label("hai scritto:" + ChatString);
            label.setTextFill(Color.web("green"));
            if (ChatString.startsWith("/")) {
                System.out.println("DEBUG: hai digitato un comando");
                client.inviaComandoAlServer(ChatString);
            } else {
                messages.add(label);
                chatBox.getChildren().add(messages.get(index));
                index++;


                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(() -> {
                    client.inviaMessaggioChat(ChatString);
                    System.out.println("ho inviato msg in chat");
                });
            }
        }
    }


    public void riceviChatServer(String message) {
        System.out.println("Ho ricevuto dalla chat " + message);
        Label label = new Label("Server: " + message);
        label.setTextFill(Color.web("red"));
        messages.add(label);
        System.out.println(messages.get(index));
        chatBox.getChildren().add(messages.get(index));
        index++;
    }



    public void setCurrentStage(Stage stage) {
    }


    @FXML
    private void switchtolobbylist() throws IOException {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = (Stage) Invia.getScene().getWindow();
            Scene scene = new Scene(root);

            HelloController controller = loader.getController();
            // Configura il controller della nuova scena se necessario
            stage.setScene(scene);
            stage.show();
        });
    }

    public void esci(ActionEvent actionEvent) throws IOException {
        HelloController.playButtonSound();
        HelloController.flagMusica=false;
        client.escidallalobby();
        switchtolobbylist();
    }

}


