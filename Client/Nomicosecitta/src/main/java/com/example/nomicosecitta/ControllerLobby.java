package com.example.nomicosecitta;

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


public class ControllerLobby implements Initializable {


    @FXML
    private Button add;
    @FXML
    volatile private VBox chatBox;

    @FXML
    private Pane pane;
    private List<Label> messages = new ArrayList<>();
    @FXML
    private ScrollPane container = new ScrollPane();
    private int index = 0;
    @FXML
    private TextField ChatText;

    private boolean inPartita = false;

    public final Object lock = new Object();

    @FXML
    private ListView ListaGiocatori;

    private boolean mutex;


    volatile private ControllerTurno controllerturno;


    volatile private ControllerFineTurno controllerfineturno;


    volatile private ControllerFinePartita controllerfinepartita;


    private Stage currentStage;

    Client client;

    ArrayList<String> Lista = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.client = Main.getClient();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            // Chiamata al metodo non bloccante
            try {
                client.gestoreMessaggiRicevutiLobby(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void riceviChat(String username, String message) {



        if(inPartita == false) {
            Platform.runLater(() -> {
                System.out.println("Ho ricevuto dalla chat " + username + message);

                Label label = new Label(username + " : " + message);

                label.setTextFill(Color.web("blue"));

                messages.add(label);

                System.out.println(messages.get(index));
                chatBox.getChildren().add(messages.get(index));

                index++;
            });
        }
        else
        {
            Platform.runLater(() -> {
                System.out.println("sto invocando la chat dell'altro controller");
                controllerfineturno.riceviChat(username, message);
            });
        }



    }

    public void riceviChatServer(String message) {
        HelloController.playButtonSound();

        if(inPartita == false) {
            Platform.runLater(() -> {
                System.out.println("Ho ricevuto dalla chat " + message);

                Label label = new Label("Server: " + message);

                label.setTextFill(Color.web("red"));

                messages.add(label);

                System.out.println(messages.get(index));
                chatBox.getChildren().add(messages.get(index));

                index++;
            });
        }
        else
        {
            Platform.runLater(() -> {
                System.out.println("sto invocando la chat dell'altro controller");
                controllerfineturno.riceviChatServer(message);
            });
        }

    }


    public void aggiornaRisultatiTurno()
    {
        controllerfineturno.aggiornaRisultati();
    }

    public synchronized void InviaChat(ActionEvent event) throws IOException {

        //switchtoTurno();
        HelloController.playButtonSound();
        String ChatString = ChatText.getText();
        if (!ChatString.isEmpty()) {

            ChatText.setText("");
            Label label = new Label("hai scritto: " + ChatString);
            label.setTextFill(Color.web("green"));
            messages.add(label);
            chatBox.getChildren().add(messages.get(index));
            index++;
            if (ChatString.startsWith("/")) {
                System.out.println("DEBUG: hai digitato un comando");
                client.inviaComandoAlServer(ChatString);
            } else {

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(() -> {
                    client.inviaMessaggioChat(ChatString);
                    System.out.println("ho inviato msg in chat");
                });
            }
        }


    }



    public void aggiungiGiocatorePartita(String utenti) {
        Platform.runLater(() -> {
            ListaGiocatori.getItems().removeAll(Lista);
            Lista.clear();
        String regex = "\\[(.*?)\\]";
        Pattern pattern = Pattern.compile(regex);

        // Crea un oggetto Matcher sull'input
        Matcher matcher = pattern.matcher(utenti);

        // Trova la corrispondenza estraendo il contenuto tra parentesi quadre
        if (matcher.find()) {
            String extractedString = matcher.group(1);

            // Dividi la stringa estratta utilizzando la virgola come delimitatore
            String[] strings = extractedString.split(", ");

            // Stampa le stringhe separate
            for (String str : strings) {
                Lista.add(str);
                System.out.println(str);
            }
        }
        ListaGiocatori.getItems().addAll(Lista);
    });
    }




    public void Invia(KeyEvent keyEvent) {
    }

    public void switchtoTurno() throws IOException {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TurnnoInsertGUI.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = null;
            if(controllerfineturno != null)
            {
                System.out.println("son nell'if");
                stage = (Stage) controllerfineturno.Invia.getScene().getWindow();
            }
            else
            {
                System.out.println("son qui");
                stage = (Stage) ChatText.getScene().getWindow();
            }

            Scene scene = new Scene(root);
            controllerturno = loader.getController();
            inPartita = true;

            // Configura il controller della nuova scena se necessario
            stage.setScene(scene);
            stage.show();
        });
    }


    public void switchtofinePartita()
    {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FinePartita.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            client.escidallalobby();
            Stage stage = (Stage) controllerfineturno.recuperaTabella().getScene().getWindow();
            Scene scene = new Scene(root);
            inPartita = false;
            controllerfinepartita = loader.getController();
            // Configura il controller della nuova scena se necessario
            stage.setScene(scene);
            stage.show();
        });
    }



    public void switchToFineTurno() throws IOException {

        Platform.runLater(() -> {
            HelloController.stopTimerSound();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TurnoGui.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //controllerturno.InviaTurnoAlServer();
            Stage stage = (Stage) controllerturno.lettera.getScene().getWindow();
            Scene scene = new Scene(root);

           controllerfineturno = loader.getController();
            // Configura il controller della nuova scena se necessario
            stage.setScene(scene);
            stage.show();

        });
    }



    public void switchtomenu(ActionEvent actionEvent) throws IOException {
        HelloController.playButtonSound();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            client.escidallalobby();
            Stage stage = (Stage) ChatText.getScene().getWindow();
            Scene scene = new Scene(root);
            inPartita = false;
            //NewController controller = loader.getController();
            // Configura il controller della nuova scena se necessario
            stage.setScene(scene);
            stage.show();

    }






}



