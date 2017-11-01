package comunicator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by chendehua on 2017/10/6.
 */
public class Server extends Thread{
    Callback callback;
    int port;

    public Server(Callback callback, int port) {
        this.callback = callback;
        this.port = port;
    }

    @Override
    public void run() {
        startServer();
    }

    public void startServer() {
        try {
            ServerSocket socket = new ServerSocket(this.port);

            while(true) {
                Socket client = socket.accept();
                Thread thrd = new Thread(new ServerThread(client));
                thrd.start();
            }
        } catch (IOException ioe){ ioe.printStackTrace(); }
    }


    class ServerThread extends Thread {
        Socket client = null;
        DataInputStream input;

        public ServerThread(Socket client) {
            this.client = client;
        }

        public void run() {
            try {

                input = new DataInputStream(client.getInputStream());
                String inString = input.readUTF();
                callback.callback(inString); // notify the device

            } catch (IOException e) { e.printStackTrace(); }
            finally {
                try { client.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}