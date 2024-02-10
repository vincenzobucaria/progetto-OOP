package com.example.nomicosecitta;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;

import static java.lang.Thread.sleep;


public class Client {

    //Attributi della classe Client

    private Socket socket;

    PrintWriter pw;
    BufferedReader br;
    Scanner input = new Scanner(System.in);

    String server_input;

    String client_input;

    String risultatiPartita;

    volatile private boolean inPartita;

    volatile private boolean inLobby = false;


    ControllerFineTurno controllerFineTurno;

    int numTurno = 0;

    private String usernameVincitore;


    private String classificapartita;


    private ArrayList<String> categoriePartita;

    String input_test;

    ObjectInputStream inputStream;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Client(String serverip, int porta) throws IOException {

        this.socket = new Socket(serverip, porta);
        pw = new PrintWriter(socket.getOutputStream(), true);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        inputStream = new ObjectInputStream(socket.getInputStream());



        System.out.println("Connessione al server effettuata");
    }

    private String letteraTurno;

    private String durataTurno;

    private String risultatiTurno;

    public int login(String username, String password) throws IOException {


        pw.println("loggami");
        server_input = br.readLine();
        if (server_input.equals("Inviami username")) {
            pw.println(username);
            server_input = br.readLine();
            if (server_input.equals("utente già connesso")) {
                return 2;
            } else if (server_input.equals("Inviami password")) {
                pw.println(password);
                server_input = br.readLine();
                if (server_input.equals("login-ok")) {
                    return 1;
                } else if (server_input.equals("login-fallito")) {
                    return 0;
                }
            }
        }
        return -1;
    }

    public int registrati(String username, String password) throws IOException {
        pw.println("registrami");
        server_input = br.readLine();
        if (server_input.equals("Inviami username")) {
            pw.println(username);
            server_input = br.readLine();
            if (server_input.equals("utente già registrato")) {
                return 0;
            } else if (server_input.equals("inviami password")) {
                pw.println(password);
                server_input = br.readLine();
                if (server_input.equals("registrazione-ok")) {
                    return 1;
                }
            }
        }
        return -1;
    }


    public String getVincitore()
    {
        return usernameVincitore;
    }

    public String getRisultatiPartita()
    {
        return risultatiPartita;
    }



    public boolean entraInLobby(String nomePartita) throws IOException {
        pw.println("entra lobby");
        System.out.println("DEBUG: metodo entraInLobby");
        while (!server_input.equals("invia nome partita")) {
            server_input = br.readLine();
            System.out.println(server_input);
        }
        pw.println(nomePartita);

        while (!server_input.equals("aggiunto")) {
            server_input = br.readLine();
            System.out.println(server_input);
            if (server_input.equals("partita non trovata")) {
                return false;
            }
        }
        inLobby = true;
        return true;
    }


    public String riceviPartite() throws IOException, ClassNotFoundException {
        pw.println("richiesta lobby");
        while (!server_input.equals("partite disponibili")) {
            server_input = br.readLine();
            if (server_input.equals("partite non disponibili")) {
                System.out.println("DEBUG: non ci sono partite disponibili");
                return null;
            }
        }

        System.out.println(server_input);
        if (server_input.equals("partite disponibili")) {
            System.out.println(server_input);
            System.out.println("DEBUG: sto ricevendo partite dal server");
            pw.println("invia partite");
            server_input = br.readLine();
            while (!server_input.startsWith("[")) {
                server_input = br.readLine();
            }
            String lobbyDisponibili = server_input;
            System.out.println("DEBUG: ho ricevuto correttamente le lobby disponibili" + lobbyDisponibili);
            return lobbyDisponibili;
        } else if (server_input.equals("partite non disponibili")) {
            System.out.println("DEBUG: non ci sono partite disponibili");
            return null;
        }
        return null;
    }

