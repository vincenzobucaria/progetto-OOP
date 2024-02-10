package com.example.nomicosecitta;


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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.Thread.sleep;

public class NewController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private ControllerFineTurno controllerfineturno;

    private ControllerTurno controllerturno;

    private ControllerFinePartita controllerfinepartita;

    @FXML
    private TableColumn<Partita, String> nomepartita;
    @FXML
    private TableColumn<Partita, String> numgiocatori;
    @FXML
    private TableView<Partita> tabella;
    Client client;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTabella();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void setupTabella() throws IOException, ClassNotFoundException {

        nomepartita.setCellValueFactory(new PropertyValueFactory<Partita, String>("nome"));
        numgiocatori.setCellValueFactory(new PropertyValueFactory<Partita, String>("numgiocatori"));
        tabella.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Assicurati che il clic sia singolo
                Partita rigaSelezionata = tabella.getSelectionModel().getSelectedItem();
                if (rigaSelezionata != null) {
                    // Fai qualcosa con la riga selezionata
                    System.out.println("DEBUG, stai accendendo alla partita: " + rigaSelezionata.getNome());
                    try {
                        controllaDisponibilitaPartita(rigaSelezionata);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        aggiornaTabellaOttimizzata();
    }



    public void controllaDisponibilitaPartita(Partita partitaSelezionata) throws IOException, ClassNotFoundException {

        //Controllo che la partita selezionata sia ancora disponibile
        this.client = Main.getClient();
        System.out.println("DEBUG: metodo controllaDisponibilitaPartita");
        String lobbyDisponibili = client.riceviPartite();
        ArrayList<Partita> partiteDisponibili = createPersoneFromParameters(lobbyDisponibili);
        for(Partita partita: partiteDisponibili)
         {
            if(partita.getNome().equals(partitaSelezionata.getNome()))
                {
                    System.out.println("DEBUG: sto provando ad entrare nella lobby");
                    boolean status = client.entraInLobby(partitaSelezionata.getNome());
                    if(status == true)
                    {
                        switchtoLobby();
                        System.out.println("DEBUG: aggiunto correttamente alla lobby");
                        return;
                    }
                    else
                    {
                        System.out.println("DEBUG: partita selezionata non disp");
                    }
                }
         }

            System.out.println("DEBUG: la partita selezionata non è più disponibile");
    }



    public void switchtoLobby() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatGUI.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) tabella.getScene().getWindow();
        Scene scene = new Scene(root);

        //ControllerLobby controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();
    }




        public void aggiornaTabellaOttimizzata() throws IOException, ClassNotFoundException {
            this.client = Main.getClient();
            String lobbyDisponibili = client.riceviPartite();
            if(lobbyDisponibili == null)
            {
                System.out.println("DEBUG UI: partite non disponibili");
                tabella.setItems(null);
                return;
            }
            ArrayList<Partita> partiteDisponibili = createPersoneFromParameters(lobbyDisponibili);
            ObservableList<Partita> listaPartite = FXCollections.observableArrayList();
            if (partiteDisponibili != null) {
                for (Partita partita : partiteDisponibili) {
                    listaPartite.add(partita);

                    tabella.setItems(listaPartite);
                }

            }
            else
            {
                System.out.println("DEBUG: partite non disponibili");
                tabella.setItems(null);
            }
        }



                private ArrayList<Partita> createPersoneFromParameters(String inputString) {
        ArrayList<Partita> personeList = new ArrayList<>();

        // Rimuovi i caratteri di parentesi quadre esterne
        inputString = inputString.substring(1, inputString.length() - 1);

        // Dividi la stringa in base alle virgole esterne
        List<String> pairs = Arrays.asList(inputString.split("\\], \\["));

        // Elabora ogni coppia
        for (String pair : pairs) {
            // Rimuovi i caratteri di parentesi quadre interne
            pair = pair.replace("[", "").replace("]", "");

            // Dividi la coppia in base alla virgola
            List<String> values = Arrays.asList(pair.split(", "));

            // Estrai il nome e il num dalla lista dei valori
            String nome = values.get(0);
            int num = Integer.parseInt(values.get(1));

            // Crea un oggetto Persona e aggiungilo alla lista delle persone
            Partita partita = new Partita(nome, num);
            personeList.add(partita);
        }

        return personeList;
    }




    public void aggiorna(ActionEvent event) throws IOException, ClassNotFoundException {
        HelloController.playButtonSound();
        aggiornaTabellaOttimizzata();


    }

    public void switchtoscene1(ActionEvent event) throws IOException {
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


    public void creanuovapartita(ActionEvent actionEvent) throws IOException {
        HelloController.playButtonSound();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Impostazioni.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
       // ControllerTurno controller = new ControllerTurno();
        //controller = loader.getController();
        // Configura il controller della nuova scena se necessario

        stage.setScene(scene);
        stage.show();

    }
}
