package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    public final static String fileUtenti = "src\\utenti";
    public static GestoreAste gestoreAste = new GestoreAste();
    public final static int porta = 3000;
    public static void main(String[] args) {
        gestoreAste.deserializza();
        resettaUtenti();
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            while(true){
                new ServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resettaUtenti(){
        try {
            ArrayList<Utente> listaUtenti = leggiUtenti();
            for(int index = 0; index < listaUtenti.size(); index++){
                Utente u = listaUtenti.get(index);
                u.disconnect();
                listaUtenti.set(index,u);
            }
            salvaUtenti(listaUtenti);
        } catch (IOException ignore) {
        }
    }

    private static ArrayList<Utente> leggiUtenti() throws IOException{
        BufferedReader fileReader;
        ArrayList<String[]> listaStringhe = new ArrayList<>();
        ArrayList<Utente> listaUtenti = new ArrayList<>();
        try{
            fileReader = new BufferedReader(new FileReader(fileUtenti + ".txt"));
            while(fileReader.ready()){
                String[] datiLetti = fileReader.readLine().split("\\|");
                listaStringhe.add(datiLetti);
            }
            fileReader.close();
            for(String[] s: listaStringhe){
                listaUtenti.add(new Utente(s[0], s[1], s[2], s[3], s[4], s[5], (s[6].equals("1")), (s[7].equals("1"))));
            }
            return listaUtenti;
        }catch(FileNotFoundException e){
            File file = new File(fileUtenti + ".txt");
            file.createNewFile();
            return listaUtenti;
        }
    }

    private static void salvaUtenti(ArrayList<Utente> lista) throws IOException{
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(Server.fileUtenti + ".txt"));
        for(Utente u: lista){
            fileWriter.write(u.toString());
            fileWriter.newLine();
        }
        fileWriter.close();
    }
}
