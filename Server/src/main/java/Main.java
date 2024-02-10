import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.net.Socket;







public class Main {

    static ArrayList<Utente> utentiOnline = new ArrayList<>();

    public static void addUtenteOnline(Utente utente) {
        utentiOnline.add(utente);
    }

    public static void removeUtenteOnline(Utente utente)
    {
        utentiOnline.remove(utente);
    }

    public static boolean isOnline(String username) {
        for (Utente i : utentiOnline) {
            if (i.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


    public static ArrayList<Partita> listaPartiteDisponibili = new ArrayList<>();

    public static ArrayList<Partita> getPartiteDisponibili()
    {
        return listaPartiteDisponibili;
    }

    public static void aggiungiPartita(Partita partita)
    {
        listaPartiteDisponibili.add(partita);
    }


    public static void rimuoviPartita(Partita partita)
    {
        listaPartiteDisponibili.remove(partita);
    }







    public static void main(String[] args) throws IOException, SQLException, ParserConfigurationException, SAXException {


        ServerSocket serverSocket = new ServerSocket(12345);
        Database databaseManager = new Database();


        SAXParserFactory spf = SAXParserFactory.newInstance();


            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            ContentHandler handler = new SAXdatabase(databaseManager);
            xmlReader.setContentHandler(handler);
            System.out.println("so qui");
            xmlReader.parse("settings.xml");
            System.out.println();
            databaseManager.connect();




        //Utente newUtente;
        //ArrayList<String> loggedUsers = new ArrayList<>();
        ArrayList<Utente> utentiOnline = new ArrayList<>();

        gestoreClient newClient;

        //ArrayList<String> categorie = new ArrayList<>();
        //categorie.add("Nomi");
        //categorie.add("Cose");
        //categorie.add("Città");

        //Partita newPartita = new Partita("Partita di test", null, "2", "2", "100", "[NOMI, COSE, CITTA]");
        //aggiungiPartita(newPartita);




            while(true) {

                Socket sockClient = serverSocket.accept();
                newClient = new gestoreClient(sockClient, databaseManager);

                newClient.start();
            }


        }
    }


//newUtente = pippo.connessioneClient(loggedUsers);
               /* if (newUtente != null) {
                    System.out.println("Un utente si è appena loggato!");
                    listaUtenti.add(newUtente);
                    loggedUsers.add(newUtente.getName());
                    listaUtenti.get(index).vediProfilo();
                    index = index+1;
                }
            }

            //listaUtenti.get(0).vediProfilo();
            //List<Utente> utentiList = Utente.caricaUtentiDalDatabase(databaseManager);



            // Utilizza la lista di utenti come desideri



        }
    }



*/



