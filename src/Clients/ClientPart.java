package Clients;

import Servers.ServerPart;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientPart {
    private static final String SERVER_ADDRESS;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static BufferedReader console;
    private static Socket client;

    static {
        try {
            SERVER_ADDRESS = String.valueOf(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int PORT = 1035;

    private static final String path = "D:\\Рабочий стол\\JavaPractice\\SocketLabaAOS\\src\\resources\\text.txt";

    private static final String LOG_PATH = "src/ClientLogs";
    static Logger logger = Logger.getLogger("ClientLogger");
    static FileHandler fh;

    public static void main(String[] args) throws IOException, InterruptedException {

        fh = new FileHandler(LOG_PATH);
        logger.addHandler(fh);
        logger.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        try {
            while (true) {
                client = new Socket(SERVER_ADDRESS, PORT);
                //Send message
                logger.info("start connection");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

                console = new BufferedReader(new InputStreamReader(System.in));

                String messageOut = console.readLine();
                logger.info("send message");

                bufferedWriter.write(messageOut + "\n");
                bufferedWriter.flush();
                logger.info("get message");

                String line;
                bufferedReader.lines().forEach(System.out::println);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.info("exit from client");
            client.close();
        }
    }
}

