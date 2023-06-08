package dk.dtu.compute.se.pisd.roborallyServer;

import java.net.DatagramSocket;
import java.net.InetAddress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartServer {
    public static void main(String[] args) {

        // Show the IP address of the server. Requires an internet conenction.
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            System.out.println("IP ADDRESS:\n-------------------\n | "
                    + datagramSocket.getLocalAddress().getHostAddress() + " |\n-------------------\n");
        } catch (Exception e) {
            System.out.println("Unable to find IP address");
        }

        SpringApplication.run(StartServer.class, args);
    }
}
