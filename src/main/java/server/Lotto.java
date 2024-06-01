package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Lotto implements Serializable{
    final static long serialVersionUID = 319310233;
    public final int ID_LOTTO;
    private String vincitoreAttuale;
    private double valoreAttuale;
    private String nomeLotto;
    private List<Oggetto> oggettiCompresi;
    private InetAddress indirizzoMulticast;
    private double prezzoBase;
    private double rilancioMinimo;

    public Lotto(String nomeLotto, double prezzoBase, double rilancioMinimo) throws UnknownHostException{
        int nextCodice = Integer.MAX_VALUE;
        BufferedWriter writer;
        BufferedReader reader;

        try{
            reader = new BufferedReader(new FileReader("src\\nextCodici.txt"));
            int codiceAsta = Integer.parseInt(reader.readLine());
            nextCodice = Integer.parseInt(reader.readLine());
            int codiceOggetto = Integer.parseInt(reader.readLine());
            int codiceUtente = Integer.parseInt(reader.readLine());
            reader.close();
            writer = new BufferedWriter(new FileWriter("src\\nextCodici.txt"));
            writer.write(Integer.toString(codiceAsta) + "\n");
            writer.append(Integer.toString((nextCodice + 1)) + "\n");
            writer.append(Integer.toString(codiceOggetto) + "\n");
            writer.append(Integer.toString(codiceUtente));
            writer.close();
        }catch(IOException ignore){
        }

        ID_LOTTO = nextCodice;
        indirizzoMulticast = InetAddress.getByName("224.0.1." + ID_LOTTO);
        this.nomeLotto = nomeLotto;
        this.prezzoBase = prezzoBase;
        this.rilancioMinimo = rilancioMinimo;
        valoreAttuale = getPrezzoMinimo();
        vincitoreAttuale = "Nessuno";
        oggettiCompresi = new ArrayList<>();
    }

    public void inserisciProdotti(Oggetto[] prodotti) throws IOException{
        if(prodotti == null) throw new IOException("Prodotti non validi");
        for(Oggetto o: prodotti){
            if(o == null) throw new IOException("Prodotti non validi" + o);
            for(Oggetto oo: oggettiCompresi){
                if(o.getID() == oo.getID()) throw new IOException("Prodotti già presenti");
            }
        }
        for(Oggetto o: prodotti){
            oggettiCompresi.add(o);
        }
    }

    public int getID() {
        return ID_LOTTO;
    }

    public int numOggetti(){
        return oggettiCompresi.size();
    }

    public String getNomeLotto() {
        return nomeLotto;
    }

    public synchronized void effettuaRilancio(double nuovoValore, String nominativo) throws IOException{
        if(nuovoValore < valoreAttuale + rilancioMinimo) throw new IOException("Il rilancio minimo deve essere di " + rilancioMinimo);
        valoreAttuale = nuovoValore;
        vincitoreAttuale = nominativo;
    }

    public String getVincitoreAttuale(){
        return vincitoreAttuale;
    }

    public double getPrezzoMinimo() {
        return prezzoBase * 4 / 5;
    }

    public InetAddress getIndirizzoMulticast() {
        return indirizzoMulticast;
    }

    public double getRilancioMinimo(){
        return rilancioMinimo;
    }

    public double getValoreAttuale() {
        return valoreAttuale;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Nome: " + getNomeLotto() + "|Valore Attuale: " + getValoreAttuale() + "|VincitoreAttuale: " + getVincitoreAttuale() + "|Indirizzo multicast: " + getIndirizzoMulticast().toString();
        return s;
    }
}
