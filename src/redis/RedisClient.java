package redis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RedisClient {

    public static void main(String[] args) {

        try {

            Socket socket =
                    new Socket("localhost", 6379);

            BufferedReader serverReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));

            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(), true);

            BufferedReader keyboard =
                    new BufferedReader(
                            new InputStreamReader(
                                    System.in));

            System.out.println(
                    serverReader.readLine()
            );

            while (true) {

                System.out.print("redis> ");

                String command =
                        keyboard.readLine();

                if (command == null) {
                    break;
                }

                writer.println(command);

                String response =
                        serverReader.readLine();

                System.out.println(response);

                if (command.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

            keyboard.close();
            serverReader.close();
            writer.close();
            socket.close();

        } catch (Exception e) {

            System.out.println(
                    "Could not connect to Redis server."
            );

            System.out.println(
                    "Make sure RedisServer is running."
            );
        }
    }
}