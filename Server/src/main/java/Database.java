import java.sql.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Database {
    private  String DB_URL = "";
    private  String USERNAME = "";
    private  String PASSWORD = "";

    private  Connection connection;

    public void connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connessione al database stabilita.");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC non trovato.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Errore durante la connessione al database.");
            e.printStackTrace();
        }
    }

    public void setUrl(String DB_URL)
    {
        this.DB_URL = DB_URL;

    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;

    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;

    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione al database chiusa.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la chiusura della connessione al database.");
            e.printStackTrace();
        }
    }

    // Altri metodi per l'esecuzione delle query, gestione delle transazioni, ecc.

    public Connection getConnection() {
        return connection;
    }


    public ResultSet caricaUtenti()
    {
        ResultSet resultSet = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT username, password, punteggio, rango FROM utenti");
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query.");
            e.printStackTrace();
        }
        return resultSet;
    }

    public boolean dbTrovaUtente(String username) throws SQLException {
        boolean esiste = false;
        ResultSet resultSet = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT username FROM utenti WHERE username = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if(resultSet.next() == true)
            {
                esiste = true;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query.");
            e.printStackTrace();
        }

        return esiste;
    }

    public boolean dbLogin(String username, String password) throws SQLException {
        boolean login = false;
        ResultSet resultSet = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT username FROM utenti WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();
            if(resultSet.next() == true)
            {
                login = true;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query.");
            e.printStackTrace();
        }

        return login;
    }


    public Giocatore getUtenteDB(String username)
    {
        ResultSet resultSet = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT punteggio, partitegiocate, partitevinte, rango, bonus FROM utenti WHERE username = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Giocatore newGiocatore = new Giocatore(username, resultSet.getString("rango"), resultSet.getInt("punteggio"), resultSet.getInt("partitevinte"), resultSet.getInt("partitegiocate"));
                System.out.println("DATABASE, DEBUG: ho creato istanziato un nuovo giocatore");
                return newGiocatore;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query.");
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<Utente> getStatistichetop100() throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = connection.prepareStatement("SELECT username, punteggio, partitevinte, rango, partitegiocate FROM utenti ORDER BY punteggio DESC LIMIT 50");
        resultSet = statement.executeQuery();
        ArrayList<Utente> statsUtenti = new ArrayList<>();

        System.out.println("DEBUG database: sto eseguendo la query per le statistiche");

        while(resultSet.next()){
            System.out.println("sto caricando " + resultSet.getString("username"));
            Utente statsUtente = new Utente(resultSet.getString("username"), resultSet.getString("rango"), resultSet.getInt("punteggio"), resultSet.getInt("partitevinte"), resultSet.getInt("partitegiocate"));
            statsUtenti.add(statsUtente);
        }
        return statsUtenti;
    }




    public boolean dbAggiungiUtente(String username, String password) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO utenti (username, password, punteggio, rango, partitegiocate, ban, bonus, partitevinte) VALUES (?, ?, 0, '1', 0, 0, 0, 0)");
            statement.setString(1, username);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utente aggiunto con successo.");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query.");
            e.printStackTrace();
        }

        return false;
    }


    public void dbAggiornaStatsUtente(String username, int punteggioGlobale, int partitegiocate, int partitevinte, String rango)
    {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE utenti SET punteggio = ?, rango = ?, partitegiocate = ?, partitevinte = ? WHERE username = ?");
            statement.setInt(1, punteggioGlobale);
            statement.setString(2, rango);
            statement.setInt(3, partitegiocate);
            statement.setInt(4, partitevinte);
            statement.setString(5, username);
            int rowsAffected = statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Errore durante l'esecuzione della query.");
            e.printStackTrace();
        }

    }
}