    public void gestoreMessaggiRicevutiLobby(ControllerLobby controllerLobby) throws IOException, InterruptedException {
        System.out.println("DEBUG: sto eseguendo il metodo gestoreMessaggiRicevutiLobby");
        while (true) {
            while (!br.ready()) {
                if(inLobby == false)
                {
                    System.out.println("sono uscito dal metodo gestoreMessaggiRicevutiLobby");
                    return;
                }
            }
            server_input = br.readLine();
            System.out.println("DEBUG: comando ricevuto " + server_input);

            if (server_input.startsWith("messaggioChat,player: ")) {
                System.out.println("DEBUG: ho ricevuto un messaggioChat " + server_input);
                String messagePart = server_input.replace("messaggioChat,player: ", "");
                String[] parts = messagePart.split(",");
                String username = parts[0].trim();
                String message = parts[1].trim();
                System.out.println("Username: " + username);
                if (username.equals("Server"))
                {

                        controllerLobby.riceviChatServer(message);
                }
                else
                {
                    System.out.println("Message: " + message);

                        controllerLobby.riceviChat(username, message);


                }

            }
            System.out.println("DEBUG gestoreMessaggioRicevutiLobby: ho ricevuto il comando" + client_input);

            if (server_input.startsWith("nuovo Giocatore in lobby:")) {
                String messagePart = server_input.replace("nuovo Giocatore in lobby:", "");
                System.out.println("DEBUG: nuovo Giocatore in lobby: " + messagePart);
                controllerLobby.aggiungiGiocatorePartita(messagePart);

            }
            if(server_input.startsWith("rimuovi Giocatore in lobby:"))
            {
                String messagePart = server_input.replace("rimuovi Giocatore in lobby:", "");
                System.out.println("DEBUG:  Giocatore rimosso dalla lobby: " + messagePart);
                controllerLobby.aggiungiGiocatorePartita(messagePart);

            }


            if(server_input.equals("inizio-turno")) {
                inPartita = true;
                System.out.println("DEBUG: è iniziato un turno");
                this.numTurno = numTurno + 1;
                controllerLobby.switchtoTurno();

                int tempoRestante = Integer.parseInt(durataTurno);


            }
            if(server_input.startsWith("specifiche: "))
            {
                server_input = server_input.replace("specifiche: ", "");
                setSpecifichePartita(server_input);
            }
            if(server_input.startsWith("risultati-turno:"))
            {
                server_input = server_input.replace("risultati-turno:", "");
                setRisultatiTurno(server_input);
            }
            if(server_input.equals("fine-turno"))
            {
                controllerLobby.switchToFineTurno();
            }
            if(server_input.equals("aggiorna-risultati-turno"))
            {
                controllerLobby.aggiornaRisultatiTurno();
            }
            if(server_input.startsWith("risultati-partita:"))
            {
                server_input = server_input.replace("risultati-partita:", "");
                setRisultatiPartita(server_input);
            }
            if(server_input.startsWith("risultatovincitore:"))
            {
                server_input = server_input.replace("risultatovincitore:", "");
                setUsernameVincitore(server_input);
            }
            if(server_input.equals("fine-partita"))
            {
                controllerLobby.switchtofinePartita();
            }

        }


    }




    public void escidallalobby()
    {
        inPartita = false;
        if(categoriePartita != null) {
            categoriePartita.clear();
        }
        numTurno = 0;
        durataTurno = "";
        inLobby = false;
        pw.println("!!!client disconnesso da partita");
    }

    private void setUsernameVincitore(String vincitore)
    {
        this.usernameVincitore = vincitore;
        System.out.println("DEBUG: " + vincitore);
    }

    private void setRisultatiPartita(String message)
    {
        this.risultatiPartita = message;
        System.out.println("DEBUG: " + risultatiPartita);
    }




    private void setRisultatiTurno(String server_input)
    {
        this.risultatiTurno = server_input;
        //System.out.println(this.risultatiTurno);
    }


    public String getRisultatiTurno()
    {
        return risultatiTurno;
    }

