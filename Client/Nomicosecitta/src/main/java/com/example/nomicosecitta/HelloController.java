package com.example.nomicosecitta;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable{
    private Stage stage;
    private Scene scene;
    private Parent root;

    int tentativi = 0;

    private Client client;


    @FXML
    TextField Username;
    @FXML
    PasswordField Password;
    @FXML
    Label StatusLable;

    @FXML
    private Button Bottone;
    static MediaPlayer mediaPlayer;
    static MediaPlayer buttonPlayer;
    static MediaPlayer timerPlayer;
    static MediaPlayer victoryPlayer;
    public static boolean flagMusica=false;

    @FXML



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!flagMusica){
            playHitSound();
            flagMusica=true;
        }
    }
    public static void playVictorySound() {
        String path = "ff7_victory.mp3";
        Media media = new Media(new File(path).toURI().toString());
        victoryPlayer = new MediaPlayer(media);
        victoryPlayer.play();
        victoryPlayer.setVolume(0.4);
    }
    public static void playTimerSound() {
        String path = "timer.mp3";
        Media media = new Media(new File(path).toURI().toString());
        timerPlayer = new MediaPlayer(media);
        timerPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        timerPlayer.play();
    }
    public static void stopTimerSound(){
        timerPlayer.stop();

    }

        public static void playHitSound() {
        String path = "SpongeBob Production Music Twelfth Street Rag.mp3";
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        mediaPlayer.setVolume(0.06);
    }

    public static void playButtonSound(){
        String path = "fixbubble.mp3";
        Media media = new Media(new File(path).toURI().toString());
        buttonPlayer = new MediaPlayer(media);
        buttonPlayer.play();
        buttonPlayer.setVolume(1);
    }

    public static void stopMusica(){
        mediaPlayer.stop();
    }


    public void LoginGUI(ActionEvent event) throws IOException {
        playButtonSound();
        this.client = Main.getClient();
        String UsernameString = Username.getText();
        String PasswordString = Password.getText();

        if(UsernameString.contains(" "))
        {
            StatusLable.setText("L'username non può contenere spazi");
            StatusLable.setOpacity(100);
        }
        else {

            boolean contieneCaratteriSpeciali = UsernameString.matches(".*[,:\\-*].*");
            if(contieneCaratteriSpeciali == true)
            {
                StatusLable.setText("L'username non può contenere caratteri speciali");
                StatusLable.setOpacity(100);
            }
            else {
                if (!UsernameString.isEmpty() && !PasswordString.isEmpty()) {

                    int loginStatus = client.login(UsernameString, PasswordString);
                    if (loginStatus == 0) {
                        //Per settare il messaggio di errore:
                        System.out.println("DEBUG: credenziali errate");
                        StatusLable.setText("Credenziali errate");
                        this.tentativi = tentativi + 1;
                        if (tentativi > 3) {
                            StatusLable.setText("Hai effettuato troppi tentativi. Riprova tra 1 minuto");
                            this.tentativi = 0;
                        }
                        StatusLable.setOpacity(100);
                    } else if (loginStatus == 1) {
                        switchtomainmenu();
                    } else if (loginStatus == 2) {
                        StatusLable.setText("Utente già loggato");
                        StatusLable.setOpacity(100);
                        return;
                    }
                }
            }
        }
    }

    public void setLabelLogin()
    {
        StatusLable.setText("Utente già loggato");
        StatusLable.setOpacity(100);
    }
    public void switchtomainmenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bo.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) StatusLable.getScene().getWindow();
        Scene scene = new Scene(root);

        //NewController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
    }


    public void visualizzaclassifica(ActionEvent actionEvent) throws IOException {
        playButtonSound();

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("TurnnoInsertGUI.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Profilo.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) Bottone.getScene().getWindow();
        Scene scene = new Scene(root);

        //NewController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();

    }











/*
    @FXML
    private void CambiaIntestazione() {
        TableColumn<String, String> nomeLobbyColumn = (TableColumn<String, String>) Tabelle.getColumns().get(0);
        TableColumn<String, String> numGiocatoriColumn = (TableColumn<String, String>) Tabelle.getColumns().get(1);
        nomeLobbyColumn.setText("Nuovo Testo 1");
        numGiocatoriColumn.setText("Nuovo Testo 2");
    }


    public void switchtoscene1(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("bo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene= new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    public void switchtoLobby(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        NewController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
    }

    public void CreaLobby(ActionEvent event) {
        //crea lobby
    }

    */

    public void RegistrazioneGUI(ActionEvent actionEvent) throws IOException {
        playButtonSound();
        this.client = Main.getClient();
        String UsernameString = Username.getText();
        String PasswordString = Password.getText();


        if (UsernameString.contains(" ")) {
            StatusLable.setText("L'username non può contenere spazi");
            StatusLable.setOpacity(100);
        } else {

            boolean contieneCaratteriSpeciali = UsernameString.matches(".*[,:\\-*].*");
            if (contieneCaratteriSpeciali == true) {
                StatusLable.setText("L'username non può contenere caratteri speciali");
                StatusLable.setOpacity(100);
            } else {


                if (!UsernameString.isEmpty() && !PasswordString.isEmpty()) {


                    int loginStatus = client.registrati(UsernameString, PasswordString);
                    if (loginStatus == 0) {
                        //Per settare il messaggio di errore:
                        System.out.println("DEBUG: utente già registrato");
                        StatusLable.setText("Utente già registrato");
                        this.tentativi = tentativi + 1;
                        if (tentativi > 3) {
                            StatusLable.setText("Hai effettuato troppi tentativi. Riprova tra 1 minuto");
                            this.tentativi = 0;
                        }
                        StatusLable.setOpacity(100);
                    } else if (loginStatus == 1) {
                        switchtomainmenu();
                    }
                }
            }
        }
    }

    public void switchtoLobbyList(ActionEvent actionEvent) throws IOException {
        playButtonSound();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        NewController controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
    }

    public void quit(ActionEvent actionEvent) {
        playButtonSound();
        System.exit(0);
    }
}