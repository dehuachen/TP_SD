package main;

/**
 * Created by chendehua on 2017/10/8.
 */
public class Node {
    String hostname;
    int port;

    public Node(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public boolean equals(Node node) {
        if (!node.hostname.equalsIgnoreCase(this.hostname)) return false;
        if (node.port != this.port) return false;
        return true;
    }
}
