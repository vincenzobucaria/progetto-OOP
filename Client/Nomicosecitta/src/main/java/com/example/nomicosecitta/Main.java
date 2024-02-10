package com.example.nomicosecitta;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main extends Application {


    static Client client;


    static {
        try {



            client = new Client("localhost", 12345);
        } catch (IOException e) {
            System.out.println("Connessione al server remoto non riuscita");
            System.exit(0);
        }
    }




     static Client getClient()
    {
        return client;
    }

    @Override
    public void start(Stage stage) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException {
        launch(args);
    }
}