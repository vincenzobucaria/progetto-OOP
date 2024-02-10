package com.example.demo;
import static java.lang.Thread.sleep;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController {

    @FXML
    private Spinner<Integer> Spinner;
    int valore;

    volatile ArrayList<String> ListaCategorie= new ArrayList<>();
    @FXML
    private ListView ListViewCategorie;
    @FXML
    private TextField CategoriaField;






    public void Start(ActionEvent event) {
        SpinnerMethod();
        //Aggiungo i valori di default alla lista caricandogli l'arraylist con i 3 valori di default
        ListaCategorie.add("Nomi");
        ListaCategorie.add("Cose");
        ListaCategorie.add("Città");
        ListViewCategorie.getItems().addAll(ListaCategorie);



    }
    public void PopolaListViewCategorie(ActionEvent event) throws InterruptedException {
        String CategoriaString=CategoriaField.getText();
        CategoriaField.setText("");
        for( String categoria : ListaCategorie ){
            if (CategoriaString.equals(categoria)){
                return;

            }

        }
        //Aggiungo comunque il nuovo elemento nell'arraylist(ci è utile per creare la partita)
        ListaCategorie.add(CategoriaString);
        //qui aggiungo il singolo elemento nuovo alla lista(non tutto l'array)
        ListViewCategorie.getItems().add(CategoriaString);

    }
    public void SpinnerMethod(){
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
        valueFactory.setValue(1);
        Spinner.setValueFactory(valueFactory);
    }
}