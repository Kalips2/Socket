package Servers;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerPart {

    private static final int PORT = 1035;
    private static String command;
    private static final String WHO = "who";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private static Socket client;
    private static ServerSocket serverSocket;
    public static BufferedReader bufferedReader;
    public static BufferedWriter bufferedWriter;

    private static final String path = "D:\\Рабочий стол\\JavaPractice\\SocketLabaAOS\\src\\resources\\text.txt";

    private static final String LOG_PATH = "src/ServerLogs";
    private static Logger logger = Logger.getLogger("ServerLogger");
    private static FileHandler fh;

    public static void main(String[] args) throws IOException {
        fh = new FileHandler(LOG_PATH);
        logger.addHandler(fh);
        logger.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        serverSocket = new ServerSocket(PORT);
        try {
            client = serverSocket.accept();
            logger.info("client accepted");
            while (true) {
                try {
                    client = serverSocket.accept();
                    logger.info("client accepted");
                    bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));


                    command = bufferedReader.readLine();
                    logger.info("receive command " + command);
                    checkCommand(command);

                } catch (Exception e) {
                    client.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }

    private static void checkCommand(String command) throws IOException {
        try {
            switch (command.toLowerCase()) {
                case WHO -> commandWho();
                case HELP -> commandHelp();
                case EXIT -> commandExit();
                default -> сommandAnother(command);
            }
        } catch (Exception e) {
            sentMessageToClient("Сталась помилка! Спробуйте ще раз!");
            logger.info("Server: сталася помилка");
        }
    }

    private static void commandExit() throws IOException {
        logger.info("Server - exit client");
        bufferedWriter.close();
        bufferedReader.close();
        client.close();
        serverSocket.close();
    }


    private static void сommandAnother(String command) throws IOException {
        command = command.trim();
        String[] parser = command.split(" ");
        if (parser.length < 2 || !parser[0].toLowerCase().equals("find")) {
            sentMessageToClient("Такої команди не існує! Спробуйте ще раз!");
            logger.info("Server: невідома команда");
            return;
        }

        List<String> result = new ArrayList<>();
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        StringBuilder context = new StringBuilder();
        for (int i = 1; i < parser.length; i++) {
            context.append(parser[i]).append(" ");
        }
        context.delete(context.length() - 1, context.length());

        List<String> separators = new ArrayList<>(
                Arrays.asList(".", "!", "?", ". ", "! ", "? ")
        );

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            while (line.contains(context)) {

                int i, j;
                i = line.indexOf(String.valueOf(context));
                j = i + context.length();
                while (i > 0 && (!separators.contains(String.valueOf(line.charAt(i))) || separators.contains(context.toString()))) {
                    i--;
                }
                while (j < line.length() && !separators.contains(String.valueOf(line.charAt(j))) && !separators.contains(context.toString())) {
                    j++;
                }

                if (j < line.length()) j++;
                if (i > 0 && separators.contains(String.valueOf(line.charAt(i)))) i++;

                //System.out.println("i = " + i + " j = " + j);
                String found = (String) line.subSequence(i, j);
                line = line.replace(found, "");
                result.add(found.trim());
            }
        }
        if (result.size() == 0) {
            sentMessageToClient("\t" + "В файлі немає такого контексту!");
            logger.info("Server: команда find не знайшла такого контексту");
        } else {
            StringBuilder _result = new StringBuilder();

            _result.append("\t" + "Результати пошуку:" + "\n");
            for (int i = 0; i < result.size(); i++) {
                _result.append((i + 1) + ") " + result.get(i) + "\n");
            }
            sentMessageToClient(_result.toString());
            logger.info("Server: команда find знайшла такой контекст");
        }

    }

    private static void sentMessageToClient(String command) throws IOException {
        //System.out.println(command);
        bufferedWriter.write(command + "\n");
        bufferedWriter.flush();
        client.close();
    }

    private static void commandHelp() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("\t" + "Наразі доступні такі команди для серверу:" + "\n");
        builder.append("1. who " + "\t" + " - Видає загальну інформацію про роботу. Функція немає аргументів." + "\n");
        builder.append("2. help" + "\t" + " - Демонструє можливі команди та їх призначення. Функція немає аргументів." + "\n");
        builder.append("3. find _______" + "\t" + " - Шукає контекст у файлі не сервері. Аргументом є текст, який користувач хоче знайти." + "\n");
        builder.append("\t" + "Повертає або результати пошуку построково, або каже, що такого контексту в файлі немає!" + "\n");
        builder.append("\t" + "Ця функція чутлива до регістру. Тому зважай на великі та маленькі букви!" + "\n");
        builder.append("У разі введення функцій не зі списку, зобразиться відповідне повідомлення та потребує повторного вводу." + "\n");
        builder.append("4. exit" + "\t" + " - Від'єднання від серверу. Вихід з клієнту." + "\n");
        sentMessageToClient(builder.toString());
        logger.info("Server: команда help визвана");
    }

    private static void commandWho() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("\t" + "Роботу виконав студент ФКНК групи К-25 Іванов М.С." + "\n");
        builder.append("Номер варіанту: 10 - Контексний пошук у файлах " + "\n");
        builder.append("Завдання варіанту: На сервері зберігається файл (достатньо лише текстовий). " + "\n");
        builder.append("Користувач в клієнті задає контекст (лише із символів клавіатури), клієнт відсилає останній до сервера для виконання контекстного пошуку рядків. " + "\n");
        builder.append("Результати пошуку порядково відсилаються до клієнта. Якщо пошук пустий, то сервер все одно відсилає відповідне попередження." + "\n");
        sentMessageToClient(builder.toString());
        logger.info("Server: команда who визвана");
    }
}
