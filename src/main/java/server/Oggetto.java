package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Oggetto implements Serializable{
    final static long serialVersionUID = 319310233;
    private final int ID_OGGETTO;
    private String nome, descrizione;
    private CATEGORIE categoria;
    public enum CATEGORIE{
        ELETTRONICA, ABBIGLIAMENTO, ELETTRODOMESTICI,
        BAMBINI, GIARDINAGGIO, SCOLASTICO, LAVORO, 
        CUCINA, ALTRO;

        public static CATEGORIE getCategoriaByValue(int value){
            switch(value){
                case 1:
                    return ELETTRONICA;
                case 2:
                    return ABBIGLIAMENTO;
                case 3:
                    return ELETTRODOMESTICI;
                case 4: 
                    return BAMBINI;
                case 5:
                    return GIARDINAGGIO;
                case 6:
                    return SCOLASTICO;
                case 7: 
                    return LAVORO;
                case 8:
                    return CUCINA;
                default: return ALTRO;
            }
        }

        public static int getValueByCategoria(CATEGORIE categoria){
            switch(categoria){
                case ELETTRONICA: return 1;
                case ABBIGLIAMENTO: return 2;
                case ELETTRODOMESTICI: return 3;
                case BAMBINI: return 4;
                case GIARDINAGGIO: return 5;
                case SCOLASTICO: return 6;
                case LAVORO: return 7;
                case CUCINA: return 8;
                default: return 9;
            }
        }
    };


    public Oggetto(int categoria, String nome, String descrizione) throws IOException{
        int nextCodice = Integer.MAX_VALUE;
        BufferedWriter writer;
        BufferedReader reader;

        try{
            reader = new BufferedReader(new FileReader("src\\nextCodici.txt"));
            int codiceAsta = Integer.parseInt(reader.readLine());
            int codiceLotto = Integer.parseInt(reader.readLine());
            nextCodice = Integer.parseInt(reader.readLine());
            int codiceUtente = Integer.parseInt(reader.readLine());
            reader.close();
            writer = new BufferedWriter(new FileWriter("src\\nextCodici.txt"));
            writer.write(Integer.toString(codiceAsta) + "\n");
            writer.append(Integer.toString(codiceLotto) + "\n");
            writer.append(Integer.toString((nextCodice + 1)) + "\n");
            writer.append(Integer.toString(codiceUtente));
            writer.close();
        }catch(IOException ignore){
        }

        ID_OGGETTO = nextCodice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.categoria = CATEGORIE.getCategoriaByValue(categoria);
        if (this.categoria == null) throw new IOException("Categoria non valida");
    }

    public int getCategoriaValue(){
        return CATEGORIE.getValueByCategoria(categoria);
    }

    public CATEGORIE getCategoria() {
        return categoria;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getID() {
        return ID_OGGETTO;
    }
    
    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return getID() + "|" + getNome() + "|" + getDescrizione();
    }
}
