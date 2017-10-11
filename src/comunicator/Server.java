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
            // listen for incoming connections on port 15432
            ServerSocket socket = new ServerSocket(this.port);
            System.out.println("Server listening on port " + this.port);

            // loop (forever) until program is stopped
            while(true) {
                // accept a new connection
                Socket client = socket.accept();
                // start a new ServerThread to handle the connection and send
                // output to the client
                Thread thrd = new Thread(new ServerThread(client));
                thrd.start();
                System.out.println("Thread " + thrd.getId() + " started.");

            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }


    class ServerThread extends Thread {
        Socket client = null;
        DataInputStream input;

        public ServerThread(Socket client) {
            this.client = client;
        }

        public void run() {
            System.out.println("Accepted connection. ");

            try {
                // open a new PrintWriter and BufferedReader on the socket
                input = new DataInputStream(client.getInputStream());

                String inString = input.readUTF();

                // run the command using CommandExecutor and get its output
                callback.callback(inString);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                // close the connection to the client
                try {
                    client.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Output closed.");
            }
        }
    }
}