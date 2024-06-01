package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client implements Runnable{
    private BufferedReader reader;
    private DataOutputStream writer;
    private Scanner scanner;
    private int porta = 3000;
    private boolean loggedIn = false;
    private int id = -1;
    private boolean admin = false;
    private ArrayList<ThreadMulticast> threadPool = new ArrayList<>();

    public static void main(String[] args) {
        new Client();
    }
    
    public Client(){
        new Thread(this).start();
    }

    public boolean isAdmin(){
        return admin;
    }

    public void admin(boolean is){
        admin = is;
    }


    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", porta)) {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new DataOutputStream(socket.getOutputStream());
                scanner = new Scanner(System.in);
                int scelta = 0;
                connessione: while(true){
                    while(!loggedIn && scelta >= 0 && scelta <= 1){
                        System.out.println("0: Registrati\n1: Login\n2: Esci");
                        try{
                            scelta = scanner.nextInt();
                        }catch(InputMismatchException e){
                            System.out.println("Errore: Valore inserito errato");
                            scelta = 43829402;
                        }
                        scanner.nextLine();
                        if(scelta == 2){
                            loggedIn = false;
                            break connessione;
                        } 
                        switch(scelta){
                            case 0:{
                                //registrati();
                                break;
                            }
                            case 1:{
                                //accedi();
                                break;
                            }
                            default:
                                break;
                        }
                    }
                    if(admin){
                        while(loggedIn){
                            if(id == -1){
                                System.out.println("Errore, riprovare il login");
                                admin = false;
                                scelta = 0;
                            }else{
                                System.out.println("0: Logout\n1: Richiedi lista aste\n" + "2: Crea asta\n" + "3: Chiudi asta");
                                try{
                                    scelta = scanner.nextInt();
                                }catch(InputMismatchException e){
                                    System.out.println("Errore: Valore inserito errato");
                                    scelta = 432482;
                                }
                                scanner.nextLine();
                            }
                            if(scelta == 0){
                                disconnetti();
                                break;
                            }
                            switch(scelta){
                                case 1:{
                                    richiediLista();
                                    break;
                                }
                                case 2:{
                                    creaAsta();
                                    break;
                                }
                                case 3:{
                                    //chiudiAsta();
                                    break;
                                }
                                default: break;
                            }
                        }
                    }else{
                        while(loggedIn){
                            if(id == -1){
                                System.out.println("Errore, riprovare il login");
                                scelta = 0;
                            }else{
                                System.out.println("0: Logout\n1: Richiedi lista aste\n2: Inserisci lotto\n" + 
                                "3: Entra gruppo lotto\n4: Esci gruppo lotto\n5: Effettua rilancio");
                                try{
                                    scelta = scanner.nextInt();
                                }catch(InputMismatchException e){
                                    System.out.println("Errore: Valore inserito errato");
                                    scelta = 3129;
                                }
                                scanner.nextLine();
                            }
    
                            if(scelta == 0){
                                disconnetti();
                                break;
                            }
                            switch(scelta){
                                case 1:{
                                    richiediLista();
                                    break;
                                }
                                case 2:{
                                    //inserisciLotto();
                                    break;
                                }
                                case 3:{
                                    //entraGruppo();
                                    break;
                                }
                                case 4:{
                                    //esciGruppo();
                                    break;
                                }
                                case 5:{
                                    //effettuaRilancio();
                                    break;
                                }
                                default: break;
                            }
                        }
                    }
                    
                }
                scanner.close();
                reader.close();
                writer.close();
        } catch (IOException ignore) {
        }
    }

    public void registrati(String nome, String cognome, String email, String password, String tel) throws IOException{
        writer.writeBytes("-Registra|" + nome + "|" + cognome + "|" + 
                            email + "|" + password + "|" + tel + "\n");
        String risposta;

        risposta = reader.readLine();
        if(risposta.contains("[OK]")){
            System.out.println("Registrazione avvenuta con successo");
        }else{
            switch(risposta.substring(4).trim()){
                case "Esistente":
                    throw new IOException("Esistente");
                case "Dati":
                    throw new IOException("Dati");
                default:
                    throw new IOException("Errore");
            }
        }
    }

    public boolean accedi(String email, String password) throws IOException{
        System.out.println("Inserisci email, password");
        writer.writeBytes("-Login|" + email + "|" + password + "\n");
        String risposta;
        risposta = reader.readLine();
        if(risposta.contains("[OK]") || risposta.contains("[AD]")){
            id = Integer.parseInt(risposta.substring(4));
            System.out.println("Login avvenuto con successo");
            loggedIn = true;
            if(risposta.contains("[AD]")){
                System.out.println("Account admin attivo");
                admin = true;
            } 
            return admin;
        }else{
            switch(risposta.substring(4).trim()){
                case "Dati":
                    System.out.println("Dati");
                    throw new IOException("Dati");
                case "Connected":
                    System.out.println("Connected");
                    throw new IOException("Connected");
                default:
                    System.out.println("Pipo");
                    throw new IOException("Errore, si prega di riprovare");
            }
        }
    }

    public void disconnetti() throws IOException{
        if(id != -1){
            writer.writeBytes("-Logout|" + id + "\n");
            String risposta = reader.readLine();
            if(risposta.contains("[OK]")){
                System.out.println("Logout avvenuto con successo");
                admin = false;
            }else{
                switch (risposta.substring(4)) {
                    case "Connesso": case "Utente":
                        throw new IOException("Utente");
                    default:
                        throw new IOException("Errore, si prega di riprovare");
                }
            }
        }
        loggedIn = false;
    }

    public String richiediLista() throws IOException{
        String list = "";
        writer.writeBytes("-Richiesta|" + id + "\n");
        String risposta;
        risposta = reader.readLine();
        if(risposta.contains("[OK]")){
            int numeroRighe;
            numeroRighe = Integer.parseInt(risposta.substring(4,risposta.indexOf("|")));
            for(int i = 0; i <= numeroRighe; i++){
                list += reader.readLine() + "\n";
            }
            return list;
        }else{
            switch (risposta.substring(4)) {
                case "Non connesso":
                    throw new IOException("Utente non connesso");
            
                default:
                    throw new IOException("Errore, si prega di riprovare");
            }
        }
    }

    public void creaAsta() throws IOException{
        writer.writeBytes("-Crea|" + id + "\n");
        String risposta;
        risposta = reader.readLine();
        if(risposta.contains("[OK]")){
            System.out.println("Asta creata con successo");
        }else{
            switch(risposta.substring(4)){
                case "NoAdmin":
                    System.out.println("Errore, account non admin");
                    id = -1;
                    break;
                case "Non connesso":
                    System.out.println("Utente non connesso, riprovare");
                    id = -1;
                    break;
                default:
                    System.out.println("Errore, riprovare");
            }
        }
    }

    public void chiudiAsta(int idAsta) throws IOException{
        writer.writeBytes("-Chiudi|" + id + "\n");
        String risposta;
        risposta = reader.readLine();
        if(risposta.contains("[OK]")){
            writer.writeBytes(idAsta + "\n");
            risposta = reader.readLine();
            if(risposta.contains("[OK]")){
                System.out.println("Asta chiusa con successo");
                return;
            }else{
                switch (risposta.substring(4)) {
                    case "Asta non presente":{
                        throw new IOException("Asta non presente");
                    }
                    case "Utente non trovato":{
                        id = -1;
                        throw new IOException("Utente non trovato");
                    }
                    default:
                        throw new IOException("Errore, riprovare");
                }
            }
        }else{
            switch (risposta.substring(4)) {
                case "Non connesso":
                    id = -1;
                    throw new IOException("Errore, l'utente non risulta connesso");
                case "NoAdmin":{
                    throw new IOException("Errore, account non admin");
                }
                default:
                    throw new IOException("Errore, riprovare");
            }
        }
    }

    public void inserisciLotto(int numOggetti, int idAsta, int[] categoria, String[] nome, String[] desc, String nomeLotto, double prezzoBase, double rilancioMinimo) throws IOException{
        writer.writeBytes("-Inserimento|" + id + "\n");
        String risposta = reader.readLine();
        if(risposta.contains("[OK]")){
            writer.writeBytes(numOggetti + "|" + idAsta + "\n");
            risposta = reader.readLine();
            if(risposta.contains("[OK]")){
                for(int i = 0; i < numOggetti; i++){
                    writer.writeBytes(categoria[i] + "|" + nome[i] + "|" + desc[i] + "\n");
                }
                risposta = reader.readLine();
                if(risposta.contains("[OK]")){
                    writer.writeBytes(nomeLotto + "|" + (double) prezzoBase + "|" + (double) rilancioMinimo + "\n");
                    risposta = reader.readLine();
                    if(!risposta.contains("[OK]")){
                        throw new IOException("Errore");
                    }
                }else{
                    switch(risposta.substring(4)){
                        case "Categoria non valida":
                            throw new IOException("Categoria");
                        default:
                            throw new IOException("Errore");
                    }
                }
            }else{
                switch(risposta.substring(4)){
                    case "Troppi oggetti":
                        throw new IOException("Troppi");
                    case "Id asta errato":
                        throw new IOException("Id");
                    case "Asta chiusa":
                        throw new IOException("Asta");
                    default:
                        throw new IOException("Errore");
                }
            }
        }else{
            switch(risposta.substring(4)){
                case "Non connesso":
                    id = -1;
                    throw new IOException("Non connesso");
                default:
                    id = -1;
                    throw new IOException("Errore");
            }
        }
    }

    public void entraGruppo(String indirizzo) throws IOException{
        try{
            for(ThreadMulticast t: threadPool){
                if(t.getIndirizzo().toString().contains(indirizzo)){
                    throw new IOException("Fai già parte del gruppo");
                }
            }
            ThreadMulticast thread = new ThreadMulticast(indirizzo);
            thread.start();
            threadPool.add(thread);
        }catch(UnknownHostException e){
            throw new IOException("Indirizzo non valido");
        }
    }

    public void esciGruppo(String indirizzo) throws IOException{
        leavegroup:{
            for(int i = 0; i < threadPool.size(); i++){
                ThreadMulticast t = threadPool.get(i);
                if(t.getIndirizzo().toString().contains(indirizzo)){
                    threadPool.get(i).interrupt();
                    break leavegroup;
                }
            }
            throw new IOException("Non fai parte del gruppo");
        }
    }

    public void effettuaRilancio(int idLotto, int idAsta, double rilancio) throws IOException{
        writer.writeBytes("-Punta|" + id + "\n");
        String risposta;
        risposta = reader.readLine();
        if(risposta.contains("[OK]")){
            writer.writeBytes(idLotto + "|" + idAsta + "|" + rilancio + "\n");
            risposta = reader.readLine();
            if(risposta.contains("[OK]")){
                System.out.println("Rilancio efettuato con successo");
                return;
            }else{
                switch(risposta.substring(4)){
                    case "Rilancio":
                        System.out.println("pipo");
                        throw new IOException("Rilancio");
                    case "AstaLotto":
                        throw new IOException("Asta");
                    default:
                        throw new IOException("Errore");
                }
            }
        }else{
            switch (risposta.substring(4)) {
                case "Non connesso":
                    id = -1;
                    throw new IOException("Non connesso");
                case "Utente non presente":
                    id = -1;
                    throw new IOException("Utente non presente");
            }
        }
    }

    public void stop(){
        for(Thread t: threadPool){
            t.interrupt();
        }
        try {
            reader.close();
            writer.close();
        } catch (IOException ignore) {
        } catch(NullPointerException ignore){}
        System.exit(0);
    }

    public void die(){
        try {
            writer.writeBytes("-die\n");
            stop();
        } catch (IOException ignore) {
        }
    }

}
