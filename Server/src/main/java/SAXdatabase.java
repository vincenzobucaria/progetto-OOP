
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXdatabase extends DefaultHandler {

    private boolean inUrl = false;
    private boolean inUsername = false;
    private boolean inPassword = false;

    private String URL, username, password;

    private Database databaseManager;


    public SAXdatabase(Database databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals("url"))
        {
            inUrl = true;
        }
        if(qName.equals("username"))
        {
            inUsername = true;
        }
        if(qName.equals("password"))
        {
            inPassword = true;
        }

    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(inUrl)
        {
            URL = new String(ch, start, length);
            databaseManager.setUrl(URL);

        }
        if(inUsername)
        {
            username = new String(ch, start, length);
            databaseManager.setUSERNAME(username);
        }
        if(inPassword)
        {
            password = new String(ch, start, length);
            databaseManager.setPASSWORD(password);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if(qName.equals("url"))
        {
            inUrl = false;
        }
        if(qName.equals("username"))
        {
            inUsername = false;
        }
        if(qName.equals("password"))
        {
            inPassword = false;
        }


    }

    public String getURL() {
        return URL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
