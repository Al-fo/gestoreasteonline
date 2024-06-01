package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.GestoreAste.Asta;

public class ServerThread extends Thread{
    private Socket socket;

    public ServerThread(Socket socket) throws IOException{
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream())) {
                while(true){
                    System.out.println("Aspetto di leggere");
                    String ricevuto = reader.readLine();
                    System.out.println("ho letto" + ricevuto);
                    String comando = ricevuto.substring(0,ricevuto.indexOf("|"));
                    System.out.println("ho ricavato: " + comando);
                    switchcase: switch(comando){
                        case "-Registra":{
                            String[] dati = new String[5];
                            Arrays.fill(dati, "");
                            int j = 0;
                            for(int i = comando.length() + 1; i < ricevuto.length(); i++){
                                if(ricevuto.charAt(i) != '|'){
                                    dati[j] += ricevuto.charAt(i);
                                }
                                else j++;
                            }
                            String nome = dati[0];
                            String cognome = dati[1];
                            String email = dati[2];
                            String password = dati[3];
                            String tel = dati[4];

                            String[] patterns = {
                                "^([A-Z]*[a-z]+)+$", "^([A-Z]*[a-z]+)+$", 
                                "^[^.\\r\\n\\t\\f @()<>,;:\"]+\\.?[\\w]+\\@([a-z0-9]+-*[a-z0-9]+\\.)+[a-z-*]{2,6}$", 
                                "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9]).{6,})\\S$", 
                                "^([+][0-9]{2} )?[0-9]{10}$"
                            };

                            Pattern inputDesiderato = Pattern.compile("");
                            boolean found = true;
                            Matcher match;
                            for(int i = 0; i < 5; i++){
                                inputDesiderato = Pattern.compile(patterns[i]);
                                match = inputDesiderato.matcher(dati[i]);
                                found = match.matches();
                            }
                            if(!found){
                                writer.writeBytes("[ER]Dati\n");
                                break switchcase;
                            }
                            System.out.println("dati ricevuti");
    
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(Utente u: listaUtenti){
                                if(u.getEmail().equalsIgnoreCase(email)){
                                    writer.writeBytes("[ER]Esistente\n");
                                    break switchcase;
                                }
                            }
                            listaUtenti.add(new Utente(nome, cognome, email, password, tel));
                            writer.writeBytes("[OK]\n");
                            salvaUtenti(listaUtenti);
                            break;
                        }
                        case "-Login":{
                            String[] dati = new String[2];
                            Arrays.fill(dati, "");
                            int j = 0;
                            for(int i = comando.length() + 1; i < ricevuto.length(); i++){
                                if(ricevuto.charAt(i) != '|'){
                                    dati[j] += ricevuto.charAt(i);
                                }
                                else j++;
                            }

                            String email = dati[0];
                            String password = dati[1];

                            System.out.println(email + " " + password);

                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(int i = 0; i < listaUtenti.size(); i++){
                                Utente u = listaUtenti.get(i);
                                if(u.getEmail().equals(email)){
                                    if(u.getPassword().equals(password)){
                                        if(u.isConnected()){
                                            u.disconnect();
                                            writer.writeBytes("[ER]Connected\n");
                                            listaUtenti.set(i,u);
                                            salvaUtenti(listaUtenti);
                                            break switchcase;
                                        }
                                        if(u.isAdmin()){
                                            writer.writeBytes("[AD]" + u.getID() + "\n");
                                            u.connect();
                                            listaUtenti.set(i,u);
                                            salvaUtenti(listaUtenti);
                                        }else{
                                            writer.writeBytes("[OK]" + u.getID() + "\n");
                                            u.connect();
                                            listaUtenti.set(i,u);
                                            salvaUtenti(listaUtenti);
                                        }
                                        break switchcase;
                                    }else{
                                        writer.writeBytes("[ER]Dati\n");
                                        break switchcase;
                                    }
                                }
                            }
                            writer.writeBytes("[ER]Dati\n");
                            break;
                        }
                        case "-Logout":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(int i = 0; i < listaUtenti.size(); i++){
                                Utente u = listaUtenti.get(i);
                                if(u.getID() == id){
                                    if(u.isConnected()){
                                        u.disconnect();
                                        listaUtenti.set(i,u);
                                        writer.writeBytes("[OK]\n");
                                        salvaUtenti(listaUtenti);
                                        break switchcase;
                                    }else{
                                        writer.writeBytes("[ER]Connesso\n");
                                        break switchcase;
                                    }
                                }
                            }
                            writer.writeBytes("[ER]Utente\n");
                            break;
                        }
                        case "-Richiesta":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(int i = 0; i < listaUtenti.size(); i++){
                                Utente u = listaUtenti.get(i);
                                if(u.getID() == id){
                                    if(!u.isConnected()){
                                        writer.writeBytes("[ER]Non connesso\n");
                                        break switchcase;
                                    }
                                    writer.writeBytes("[OK]"+Server.gestoreAste.toString()+"\n");
                                    break switchcase;
                                }
                            }
                            writer.writeBytes("[ER]Errore\n");
                            break;
                        }
                        case "-Inserimento":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(Utente u: listaUtenti){
                                if(u.getID() == id){
                                    if(!u.isConnected()){
                                        writer.writeBytes("[ER]Non connesso\n");
                                        break switchcase;
                                    }
                                    writer.writeBytes("[OK]\n");
                                    ricevuto = reader.readLine();

                                    int quantitaOggetti = Integer.parseInt(ricevuto.split("\\|")[0]);
                                    int idAstaInCuiInserire = Integer.parseInt(ricevuto.split("\\|")[1]);

                                    System.out.println(quantitaOggetti + "" + idAstaInCuiInserire);

                                    if(quantitaOggetti > 4){
                                        writer.writeBytes("[ER]Troppi oggetti\n");
                                        break switchcase;
                                    }
                                    if(idAstaInCuiInserire >= Server.gestoreAste.quantitaAste()){
                                        writer.writeBytes("[ER]Id asta errato\n");
                                        break switchcase;
                                    }
                                    try{
                                        if(!Server.gestoreAste.getAsta(idAstaInCuiInserire).isAperta()){
                                            writer.writeBytes("[ER]Asta chiusa\n");
                                            break switchcase;
                                        }
                                    }catch(IOException e){
                                        writer.writeBytes("[ER]Id asta errato\n");
                                        break switchcase;
                                    }
                                    writer.writeBytes("[OK]\n");

                                    Oggetto[] oggetti = new Oggetto[quantitaOggetti];

                                    for(int i = 0; i < quantitaOggetti; i++){
                                        ricevuto = reader.readLine();
                                        int categoria = Integer.parseInt(ricevuto.split("\\|")[0]);
                                        String nome = ricevuto.split("\\|")[1];
                                        String desc = ricevuto.split("\\|")[2];
                                        try{
                                            oggetti[i] = new Oggetto(categoria, nome, desc);
                                        }catch(IOException e){
                                            writer.writeBytes("[ER]Categoria non valida\n");
                                            break switchcase;
                                        }
                                    }
                                    writer.writeBytes("[OK]\n");

                                    ricevuto = reader.readLine();

                                    String nome = ricevuto.split("\\|")[0];
                                    double prezzoBase = Double.parseDouble(ricevuto.split("\\|")[1]);
                                    double rilancioMinimo = Double.parseDouble(ricevuto.split("\\|")[2]);

                                    System.out.println(nome + prezzoBase + rilancioMinimo);

                                    try{
                                        Lotto l = new Lotto(nome, prezzoBase, rilancioMinimo);
                                        l.inserisciProdotti(oggetti);
                                        Server.gestoreAste.aggiungiLotto(idAstaInCuiInserire, l);
                                        writer.writeBytes("[OK]\n");
                                        Server.gestoreAste.serializza();
                                    }catch(UnknownHostException e){
                                        writer.writeBytes("[ER]errore\n");
                                    }catch(IOException e){
                                        writer.writeBytes("[ER]Asta non trovata\n");
                                    }

                                    break switchcase;
                                }
                            }
                            writer.writeBytes("[ER]errore\n");
                            break switchcase;
                        }
                        case "-Punta":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(Utente u: listaUtenti){
                                if(u.getID() == id){
                                    if(!u.isConnected()){
                                        writer.writeBytes("[ER]Non connesso\n");
                                        break switchcase;
                                    }
                                    writer.writeBytes("[OK]\n");
                                    ricevuto = reader.readLine();
                                    int idLotto = Integer.parseInt(ricevuto.split("\\|")[0]);
                                    int idAsta = Integer.parseInt(ricevuto.split("\\|")[1]);
                                    double rilancio = Double.parseDouble(ricevuto.split("\\|")[2]);
                                    try{
                                        Asta a = Server.gestoreAste.getAsta(idAsta);
                                        Lotto l = a.getLotto(idLotto);
                                        double valoreAttuale = l.getValoreAttuale() + rilancio;
                                        try{
                                            l.effettuaRilancio(valoreAttuale, new String(u.getNome() + " " +  u.getCognome()));
                                        }catch(IOException e){
                                            writer.writeBytes("[ER]Rilancio\n");
                                            e.printStackTrace();
                                            break switchcase;
                                        }
                                        a.replaceLotto(l, idLotto);
                                        Server.gestoreAste.replace(a, idAsta);
                                        scriviAggiornamento(rilancio, valoreAttuale, u, l.getIndirizzoMulticast().toString());
                                        Server.gestoreAste.serializza();
                                        writer.writeBytes("[OK]\n");
                                    }catch(IOException e){
                                        e.printStackTrace();
                                        writer.writeBytes("[ER]AstaLotto\n");
                                        break switchcase;
                                    }
                                    break switchcase;
                                }
                            }
                            writer.writeBytes("[ER]Utente non presente\n");
                            break switchcase;
                        }
                        case "-Crea":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(Utente u: listaUtenti){
                                if(u.getID() == id){
                                    if(!u.isAdmin()){
                                        writer.writeBytes("[ER]NoAdmin\n");
                                        break switchcase;
                                    }
                                    if(!u.isConnected()){
                                        writer.writeBytes("[ER]Non connesso\n");
                                        break switchcase;
                                    }
                                    Server.gestoreAste.creaAsta();
                                    Server.gestoreAste.serializza();
                                    writer.writeBytes("[OK]\n");
                                    break switchcase;
                                }
                            }
                        }
                        case "-Chiudi":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            System.out.println(id);
                            for(Utente u: listaUtenti){
                                if(u.getID() == id){
                                    if(!u.isAdmin()){
                                        writer.writeBytes("[ER]NoAdmin\n");
                                        break switchcase;
                                    }
                                    if(!u.isConnected()){
                                        writer.writeBytes("[ER]Non connesso\n");
                                        break switchcase;
                                    }
                                    writer.writeBytes("[OK]\n");
                                    int idAsta = Integer.parseInt(reader.readLine());
                                    try{
                                        Server.gestoreAste.chiudiAsta(idAsta);
                                        Server.gestoreAste.serializza();
                                        writer.writeBytes("[OK]\n");
                                        break switchcase;
                                    }catch(IOException e){
                                        writer.writeBytes("[ER]Asta non presente\n");
                                        break switchcase;
                                    }
                                }
                            }
                            writer.writeBytes("[ER]Utente non trovato");
                            break switchcase;
                        }
                    }
                }
        } catch (IOException ignore) {
        }
    }

    private ArrayList<Utente> leggiUtenti() throws IOException{
        BufferedReader fileReader;
        ArrayList<String[]> listaStringhe = new ArrayList<>();
        ArrayList<Utente> listaUtenti = new ArrayList<>();
        try{
            fileReader = new BufferedReader(new FileReader(Server.fileUtenti + ".txt"));
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
            File file = new File(Server.fileUtenti + ".txt");
            file.createNewFile();
            return listaUtenti;
        }
    }

    private void salvaUtenti(ArrayList<Utente> lista) throws IOException{
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(Server.fileUtenti + ".txt"));
        for(Utente u: lista){
            fileWriter.write(u.toString());
            fileWriter.newLine();
        }
        fileWriter.close();
    }

    private void scriviAggiornamento(double rilancio,double nuovoValore, Utente utente, String indirizzo) throws IOException{
        DatagramSocket socket = new DatagramSocket();
        InetAddress gruppoMulticast = InetAddress.getByName(indirizzo.substring(1));
        byte[] messaggio = ("Rilancio di " + rilancio + " effettuato da " + utente.getNome() + " " + utente.getCognome() + ", Nuovo valore: " + nuovoValore + "").getBytes(); 
        DatagramPacket packet = new DatagramPacket(messaggio, messaggio.length, gruppoMulticast, 3200);
        socket.send(packet);
        socket.close();
    }
}   
