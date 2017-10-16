package main;

import comunicator.Callback;
import comunicator.Client;
import comunicator.Server;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by chendehua on 2017/10/8.
 */
public class Device extends Thread implements Callback {
    Client client;
    Server server;
    Node self;
    ArrayList<Node> nodes;
    HashMap<Node, Boolean> auto;
    Queue<Node> queue;
    Semaphore semaphore;
    PrinterInterface printer;
    private boolean requesting;
    private boolean printing;
    private int timestamp;
    private int requesting_time;

    public Device(String hostname, int port, PrinterInterface printer) {
        this.client = new Client();
        this.server = new Server(this, port);
        this.printer = printer;
        this.self = new Node(hostname, port);

        this.nodes = new ArrayList<>();
        this.auto = new HashMap<>();
        this.queue = new ArrayDeque<>();
        this.requesting = false;
        this.printing = false;
        this.timestamp = 0;

        this.semaphore = new Semaphore(1);

        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.server.start();
    }

    private boolean check() {
        for (Node node: this.nodes) {
            if (!auto.get(node)) {
                return false;
            }
        }
        return true;
    }

    public void sendMessage(String hostname, int port, String msg) {
        this.client.sendMessage(hostname, port, msg);
    }

    private void addNode(String msg) {

        Node node = parseMsg(msg);

        if (this.self.port == port_base) {
            broadcast(node);
            sendListBack(node);
        }
        auto.put(node, false);
        nodes.add(node);
    }

    private void receiveRequest(String msg) {
        Node node = parseMsg(msg);
        int time_ = getTime(msg);
        this.timestamp = Math.max(this.timestamp, time_);
        node = findNodeRef(node);

        if (this.requesting) {
            if (this.printing) {
                queue.add(node);
                auto.put(node, false);
            } else {
                // greater than local timestamp
                if (checkTimestamp(msg)) {
                    this.timestamp++;
                    sendMessage(node.hostname, node.port, formatMsg(self, CONFIRM));
                } else {
                    queue.add(node);
                    auto.put(node, false);
                }
            }
        } else {
            this.timestamp++;
            sendMessage(node.hostname, node.port, formatMsg(self, CONFIRM));
        }

    }

    private void receiveConfirm(String msg) {

        Node node = parseMsg(msg);

        node = findNodeRef(node);
        auto.put(node, true);

        if (check()) {
            this.semaphore.release();
            print();
        }

    }

    @Override
    public synchronized void callback(String str) {
        String[] command = str.split(",", 2);

        String action = command[0];

        if (action.equalsIgnoreCase(ADD)) {
            addNode(command[1]);
        } else if (action.equalsIgnoreCase(REQUEST)) {
            receiveRequest(command[1]);
        } else if (action.equalsIgnoreCase(CONFIRM)) {
            receiveConfirm(command[1]);
        }

        System.out.println("(" + this.self.hostname + ", " + this.self.port + "): "+ str);

    }

    private void sendRequest() {
        this.timestamp++;
        this.requesting_time = this.timestamp;
        for (Node n: nodes) {
            if (!auto.get(n)) {
                sendMessage(n.hostname, n.port, formatMsg(self, REQUEST));
            }
        }
    }

    @Override
    public void run() {
        Random random = new Random();

        while (true) {

            if (random.nextFloat() > 0.7) {
                if (check()) {
                    print();
                    break;
                }
                this.requesting = true;
                sendRequest();
                try {
                    this.semaphore.acquire();
                    this.requesting = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sleep(1000);
        }
    }


    // utils
    private int getTime(String msg) {
        String time = msg.split(",")[2];
        int time_ = Integer.valueOf(time);
        return time_;
    }

    private boolean checkTimestamp(String msg) {
        int time_ = getTime(msg);

        if (this.requesting_time < time_) {
            return false;
        }

        return true;
    }

    private Node parseMsg(String msg) {
        String[] strings = msg.split(",");
        String hostname = strings[0];
        int port = Integer.parseInt(strings[1]);

        return new Node(hostname, port);
    }

    private void print() {
        this.printing = true;

        for (int i = 0; i < 10; i++) {
            this.printer.print(String.valueOf(this.timestamp + i));
            sleep(500);
        }

        this.printing = false;

        if (!queue.isEmpty()) {
            Node n = queue.remove();
            this.timestamp++;
            sendMessage(n.hostname, n.port, formatMsg(self, CONFIRM));
        }
    }

    private String formatMsg(Node node, String type) {
        return type+","+node.hostname+","+node.port+","+this.timestamp;
    }

    private void broadcast(Node node) {
        for (Node n: this.nodes) {
            sendMessage(n.hostname, n.port, ADD+","+node.hostname+","+node.port);
        }
    }

    private void sendListBack(Node node) {
        sendMessage(node.hostname, node.port, ADD+","+self.hostname+","+self.port);
        for (Node n: this.nodes) {
            sendMessage(node.hostname, node.port, ADD+","+n.hostname+","+n.port);
        }
    }

    private Node findNodeRef(Node node) {
        for (Node n: this.nodes) {
            if (node.equals(n)) {
                return n;
            }
        }
        return null;
    }


    // global attributes
    public final static String ADD = "add";
    public final static String REQUEST = "request";
    public final static String CONFIRM = "confirm";
    public final static int port_base = 8081;

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
