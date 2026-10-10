package redis;

import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    public static void main(String[] args) {

        RedisDatabase database = new RedisDatabase();

        try {

            ServerSocket server = new ServerSocket(6379);

            System.out.println("Mini Redis Server Started!");
            System.out.println("Listening on port 6379");

            while (true) {

                Socket client = server.accept();

                System.out.println("Client connected!");

                ClientHandler handler =
                        new ClientHandler(client, database);

                Thread thread = new Thread(handler);

                thread.start();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}