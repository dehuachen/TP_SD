package main;

import comunicator.Callback;
import comunicator.Client;
import comunicator.Server;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chendehua on 2017/10/8.
 */
public class Device implements Callback {
    Client client;
    Server server;
    Node self;
    ArrayList<Node> nodes;
    HashMap<String, Boolean> auto;

    public Node getSelf() {
        return self;
    }

    public Device(String hostname, int port) {
        this.client = new Client();
        this.server = new Server(this, port);
        this.self = new Node(hostname, port);
        this.nodes = new ArrayList<>();
        this.auto = new HashMap<>();

        this.server.start();
    }

    public boolean check() {
        for (Node node: this.nodes) {
            if (!auto.get(node.hostname)) {
                return false;
            }
        }
        return true;
    }

    public void sendMessage(String hostname, int port, String msg) {
        this.client.sendMessage(hostname, port, msg);
    }

    public void broadcast(Node node) {
        for (Node n: this.nodes) {
            sendMessage(n.hostname, n.port, "add,"+node.hostname+","+node.port);
        }
    }

    public void sendListBack(Node node) {
        sendMessage(node.hostname, node.port, "add,"+self.hostname+","+self.port);
        for (Node n: this.nodes) {
            sendMessage(node.hostname, node.port, "add,"+n.hostname+","+n.port);
        }
    }

    public void addNode(String msg) {
        String[] strings = msg.split(",");
        String hostname = strings[0];
        int port = Integer.parseInt(strings[1]);

        Node node = new Node(hostname, port);

        if (this.self.port == port_base) {
            broadcast(node);
            sendListBack(node);
        }
        auto.put(node.hostname, false);
        nodes.add(node);
    }

    public void request() {}

    public void confirm() {}

    @Override
    public synchronized void callback(String str) {
        String[] command = str.split(",", 2);

        String action = command[0];

        if (action.equalsIgnoreCase(ADD)) {
            addNode(command[1]);
        } else if (action.equalsIgnoreCase(REQUEST)) {

        } else if (action.equalsIgnoreCase(CONFIRM)) {

        }

        System.out.println("(" + this.self.hostname + ", " + this.self.port + "): "+ str);

    }

    public static void startDevices() {

        // create devices
        Device[] ds = new Device[4];
        for (int i=0; i<ds.length; i++) {
            ds[i] = new Device("localhost", port_base + i);
        }

        // connect to each other
        for (int i=0; i<ds.length; i++) {
            sleep(1000);
            if (i != 0) {
                ds[i].sendMessage("localhost", port_base, "add,"+ds[i].self.hostname+","+ds[i].self.port);
            }
        }

    }

    public static void main(String... args) {

        startDevices();

    }

    public final static String ADD = "add";
    public final static String REQUEST = "request";
    public final static String CONFIRM = "confirm";

    public final static int port_base = 8081;
    public final static int port_printer = 8080;

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
