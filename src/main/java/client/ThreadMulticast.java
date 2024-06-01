package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ThreadMulticast extends Thread{
    private InetAddress indirizzo;
    private static final int porta = 3200;
    private MulticastSocket socket;

    public ThreadMulticast(String indirizzo) throws UnknownHostException{
        this.indirizzo = InetAddress.getByName(indirizzo);
    }

    @Override
    public void run() {
        try {
            socket = new MulticastSocket(porta);
            byte[] buffer = new byte[1024];
            socket.joinGroup(indirizzo);
            while(true){
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData());
                System.out.println("Messaggio da " + indirizzo.toString() + ": " + msg);
            }
        }
        catch (IOException e) {
        }
    }

    public InetAddress getIndirizzo() {
        return indirizzo;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void interrupt(){
        socket.close();
        try{    
            this.stop();
        }catch(UnsupportedOperationException ignore){
        }
    }
}
