package com.example.nomicosecitta;
public class Utente{

    String username;
    String rango;
    int punteggioGlobale;
    int partiteVinte;
    int partiteGiocate;


    public Utente(String username, String rango, int punteggioGlobale, int partiteVinte, int partiteGiocate)
    {
        this.username = username;
        this.rango = rango;
        this.punteggioGlobale = punteggioGlobale;
        this.partiteVinte = partiteVinte;
        this.partiteGiocate = partiteGiocate;
    }


    public String getUsername()
    {
        return username;
    }
    public String getPunteggioGlobale()
    {
        return String.valueOf(punteggioGlobale);
    }
    public String getRango()
    {
        return rango;
    }
    public String getPartiteVinte()
    {
        return String.valueOf(partiteVinte);
    }
    public String getPartitegiocate()
    {
        return String.valueOf(partiteGiocate);
    }


}


