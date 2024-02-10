import java.io.Serializable;

public class Lobby implements Serializable {
    String nome;
    int numgiocatori;

    public Lobby(String nome, int numgiocatori) {
        this.nome = nome;
        this.numgiocatori = numgiocatori;
    }
    public String getNome()
    {
        return nome;
    }
}
