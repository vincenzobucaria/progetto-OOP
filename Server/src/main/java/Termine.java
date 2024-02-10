public class Termine {

    private String parola;
    volatile private int punteggio;

    volatile private int segnalazioni;




    public Termine(String parola)
    {
        this.parola = parola;
        segnalazioni = 0;
        punteggio = 0;
    }
    public String getParola()
    {
        return parola;
    }

    public int getPunteggio()
    {
        return punteggio;
    }

    public String toString()
    {
        String message = getParola() + String.valueOf(punteggio);
        return message;
    }
    public void setPunteggio(int punteggio)
    {
        System.out.println("Mi hanno chiamato " + punteggio);
        this.punteggio = punteggio;
    }

    public void setParola(String parola)
    {
        this.parola = parola;
    }


    public void segnalaTermine()
    {
        segnalazioni = segnalazioni + 1;
    }

    public int getSegnalazioni()
    {
        return segnalazioni;
    }



}
