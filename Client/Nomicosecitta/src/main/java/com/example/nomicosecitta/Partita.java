package com.example.nomicosecitta;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Partita {
    private String nome = "Pierpalo";
    private int numgiocatori;

    public Partita(String nome, int numgiocatori)
    {
        this.nome = nome;
        this.numgiocatori = numgiocatori;
    }


    public String getNome(){
        return nome;
    }



public int getNumgiocatori()
{
    return numgiocatori;
}
}
