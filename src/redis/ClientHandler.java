package redis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private RedisDatabase database;

    public ClientHandler(Socket socket, RedisDatabase database) {
        this.socket = socket;
        this.database = database;
    }

    public void run() {

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));

            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(), true);

            writer.println("Welcome to Mini Redis!");

            String command;

            while ((command = reader.readLine()) != null) {

                if (command.equalsIgnoreCase("QUIT")) {
                    writer.println("Goodbye!");
                    break;
                }

                CommandHandler handler =
                        new CommandHandler(database);

                String response =
                        handler.execute(command);

                writer.println(response);
            }

            reader.close();
            writer.close();
            socket.close();

        } catch (Exception e) {

            System.out.println("Client disconnected.");
        }
    }
}