import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class TurnoEdit {

    private Partita partita;
    private ArrayList<String> categorie;

    private int durataTurno;
    private int attesaTurno;
    private ArrayList<gestoreClient> players;
    double quorum = 0.49;
    private double sogliaVotazioni;


    public TurnoEdit(Partita partita, ArrayList<gestoreClient> players, ArrayList<String> categorie, int durataTurno, int attesaTurno)
    {
        this.partita = partita;
        this.players = players;
        this.categorie = categorie;
        this.durataTurno = durataTurno;
        this.attesaTurno = attesaTurno;

        for(gestoreClient player: players)
        {
            player.getGiocatore().newTurno();
        }

        this.sogliaVotazioni = quorum * players.size();

    }

    public void iniziaTurno() throws InterruptedException, IOException {
        char lettera = sorteggiaLettera();
        System.out.println("DEBUG: E' iniziato un turno ed ho estratto una lettera");
        setSpecificheTurnoClient(lettera);
        AttivaInterfacciaInserimentoClient();
        System.out.println("DEBUG: ho imposto ai client di attivare la GUI");
        sleep(durataTurno*1000);
        System.out.println("DEBUG: sto prelevando le parole scritte dai client");
        svuotaBufferUtenti();
        inputParoleTurno(lettera);
        partita.messaggioBroadcast("Tempo scaduto");
        System.out.println("DEBUG: sto calcolando i punteggi");
        calcoloPunteggioTurno();
        stampaTurno();
        AttivaInterfacciaStampaTurno();
        partita.attivaVotazioni();
        partita.InviaMessaggioChat("Server", "il prossimo turno avrà inizio tra " + attesaTurno + " secondi.");
        partita.InviaMessaggioChat("Server", "per segnalare un giocatore -> /segnala [nomegiocatore] [categoria].");
        sleep(attesaTurno*1000);
        fineTurno();
        partita.disattivaVotazioni();
    }

    private void AttivaInterfacciaInserimentoClient() throws IOException {
        String message = "inizio-turno";
        partita.messaggioBroadcast(message);
    }

    private void AttivaInterfacciaStampaTurno() throws IOException {
        String message = "fine-turno";
        partita.messaggioBroadcast(message);
    }

    private void setSpecificheTurnoClient(char lettera) throws IOException {
        String specifiche = "specifiche: " + "categorie:" + categorie.toString() + " lettera:" + lettera + " durata:" + durataTurno;
        System.out.println("DEBUG: ho inviato le seguenti specifiche: " + specifiche);
        partita.messaggioBroadcast(specifiche);
    }


    public void segnalaParola(gestoreClient player, int indiceCategoria) throws IOException {

        ArrayList<Termine> terminiGiocatore = player.getGiocatore().getTerminiTurno();
        terminiGiocatore.get(indiceCategoria).segnalaTermine();
        int segnalazioni = terminiGiocatore.get(indiceCategoria).getSegnalazioni();
        if(segnalazioni > sogliaVotazioni)
        {

            partita.InviaMessaggioChat("Server", "La maggioranza dei giocatori ha deciso di cancellare il termine " + categorie.get(indiceCategoria) + " del giocatore " + player.getGiocatore().getUsername());
            terminiGiocatore.get(indiceCategoria).setParola("");
            calcoloPunteggioTurno();
            stampaTurno();
            partita.messaggioBroadcast("aggiorna-risultati-turno");
        }



    }


    private void svuotaBufferUtenti() throws IOException {

        for (gestoreClient player : players) {
            System.out.println("Sto prelevando gli input del player...");
            ArrayList<String> parole = new ArrayList<>();
            player.riceviDalClientParoleNB();
        }
    }



    private void inputParoleTurno(char letteraSorteggiata) throws IOException {
        String client_input;

        for (gestoreClient player : players) {
            System.out.println("Sto prelevando gli input del player...");
            ArrayList<String> parole = new ArrayList<>();
            client_input = player.riceviDalClientParoleNB();
            //Ricevuto l'input grezzo dal client, devo controllare che non sia null.
            //se è null significa che l'Giocatore non ha scritto compeltamente niente.
            int i=0;
            while (i < categorie.size()) {
                Termine newTermine = new Termine("");
                player.getGiocatore().addTermineTurno(newTermine);
                i = i+1;
            }

            if (client_input == null)
            {
                System.out.println("il player non ha inserito nulla");
                continue;
            }

            else
            {
                ArrayList<String> elements = sanitizzaInput(player, client_input);
                int j = 0;
                for(String element: elements)
                {

                    System.out.println("Elemento:" + element);
                    if (!element.isEmpty() && (element.toUpperCase().charAt(0) == letteraSorteggiata)) {
                            System.out.println("Ho impostato la parola");
                            player.getGiocatore().getTerminiTurno().get(j).setParola(element);
                        }
                        j = j+1;

                    }

                    player.getGiocatore().stampaTerminiTurno();
                }
            System.out.println("Ho finito di prelevare l'input del player...");
            }
        }



    private ArrayList<String >sanitizzaInput(gestoreClient player, String client_input) {
            System.out.println(player.getGiocatore().getUsername() + "ha scritto in origine: " + client_input);
            String[] elements = client_input.split(",");
            ArrayList <String> sanitized_elements = new ArrayList<String>();
            System.out.println("ho ottenuto i seguenti elementi: " + elements);
            for(String element: elements)
            {

                element.replaceAll("\\s+", "");
                element = element.trim();
                System.out.println(element);
                sanitized_elements.add(element);
                }
        System.out.println("Ho tolto gli spazi: " + elements);
        return sanitized_elements;
            }



    private void calcoloPunteggioTurno()
    {

        int iMax = categorie.size();
        int i = 0;
        int punteggioBonus;
        System.out.println(iMax);

        while (i < iMax) {


            for (gestoreClient player : players) {
                //System.out.println("SONO NEL FOR");
                punteggioBonus = 20;
                ArrayList<Termine> termini = player.getGiocatore().getTerminiTurno();
                String termineDaConfrontare = termini.get(i).getParola();
                if(termineDaConfrontare == "")
                {
                    termini.get(i).setPunteggio(0);
                    System.out.println("DEBUG: parola vuota, punteggio 0");
                    player.getGiocatore().calcolaPunteggioTurno();
                    continue;
                }
                for (gestoreClient player2 : players)
                {
                    //System.out.println("SONO NEL SECONDO FOR");
                    if (player == player2) {
                        //System.out.println("BREAK");
                        continue;
                    }


                    ArrayList<Termine> termini2 = player2.getGiocatore().getTerminiTurno();
                    String termineDaConfrontare2 = termini2.get(i).getParola();

                    if(termineDaConfrontare2 == "")
                    {
                        continue;
                    }
                    punteggioBonus = 0;

                    if (termineDaConfrontare.equalsIgnoreCase(termineDaConfrontare2)) {
                        termini.get(i).setPunteggio(5);
                        continue;
                    } else {
                        termini.get(i).setPunteggio(10);
                    }
                }
                if(punteggioBonus == 20)
                {
                    System.out.println("DEBUG: ho assegnato punteggio Bonus");
                    termini.get(i).setPunteggio(20);
                }
                player.getGiocatore().calcolaPunteggioTurno();
            }

            i = i+1;
        }
    }


    private void stampaTurno() throws IOException {
        //chatSwitch(false);
        int i = 0;
        ArrayList<ArrayList<String>> risultatiTurnoGenerale = new ArrayList<>();
        for (gestoreClient player : players) {
            System.out.println("sono qui for");
            ArrayList<Termine> termini = player.getGiocatore().getTerminiTurno();
            ArrayList<String> risultatiTurnoGiocatore = new ArrayList<>();
            i = 0;
            risultatiTurnoGiocatore.add(player.getGiocatore().getUsername());
            while (i < categorie.size())
                {
                    risultatiTurnoGiocatore.add(termini.get(i).getParola());
                     i = i+1;
                }
            risultatiTurnoGiocatore.add(String.valueOf(player.getGiocatore().getPunteggioTurno()));
            risultatiTurnoGenerale.add(risultatiTurnoGiocatore);
        }
        partita.messaggioBroadcast("risultati-turno:" + risultatiTurnoGenerale.toString());
    }


    private char sorteggiaLettera()
    {
        String alfabeto = "ABCDEFGHILMNOPQRSTUVZ";
        Random random = new Random();
        int randomIndex = random.nextInt(alfabeto.length());
        return alfabeto.charAt(randomIndex);
    }

    private void fineTurno()
    {
        System.out.println("DEBUG: il turno è terminato");
        for(gestoreClient player: players)
        {
            player.getGiocatore().convalidaTurno();
        }
    }

}
