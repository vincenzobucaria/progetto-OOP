package com.example.nomicosecitta;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ControllerTurno implements Initializable {
   @FXML
    private ChoiceBox<String> SelectionBox;
    @FXML
    private TextField ParolaField;
    private Client client;

    @FXML
    private Label invioeffettuato;
    @FXML
    public Label lettera;
    @FXML
    private Label numeroTurno;
    @FXML
    private ListView paroleins;
    private ArrayList<String> categorie = new ArrayList<>();
    private Hashtable<String, String> ParoleTurno = new Hashtable<String, String>();
    private ArrayList<String> Lista = new ArrayList<>();
    @FXML
    private Label tempo;
    private String durataTurno = "1";
    private int seconds = 0;
    private Timeline timeline;
    private final IntegerProperty timeSeconds = new SimpleIntegerProperty(0);

    volatile private boolean paroleInviate = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HelloController.playTimerSound();
        HelloController.stopMusica();
        this.client = Main.getClient();
        this.categorie = client.getCategoriePartita();
        this.durataTurno = client.getDurataTurno();
        SelectionBox.getItems().addAll(categorie);
        SelectionBox.getSelectionModel().selectFirst();
        lettera.setText(client.getLetteraTurno());
        numeroTurno.setText(String.valueOf(client.getNumTurno()));

        for(String categoria: categorie)
        {
            ParoleTurno.put(categoria,"");
            Lista.add("");
        }


        tempo.textProperty().bind(timeSeconds.asString());

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> updateTime()));
        timeline.setCycleCount(Animation.INDEFINITE); // repeat over and over again
        timeSeconds.set(Integer.parseInt(durataTurno));
        timeline.play();



    }


    private void updateTime() {
        // increment seconds
        int seconds = timeSeconds.get();
        timeSeconds.set(seconds-1);
        if(seconds < 3)
        {
            InviaTurnoAlServer();
        }
    }
    public void SetParola(){
        HelloController.playButtonSound();
        String myChoice = SelectionBox.getValue();
        String Parola = ParolaField.getText();
        if (!Parola.isEmpty()) {
            if (Parola.contains(",")) {
                System.out.println("Non puoi usare il carattere ,");
            } else {
                ParoleTurno.put(myChoice, Parola);
                paroleins.getItems().removeAll(Lista);
                System.out.println("DEBUG: hai aggiunto" + Parola + " in " + myChoice);
                String payload = myChoice + ": " + Parola;
                Lista.set(categorie.indexOf(myChoice), payload);
                paroleins.getItems().addAll(Lista);
                System.out.println("Nella LW: " + Lista.toString());
                ParolaField.setText("");
            }
        }
    }

    private void InviaTurnoAlServer()
    {
        if(paroleInviate == false)
        {
            String Parole = "";
            int i = 0;

            for(String categoria: categorie)
            {
                if(i == 0)
                {
                    Parole = Parole + ParoleTurno.get(categoria) + ",";
                    i = 1;
                }
                else if(i == 1)
                {
                    Parole = Parole + ParoleTurno.get(categoria);
                    i = 2;
                }
                else
                {
                    Parole = Parole + "," + ParoleTurno.get(categoria);
                }

            }

            System.out.println("Nel turno hai inserito: " + Parole);
            String[] strings = Parole.split(",");
            for(String string: strings)
            {
                System.out.println(string);
            }
            client.inviaParole(Parole);
            paroleInviate = true;
        }
    }




    private void switchtolobbylist() throws IOException {

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = (Stage) lettera.getScene().getWindow();
            Scene scene = new Scene(root);

            HelloController controller = loader.getController();
            // Configura il controller della nuova scena se necessario
            stage.setScene(scene);
            stage.show();
        });
    }


    public void escipartita(ActionEvent actionEvent) throws IOException {
        HelloController.flagMusica=false;
        HelloController.stopTimerSound();
        HelloController.playButtonSound();
        client.escidallalobby();
        switchtolobbylist();
    }
}