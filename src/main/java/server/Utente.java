package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Utente {
    public final int ID;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String telefono;
    private boolean connected;
    private boolean admin;

    public Utente(String nome, String cognome, String email, String password, String telefono){
        int nextCodice = Integer.MAX_VALUE;
        BufferedWriter writer;
        BufferedReader reader;

        try{
            reader = new BufferedReader(new FileReader("src\\nextCodici.txt"));
            int codiceAsta = Integer.parseInt(reader.readLine());
            int codiceLotto = Integer.parseInt(reader.readLine());
            int codiceOggetto = Integer.parseInt(reader.readLine());
            nextCodice = Integer.parseInt(reader.readLine());
            reader.close();
            writer = new BufferedWriter(new FileWriter("src\\nextCodici.txt"));
            writer.write(Integer.toString(codiceAsta) + "\n");
            writer.append(Integer.toString(codiceLotto) + "\n");
            writer.append(Integer.toString(codiceOggetto) + "\n");
            writer.append(Integer.toString(nextCodice + 1));
            writer.close();
        }catch(IOException ignore){
        }

        ID = nextCodice;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        connected = false;
        admin = false;
    }

    public Utente(String ID, String nome, String cognome, String email, String password, String telefono, boolean connected, boolean admin){
        this.ID = Integer.parseInt(ID);
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.connected = connected;
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public int getID() {
        return ID;
    }

    public String getNome(){
        return nome;
    }

    public String getCognome(){
        return cognome;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect(){
        connected = true;
    }

    public void disconnect(){
        connected = false;
    }
    @Override
    public String toString() {
        return ID + "|" + nome + "|" + cognome + "|" + email + "|" + password + "|" + telefono + "|" + (isConnected()? "1":"0") + "|" + (admin? "1":"0");
    }

}
