package main;

/**
 * Created by chendehua on 2017/10/16.
 */
public class Main {

    public static void startDevices() {

        Printer printer = null;

        // create devices
        Device[] ds = new Device[4];
        for (int i=0; i<ds.length; i++) {
            ds[i] = new Device("localhost", Device.port_base + i, printer);
        }

        // connect to each other
        for (int i=0; i<ds.length; i++) {
            Device.sleep(1000);
            if (i != 0) {
                ds[i].sendMessage("localhost", Device.port_base, Device.ADD+","+ds[i].self.hostname+","+ds[i].self.port);
            }
        }

    }


    public static void main(String... args) {

//        startDevices();
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