    private void setSpecifichePartita(String specifiche)
    {
        System.out.println("metodo setSpeicifhe invocato");
        int startIndex = specifiche.indexOf("[");
        int endIndex = specifiche.indexOf("]");
        String categorieSubstring = specifiche.substring(startIndex + 1, endIndex);

        // Trova il carattere dopo "lettera:"
        int letteraIndex = specifiche.indexOf("lettera:") + 8;
        String letteraChar = String.valueOf(specifiche.charAt(letteraIndex));

        // Trova la sottostringa dopo "durata:"
        int durataIndex = specifiche.indexOf("durata:") + 7;
        String durataSubstring = specifiche.substring(durataIndex);

        System.out.println(categorieSubstring);

        String[] categorie = categorieSubstring.split(", ");

        categoriePartita = new ArrayList<>();

        for(String categoria: categorie)
        {
            categoriePartita.add(categoria);
            //System.out.println("ho aggiunto " + categoria);
        }

        this.durataTurno = durataSubstring;
        this.letteraTurno = letteraChar;

    }

    public ArrayList<String> getCategoriePartita()
    {
        return categoriePartita;
    }

    public String getDurataTurno()
    {
        return durataTurno;
    }

    public String getLetteraTurno()
    {
        return letteraTurno;
    }

    public int getNumTurno()
    {
        return numTurno;
    }




    public ArrayList<Utente> richiediStatisticheTop100() throws IOException, ClassNotFoundException {
        pw.println("richiesta-classifica-generale");
        System.out.println("metodo richiedi eseguito");

        while(!server_input.equals("ti sto inviando la classifica"))
        {
            server_input = br.readLine();
            System.out.println("DEBUG:" + server_input);
        }

        ArrayList<Utente> stats = new ArrayList<>();

        while ((!server_input.equals("finito")))
        {
            // Aggiungi l'attributo all'ArrayList
            server_input = br.readLine();
            if(server_input.equals("finito"))
            {
                System.out.println("ho terminato");
                return stats;
            }
            System.out.println("a:" + server_input);
            String[] campi = server_input.split(",");
            Utente utente = new Utente(campi[0], campi[1], Integer.parseInt(campi[2]), Integer.parseInt(campi[3]), Integer.parseInt(campi[4]));
            stats.add(utente);
            System.out.println(server_input);
            while(!server_input.equals("ti ho inviato 1 stats"))
            {
                server_input = br.readLine();
                System.out.println(server_input);
            }
            pw.println("ok continua");
        }
        System.out.println("ho finito");
        return stats;

    }
    public Utente richiedistats() throws IOException, ClassNotFoundException {
        System.out.println("DEBUG: metodo richiedistats");
        pw.println("richiesta-stats");
        server_input = br.readLine();
        System.out.println("DDDD:" + server_input);
        server_input = br.readLine();
        String[] attributi = server_input.split(",");
        System.out.println("Attributi" + attributi);

        Utente utente = new Utente(attributi[0], attributi[1], Integer.parseInt(attributi[2]),Integer.parseInt(attributi[3]), Integer.parseInt(attributi[4]));
        System.out.println("DEBUG: " + utente.getUsername() + utente.getRango());
        return utente;
    }




    public void inviaParole(String parole)
    {
        System.out.println("DEBUG: il metodo inviaParola è stato invocato: " + parole);
        pw.println(parole);
    }


    public void inviaMessaggioChat(String message) {
        pw.println("invia messaggio chat");
        pw.println(message);
    }

    public void creaPartita(String nomePartita, int ValoreTurni, int ValoreGiocatori, int tempo, ArrayList<String> categorie) throws IOException {
        pw.println("crea-partita");
        while (!server_input.equals("manda i dettagli partita")) {
            server_input = br.readLine();
            System.out.println(server_input);
        }

        String payload = nomePartita + "," + String.valueOf(ValoreTurni) + "," + String.valueOf(ValoreGiocatori) + "," + String.valueOf(tempo) + "," + categorie.toString();
        inLobby = true;
        pw.println(payload);
    }

    public void inviaComandoAlServer(String comando) {
        pw.println("invio comando");
        pw.println(comando);
    }
}














