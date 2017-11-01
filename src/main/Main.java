package main;

/**
 * Created by chendehua on 2017/10/16.
 */
public class Main {

    public static boolean SHOW_MSG = false;

    public static void startDevices(int num_hosts) {

        Printer printer = new Printer();

        // create devices
        println("Creating hosts...");
        println("Number of hosts: " + num_hosts);
        Device[] ds = new Device[num_hosts];
        for (int i=0; i<ds.length; i++) {
            println("Host "+ i + ": (localhost, " + (Device.port_base + i) + ")");
            ds[i] = new Device("localhost", Device.port_base + i, printer);
        }
        println("Creation: done.");

        // connect to each other
        println("Connecting hosts...");
        for (int i=0; i<ds.length; i++) {
            Device.sleep(1000);
            if (i != 0) {
                ds[i].sendMessage("localhost", Device.port_base, Device.ADD+","+ds[i].self.hostname+","+ds[i].self.port);
            }
        }
        Device.sleep(2000);
        for (int i = 0; i < ds.length; i++) {
            println("Host "+ i + ": " + ds[i].nodes.size() + " hosts connected.");
        }
        println("Connection: done.");

        // start to run
        println("Starting to run...");
        for (int i=0; i<ds.length; i++) {
            println("Host "+ i + ": started.");
                ds[i].start();
        }
        println("All started.\n");

    }

    public static void println(String str) {
        System.out.println(str);
    }


    public static void main(String... args) {

        int num_args = args.length;

        int num_hosts = 6;

        if (num_args > 0) {
            num_hosts = Integer.parseInt(args[0]);
        }

        if (num_args > 1) {
            SHOW_MSG = Boolean.parseBoolean(args[1]);
        }

        startDevices(num_hosts);

    }

}
