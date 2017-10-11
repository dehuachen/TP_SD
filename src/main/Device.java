package main;

import comunicator.Callback;
import comunicator.Client;
import comunicator.Server;

import java.util.ArrayList;

/**
 * Created by chendehua on 2017/10/8.
 */
public class Device implements Callback {
    Client client;
    Server server;
    Node self;
    ArrayList<Node> nodes;

    public Device(String hostname, int port) {
        this.client = new Client();
        this.server = new Server(this, port);
        this.self = new Node(hostname, port);
        this.nodes = new ArrayList<>();

        this.server.start();
    }

    public void sendMessage(String hostname, int port, String msg) {
        this.client.sendMessage(hostname, port, msg);
    }

    @Override
    public synchronized void callback(String str) {
        System.out.println(str);
    }

    public static void main(String... args) {
        Device d1 = new Device("localhost", 8080);
        Device d2 = new Device("localhost", 8081);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        d1.sendMessage("localhost", 8081, "hi 8081");
        d2.sendMessage("localhost", 8080, "hi 8080");
    }
}
