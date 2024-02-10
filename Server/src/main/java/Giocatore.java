import java.util.ArrayList;


public class Giocatore extends Utente{



    volatile ArrayList<Termine> terminiPartita;
    volatile ArrayList<Termine> terminiTurno;
    boolean loggato = false;

    int punteggioPartita = 0;
    volatile int punteggioTurno;

    public Giocatore(String username, String rango, int punteggio, int partiteVinte, int partiteGiocate) {
        super(username, rango, punteggio, partiteVinte, partiteGiocate);
        this.punteggioTurno = 0;
        this.terminiPartita = new ArrayList<>();
        loggato = true;
    }


    public void newTurno()
    {
        punteggioTurno = 0;
        terminiTurno = new ArrayList<>();
    }

    public void resetPartita()
    {
        this.punteggioTurno = 0;
        this.punteggioPartita = 0;
        this.terminiPartita = new ArrayList<Termine>();
        this.terminiTurno = new ArrayList<Termine>();
    }


    public void addTermineTurno(Termine termine)
    {
        terminiTurno.add(termine);
        System.out.println("ho aggiunto all'utente " + getUsername() + " il termine " + termine.getParola());
    }

    public ArrayList<Termine> getTerminiTurno()
    {
        return terminiTurno;
    }


    public void stampaTerminiTurno(){
        for(Termine termini: terminiTurno)
        {
            System.out.println("L'utente" + getUsername() + "conserva: " + termini.getParola());
        }
    }


    public void calcolaPunteggioTurno()
    {
        punteggioTurno = 0;
        for(Termine termine: terminiTurno)
        {
            punteggioTurno = punteggioTurno + termine.getPunteggio();
        }
    }

    public int getPunteggioTurno()
    {
        return punteggioTurno;
    }

    public int getPunteggioPartita()
    {
        return punteggioPartita;
    }

    public void convalidaTurno()
    {
        punteggioPartita = punteggioPartita + punteggioTurno;
        for(Termine termine: terminiTurno)
        {
            terminiPartita.add(termine);
        }
    }


    public void addVittoria()
    {
        this.partiteVinte = partiteVinte+1;
    }

    public void addPartitaGiocata()
    {
        this.partiteGiocate = partiteGiocate + 1;
    }

    public void updatePunteggioGlobale()
    {
        this.punteggioGlobale = punteggioGlobale + punteggioPartita;
    }

    public void updateRango()
    {

        System.out.println("Sto aggiornando il rango");
        int rank = 1;
        double puntirank = 200;
        int rankgiocatore = 0;

        while(punteggioGlobale>puntirank)
        {
            puntirank = puntirank + (puntirank*0.5);
            rank ++;
            System.out.println(rank);
        }
        rankgiocatore = rank;
        System.out.println("Ho finito di aggiornare il rango");
        this.rango = String.valueOf(rankgiocatore);
    }

    public void updateStats(Database database)
    {
        database.dbAggiornaStatsUtente(username, this.punteggioGlobale, this.partiteGiocate, this.partiteVinte, this.rango);
    }

    public int getPunteggioPartita(Object o) {
        System.out.println(punteggioPartita);
        return punteggioPartita;
    }
}
