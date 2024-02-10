import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class gestoreClient extends Thread {


    private Socket clientSocket = null;
    private Database database;
    private Giocatore giocatore = null;
    private String username = null;

    Partita partita = null;

    String custombuffer = "";

    volatile boolean inPartita = false;
    volatile boolean chatFlag = true;

    String client_input;

    PrintWriter pw;
    BufferedReader br;

    ObjectOutputStream outputStream;


    public gestoreClient(Socket client, Database database) throws IOException {
        clientSocket = client;
        this.database = database;
        this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        pw = new PrintWriter(clientSocket.getOutputStream(), true);
        br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }


    public void run() {

        try {
            connessioneClient();
        } catch (IOException e) {
            System.out.println("Utente disconnesso");
            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Socket:" + clientSocket.isConnected() + clientSocket.getLocalAddress());


        try {
            gestoreComandiClient();
        } catch (IOException e) {
            System.out.println("Utente disconnesso");
            try {
                gestisciDisconnessione();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void gestisciDisconnessione() throws IOException {
        Main.removeUtenteOnline(this.giocatore);
        if(this.partita!=null)
        {
            partita.gestiscidisconnessioneclient(this);
        }

    }


    public void stopChat(boolean value)
    {
        chatFlag = !value;
    }

    private void gestoreComandiClient() throws IOException, ClassNotFoundException, InterruptedException, SQLException {


        String comando; //Stringa inviata dal client per interagire con il suo thread sul server


        while (true) {
            comando = br.readLine();
            System.out.println("DEBUG, ho ricevuto il comando " + comando);

            if (comando != null) {
                if (comando.equals("1")) {
                    //gestoreMenuPartite();
                    System.out.println("sono ritornato da menu");
                } else if (comando.equals("richiesta lobby")) {
                    inviaPartiteDisponibili();
                    System.out.println("sono ritornato da inviaPartite");
                } else if (comando.equals("entra lobby")) {
                    System.out.println("ho chiamato inserisciInLobby");
                    inserisciInLobby();
                    System.out.println("sono ritornato da insLobby");
                } else if (comando.equals("invia messaggio chat")) {
                    clientInviaMessaggioChat();
                    System.out.println("sono ritornato da chat");
                } else if (comando.equals("crea-partita")) {
                    creaPartita();
                    System.out.println("sono ritornato da crea partita");
                } else if (comando.equals("invio comando")) {
                    clientInviaComandoServer();
                    System.out.println("sono ritornato da InviaComando");
                } else if (comando.equals("richiesta-classifica-generale")) {
                    try {
                        inviaClassificaTop100();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (comando.equals("richiesta-stats")) {
                    System.out.println("son qui!");
                    inviaStats();
                } else if (comando.equals("!!!client disconnesso da partita")) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(() -> {
                        try {
                            clientuscitapartita();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    this.custombuffer = comando;
                }
            }
        }
    }


    Database getDatabase()
    {
        return database;
    }


    private void inviaStats() throws IOException {

        pw.println("ti sto inviando la classifica");
        System.out.println("invia Stats invocato");
        String payload = giocatore.getUsername()+giocatore.getRango()+giocatore.getPunteggioGlobale()+giocatore.getPartiteVinte()+giocatore.getPartitegiocate();
        System.out.println("payload: " + payload);
        pw.println(payload);

        System.out.println("DEBUG: ho terminato");



    }




    private void inviaClassificaTop100() throws SQLException, IOException {
        ArrayList<Utente> statsUtenti = database.getStatistichetop100();
        pw.println("ti sto inviando la classifica");
        for(Utente stats: statsUtenti)
        {
            String payload = stats.getUsername()+stats.getRango()+stats.getPunteggioGlobale()+stats.getPartiteVinte()+stats.getPartitegiocate();
            pw.println(payload);
            System.out.println(payload);
            pw.println("ti ho inviato 1 stats");
            while(!client_input.equals("ok continua"))
            {
                client_input = br.readLine();
                System.out.println(client_input);
            }
        }
        while(!client_input.equals("ok continua")) {
        client_input = br.readLine();
            System.out.println("secondo while " + client_input);
        }
        pw.println("finito");
        System.out.println("ho terminato");
    }

    private void clientInviaComandoServer() throws IOException, InterruptedException {
        System.out.println("inviaComandoServer invocato");
        client_input = br.readLine();
        this.partita.gestoreComandiChat(this, client_input);
    }

    private void clientuscitapartita() throws IOException {
        if(this.partita!=null)
        {
            partita.gestiscidisconnessioneclient(this);
            this.partita = null;
        }
    }
    private void inserisciInLobby() throws IOException {
        System.out.println("DEBUG: metodo inserisciInLobby()");
        ArrayList<Partita> partiteDisponibili = Main.getPartiteDisponibili();
        pw.println("invia nome partita");

        client_input = br.readLine();
        System.out.println("DEBUG, nome partita richiesta:" + client_input);

        for(Partita partita: partiteDisponibili)
        {
            System.out.println("Sto confrontando la partita: " + partita.getNomePartita());
            if(partita.getNomePartita().equals(client_input))
            {
                pw.println("aggiunto");
                this.partita = partita;
                inPartita = true;
                partita.aggiungiInPartita(this);
                System.out.println("DEBUG: ho aggiunto un client in una partita");
                //pw.println("aggiunto");
                return;
            }
        }
        System.out.println("DEBUG: partita non trovata");
        pw.println("partita non trovata");
        return;
    }

    public Giocatore getGiocatore() {
        return giocatore;
    }
    private void inviaPartiteDisponibili() throws IOException {
        System.out.println("DEBUG: sto eseguendo il metodo InviaPariteDisponibili");

        ArrayList<Partita> partiteDisponibili = Main.getPartiteDisponibili();
        ArrayList<ArrayList<String>> lobbyDisponibili = new ArrayList<>();


        if (!partiteDisponibili.isEmpty()) {
            System.out.println("DEBUG: sono disponibili partite da joinare");
            pw.println("partite disponibili");
            for (Partita partita : partiteDisponibili) {
                ArrayList<String> lobby = new ArrayList<>();
                lobby.add(partita.getNomePartita());
                lobby.add(String.valueOf(partita.getNumGiocatori()));
                lobbyDisponibili.add(lobby);
            }
            while(!br.readLine().equals("invia partite")) {

            }
            System.out.println(lobbyDisponibili.toString());
            pw.println(lobbyDisponibili.toString());
            return;
        }
        else
            {
                System.out.println("DEBUG: non sono disponibili partite");
                pw.println("partite non disponibili");
                return;
            }
    }


    private void creaPartita() throws IOException, ClassNotFoundException {

            pw.println("manda i dettagli partita");
            String payload = br.readLine();

        String primiQuattroTermini = payload.substring(0, payload.indexOf("[")).trim();
        String[] terminiSeparati = primiQuattroTermini.split(",");

        // Stampa i primi quattro termini
        for (String termine : terminiSeparati) {
            System.out.println(termine.trim());
        }

        // Estrai la stringa tra parentesi quadre
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(payload);

        String stringaTraParentesi = "";
        if (matcher.find()) {
            stringaTraParentesi = "["+matcher.group(1)+"]";
            System.out.println(stringaTraParentesi);
        }
        else
        {
            stringaTraParentesi = "";
        }
        Partita newPartita = new Partita(terminiSeparati[0].trim(),this,terminiSeparati[1].trim(),terminiSeparati[2].trim(), terminiSeparati[3].trim(), stringaTraParentesi);
        this.partita = newPartita;
        Main.aggiungiPartita(newPartita);
        newPartita.aggiungiInPartita(this);

    }


    public void gestioneFinePartita()
    {
        partita.interrupt();
        Main.rimuoviPartita(partita);
        this.partita = null;
        inPartita = false;
    }

    public void inviaAlClient(String string)
    {
        System.out.println("DEBUG: inviaAlClient è stato chiamato");
        pw.println(string);
    }

    public void InviaAlClientListaUtenti()
    {
    }


    public String riceviDalClient() throws IOException {

        pw.println("richiesto-input");

        client_input = br.readLine();
        return client_input;
    }

    public void clientRiceviMessaggioChat(String playerUsername,     String chatMessage)
    {
        pw.println("messaggioChat,player: " + playerUsername + "," + chatMessage);
    }





    public String riceviDalClientParoleNB() throws IOException {
        System.out.println("DEBUG: sto cercando di prelevare dal buffer");
        if(!custombuffer.isEmpty())
        {
            System.out.println("DEBUG: server ha prelevato dal buffer correttamente");
            return custombuffer;
        }
        System.out.println("DEBUG: il buffer è vuoto");
        return null;
    }

    public void clientInviaMessaggioChat() throws IOException {
       client_input = br.readLine();
       partita.chat(this, client_input);
    }



    private void connessioneClient() throws IOException, SQLException {

        String username;
        String password;
        Utente newUtente = null;

        while (true)
        {

            client_input = br.readLine();
            if (client_input.equals("registrami"))
            {
                Giocatore newGiocatore = registrazione();
                if (newGiocatore != null)
                {
                    System.out.println("registrazione effettuata");
                    this.giocatore = newGiocatore;
                    return;
                }
            } else if (client_input.equals("loggami"))
            {
                Giocatore newGiocatore = login();
                if (newGiocatore != null)
                {
                    System.out.println("login effettuato");
                    this.giocatore = newGiocatore;
                    return;
                }
            }

        }
    }



    public Giocatore login() throws IOException, SQLException {
        String username;
        String password;
        pw.println("Inviami username");
        username = br.readLine();

        if(Main.isOnline(username))
        {
            pw.println("utente già connesso");
            return null;
        }
        else
        {
            pw.println("Inviami password");
            password = br.readLine();
            boolean login = database.dbLogin(username, password);

            if(login == true)
            {
                pw.println("login-ok");
                Giocatore giocatore = database.getUtenteDB(username);
                Main.addUtenteOnline(giocatore);
                return giocatore;
            }
            else if(login == false)
            {
                pw.println("login-fallito");
                return null;
            }

        }
        return null;
    }


    public Giocatore registrazione() throws IOException, SQLException {
        String username;
        String password;
        pw.println("Inviami username");
        username = br.readLine();

        boolean registrato = database.dbTrovaUtente(username);
        if (registrato == true) {
            pw.println("utente già registrato");
            return null;
        } else
        {
            pw.println("inviami password");
            password = br.readLine();
            database.dbAggiungiUtente(username, password);
            Giocatore newGiocatore = database.getUtenteDB(username);
            pw.println("registrazione-ok");
            Main.addUtenteOnline(newGiocatore);
            return newGiocatore;
        }

    }




    public void gestioneFinePartitaNormale()
    {
        this.partita=null;
        inPartita=false;
    }





}
