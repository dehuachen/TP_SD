package main;

/**
 * Created by chendehua on 2017/10/16.
 */
public class Main {

    public static void startDevices() {

        Printer printer = new Printer();
        int num_hosts = 3;

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

        startDevices();
//        Device a = new Device("localhost", 8080);
//        try {
//            System.out.println("sleep");
//            a.semaphore.acquire();
//            sleep(2000);
////            a.semaphore.release();
//            System.out.println("wake up");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

}
