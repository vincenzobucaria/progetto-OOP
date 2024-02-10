import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Partita extends Thread {

    String nomePartita;
    volatile private ArrayList<gestoreClient> utentiInPartita = new ArrayList<>();


    private ArrayList<gestoreClient> utentiIniziali = new ArrayList<>();

    private ArrayList<gestoreClient> clientDisconnessi = new ArrayList<>();
    private int numero_turni;

    private int numMinGiocatori;

    private int durataTurno;

    private ExecutorService executorService = null;

    volatile private boolean impedisciDisconnessione = false;

    int turniGiocati = 0;
    private gestoreClient host;

    volatile private Giocatore Giocatore;
    volatile private boolean partitaAvviata = false;
    volatile private boolean stopPartita = false;

    String messaggio;

    volatile private boolean allowVotazione = false;

    volatile private TurnoEdit turnoCorrente = null;

    volatile private ArrayList<String> usernameGiocatori = new ArrayList<>();

    volatile private ArrayList<String> categorie = new ArrayList<>();



    public Partita(String nomePartita, gestoreClient host, String turni, String numMinGiocatori, String durataTurno, String categorie) {

        System.out.println("DEBUG: sto creando una nuova partita" + nomePartita);
        this.nomePartita = nomePartita;
        // utentiInPartita.add(host);
        this.host = host;
        this.numero_turni = Integer.parseInt(turni);
        this.numMinGiocatori = Integer.parseInt(numMinGiocatori);
        this.durataTurno = Integer.parseInt(durataTurno);
        String regex = "\\[(.*?)\\]";

        // Crea un oggetto Pattern
        Pattern pattern = Pattern.compile(regex);

        // Crea un oggetto Matcher sull'input
        Matcher matcher = pattern.matcher(categorie);

        // Trova la corrispondenza estraendo il contenuto tra parentesi quadre
        if (matcher.find()) {
            String extractedString = matcher.group(1);

            // Dividi la stringa estratta utilizzando la virgola come delimitatore
            String[] stringArray = extractedString.split(", ");

            // Aggiungi le stringhe separate alla lista
            for (String str : stringArray) {
                this.categorie.add(str);
            }
            System.out.println("E' stata creata una nuova partita: " + getCategorie());
        }


    }

    public void run() {


    }


    private void setImpostazioni() {

    }

    public String getNomePartita()
    {
        return nomePartita;
    }

    public int getNumGiocatori()
    {
        return utentiInPartita.size();
    }

    public void attivaVotazioni()
    {
        allowVotazione = true;
    }

    public void disattivaVotazioni()
    {
        allowVotazione = false;
    }

    public void gestoreComandiChat(gestoreClient client, String command) throws InterruptedException, IOException {
        if(command.equals("/iniziaPartita"))
        {
            if(partitaAvviata == false) {
                if (client == this.host) {
                    if (getNumeroGiocatori() >= numMinGiocatori) {
                        System.out.println("DEBUG: l'host ha avviato la partita");
                        client.clientRiceviMessaggioChat("Server", "hai avviato la partita");
                        InviaMessaggioChat("Server", "l'host ha avviato la partita. Avvio partita in 10 secondi.");
                        partitaAvviata = true;
                        this.executorService = Executors.newSingleThreadExecutor();
                        executorService.submit(() -> {
                            // Chiamata al metodo non bloccante
                            try {
                                IniziaPartita();
                                executorService.shutdown();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } else {
                        client.clientRiceviMessaggioChat("Server", "numero di giocatori insufficiente");
                    }
                } else {
                    System.out.println("DEBUG: un non host ha provato ad avviare la partita");
                    client.clientRiceviMessaggioChat("Server", "soltanto l'host può avviare la partita");
                }
            }
            else
            {
                System.out.println("DEBUG: la partita è già avviata!");
                client.clientRiceviMessaggioChat("Server", "la partita è già avviata.");
            }
        }

        else if(command.startsWith("/segnala"))
        {
            gestoreSegnalazioni(client, command);
        }

    }


    public String getCategorie()
    {
        return categorie.toString();
    }


    public void aggiungiInPartita(gestoreClient player) throws IOException {

        System.out.println("DEBUG: un nuovo Giocatore è stato inserito in partita");
        this.utentiInPartita.add(player);
        Giocatore = player.getGiocatore();
        String nome_Giocatore = Giocatore.getUsername();
        usernameGiocatori.add(nome_Giocatore);
        String messaggioChat = "il giocatore " + nome_Giocatore + " si è connesso alla lobby!";
        messaggio = "nuovo Giocatore in lobby:" + usernameGiocatori.toString();
        System.out.println(messaggio);
        messaggioBroadcast(messaggio);
        player.getGiocatore().resetPartita();
        System.out.println(utentiInPartita.size());
        InviaMessaggioChat("Server", messaggioChat);

    }


    private void IniziaPartita() throws InterruptedException, IOException {
        sleep(5000);
        partitaAvviata = true;
        Main.rimuoviPartita(this);
        for(gestoreClient Giocatore: utentiInPartita)
        {
            utentiIniziali.add(Giocatore);
        }
        giocaPartita();
        //String msg = "invio-categorie: " + getCategorie();
        //System.out.println(msg);
        //messaggioBroadcast(msg);

    }

    private void giocaPartita() throws IOException, InterruptedException {
        ArrayList<TurnoEdit> turni = new ArrayList<>();
        while(turniGiocati < numero_turni)
        {
            if(stopPartita == true)
            {
                System.out.println("DEBUG: partita interrotta");
                break;
            }
            System.out.println("ho creato turno");
            TurnoEdit newTurno = new TurnoEdit(this, utentiIniziali, categorie, durataTurno, 20);
            this.turnoCorrente = newTurno;
            newTurno.iniziaTurno();
            turniGiocati = turniGiocati + 1;
        }
        System.out.println("DEBUG: la partita è terminata");
        finePartita();

    }


    private void finePartita() throws IOException {
        impedisciDisconnessione = true;
        List<Giocatore> giocatori = new ArrayList<>();
        String payload = "risultati-partita:";
        int punteggioMax = -1;
        Giocatore vincitore = null;


        for (gestoreClient utente : utentiIniziali) {
            //giocatori.add(utente.getGiocatore());
            System.out.println("DEBUG: "  + utente.getGiocatore().getUsername() + utente.getGiocatore().getPunteggioPartita());

            if(utente.getGiocatore().getPunteggioPartita() > punteggioMax)
            {
                System.out.println("sono qui per test6");
                punteggioMax = utente.getGiocatore().getPunteggioPartita();
                System.out.println("sono qui per test");
                vincitore = utente.getGiocatore();
                System.out.println("sono qui per test0");
            }

            System.out.println("sono qui per test1");
            utente.getGiocatore().updatePunteggioGlobale();
            System.out.println("sono qui per test2");
            utente.getGiocatore().addPartitaGiocata();
            System.out.println("sono qui per test3");
            payload = payload + utente.getGiocatore().getUsername() + ":" + utente.getGiocatore().getPunteggioPartita() + ",";
            System.out.println("sono qui per test4");
        }

        //Giocatore vincitore = Collections.max(giocatori, Comparator.comparingInt(Giocatore::getPunteggioPartita));
        System.out.println("Il vincitore è " + vincitore.getUsername());
        vincitore.addVittoria();
        messaggioBroadcast("risultatovincitore:" + vincitore.getUsername());
        messaggioBroadcast(payload);


        for (gestoreClient utenti : utentiInPartita) {
            utenti.getGiocatore().updateRango();
            utenti.getGiocatore().updateStats(utenti.getDatabase());
        }
        System.out.println("DEBUG: fine partite");
        messaggioBroadcast("fine-partita");
        impedisciDisconnessione = false;

    }







    private int getNumeroGiocatori()
    {
        return utentiInPartita.size();
    }




    public void messaggioBroadcast(String messaggio) throws IOException {
        for(gestoreClient i: utentiInPartita)
        {
            i.inviaAlClient(messaggio);
        }
    }



    public void gestoreSegnalazioni(gestoreClient client, String client_input) throws IOException {
        if(allowVotazione == false)
        {
            client.clientRiceviMessaggioChat("Server", "le segnalazioni non sono abilitate");
            return;
        }

        System.out.println("DEBUG: comando segn" + client_input);
        String segnala = "/segnala";
            int startIndex = client_input.indexOf(segnala) + segnala.length();
            String uscat = client_input.substring(startIndex).trim();
            System.out.println("non formattato:" + uscat);


            String[] data = uscat.split(" ");
            String username;
            String categoria;
            username = data[0];
            if (data.length > 1) {
                categoria = data[1];
                System.out.println("formattato:" + username + categoria);


                System.out.println("DEBUG: " + client.getGiocatore().getUsername() + " sta segnalando " + username + " nella categoria " + categoria);
                if (usernameGiocatori.contains(username)) {

                    int indiceGiocatore = usernameGiocatori.indexOf(username);
                    if (categorie.contains(categoria)) {

                        int indiceCategoria = categorie.indexOf(categoria);
                        client.clientRiceviMessaggioChat("Server", "hai segnalato" + username + " nella categoria " + categoria);
                        InviaMessaggioChat("Server", client.getGiocatore().getUsername() + " ha segnalato il giocatore " + username + " nella categoria " + categoria);
                        turnoCorrente.segnalaParola(utentiInPartita.get(indiceGiocatore), indiceCategoria);

                        //DA CAMBIARE CON utentiIniziali
                    } else {
                        client.clientRiceviMessaggioChat("Server", "La categoria specificata non è stata trovata, riprova!");
                    }
                } else {
                    client.clientRiceviMessaggioChat("Server", "Non ho trovato nessun giocatore di nome " + username);
                }
            }
        }

    public void gestiscidisconnessioneclient(gestoreClient client) throws IOException {
        while(impedisciDisconnessione)
        {

        }

        utentiInPartita.remove(client);
        clientDisconnessi.add(client);
        if(getNumeroGiocatori() < 1) {
            System.out.println("DEBUG: Nella partita ci sono 0 giocatori. Eliminazione partita");
            stopPartita = true;
            Main.rimuoviPartita(this);
            stop();
        }
        System.out.println("DEBUG: un Giocatore si è disconnesso");


        Giocatore = client.getGiocatore();
        String nome_Giocatore = Giocatore.getUsername();
        usernameGiocatori.remove(nome_Giocatore);
        String messaggioChat = "il giocatore " + nome_Giocatore + " si è connesso alla lobby!";
        messaggio = "rimuovi Giocatore in lobby:" + usernameGiocatori.toString();
        System.out.println(messaggio);
        messaggioBroadcast(messaggio);

        if(client == this.host)
        {
            if(utentiInPartita.isEmpty())
            {
                return;
            }
            System.out.println("DEBUG: l'Giocatore disconnesso era l'host. Il nuovo host è adesso " + utentiInPartita.get(0).getGiocatore().getUsername());
            this.host = utentiInPartita.get(0);
            host.clientRiceviMessaggioChat("Server", "sei il nuovo host!");
        }
    }

    public ArrayList<gestoreClient> getUtentiDisconnessi()
    {
        return clientDisconnessi;
    }

    public void chat(gestoreClient client, String client_input)
    {

        Giocatore newGiocatore = client.getGiocatore();
        String nomeGiocatore = newGiocatore.getUsername();
        InviaMessaggioChat(nomeGiocatore, client_input, client);
    }

    public void InviaMessaggioChat(String nomeGiocatore, String message)
    {
        System.out.println("DEBUG metodo InviaMessaggioChat di Partita: sto inviando un messaggio in chat" + message);
        for(gestoreClient i: utentiInPartita)
        {
            i.clientRiceviMessaggioChat(nomeGiocatore, message);
        }
    }

    private void InviaMessaggioChat(String nomeGiocatore, String message, gestoreClient clientEscluso) {
        System.out.println("DEBUG metodo InviaMessaggioChat di Partita: sto inviando un messaggio in chat" + message);
        for (gestoreClient i : utentiInPartita) {
            if (i != clientEscluso) {
                i.clientRiceviMessaggioChat(nomeGiocatore, message);
            }
        }
    }


}


