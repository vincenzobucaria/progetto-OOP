package com.example.demogui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HelloController {


    @FXML
    private Button add;
    @FXML
    private VBox chatBox;
    private List<Label> messages = new ArrayList<>();
    @FXML
    private ScrollPane container = new ScrollPane();
    private int index = 0;
    @FXML
    private TextField ChatText;
    @FXML
    private ListView ListaGiocatori;

    ArrayList<String> Lista= new ArrayList<>();









        public void InviaChat(ActionEvent event) {
            String ChatString = ChatText.getText();
            ChatText.setText("");
            Label label = new Label("Utente X:" +ChatString);
            label.setTextFill(Color.web("green"));
            messages.add(label);
            chatBox.getChildren().add(messages.get(index));
            index++;
        }
        public void PopolaListaGiocatori(){
            //lista è un arraylist
            Lista.add("pizza");
            Lista.add("cane");
            ListaGiocatori.getItems().addAll(Lista);

        }


        };





