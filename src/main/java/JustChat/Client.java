package JustChat;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Client {

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private final int SERVER_PORT2 = 8190;


    private Socket socket;
    private Socket socket2;
    private DataInputStream in;
    private DataOutputStream out;

    private PrintIn printMsg;

    private String nick = "";
    private String login = "";


    public Client (PrintIn printMsg){
        this.printMsg = printMsg;
    }


    public void sendMessage(String message){
        String logMessage = message.trim();
        if (!logMessage.isEmpty()) {
            if (!login.isEmpty() &&  !logMessage.startsWith("/")) {
                writeToLog(logMessage, login);
            }
        }
        try {
            if(socket == null || socket.isClosed()) {
                socket = new Socket(SERVER_ADDR, SERVER_PORT);
                socket2 = new Socket(SERVER_ADDR, SERVER_PORT2);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.setSoTimeout(120000);
                            while (true) {
                                if (waitAuthorization()) {
                                    socket.setSoTimeout(0);
                                    break;
                                }
                            }
                            while (true) {
                                if (waitDisconnect()) break;
                            }
                        }catch (Exception e) {
                            System.out.println(e + " - Session closed");
                        }finally {
                            closeConnection();
                        }
                    }
                });
                t.setDaemon(true);
                t.start();
            }
            out.writeUTF(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public boolean waitAuthorization() throws IOException {
        String strFromServer = in.readUTF();
        if(strFromServer.startsWith("/authok")) {
            String[] parts = strFromServer.split("\\s+");
            nick = parts[1];
            this.login = parts[2];
            transferMsg("Авторизация успешна \n" + "Welcome " + nick + "\n");
            loadHistory(login);
            return true;
        }
        transferMsg(strFromServer + "\n");
        return false;
    }

    public boolean waitDisconnect() throws IOException {
        String strFromServer = in.readUTF();
        if (strFromServer.equals("/end")) {
            return true;
        }
        transferMsg(strFromServer + "\n");
        return false;
    }

    public void transferMsg(String msg){
        printMsg.printIn(msg);
    }

    public void closeConnection() {
        transferMsg("Соединение с сервером закрыто \n");
        transferMsg("/clients ");
        try {
            in.close();
            out.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToLog(String logMessage, String login){
        try(FileWriter fw = new FileWriter("history_" + login + ".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadHistory(String login){
        try{
            List <String> allLines = Files.readAllLines(Paths.get("history_" + login + ".txt"));
            int allLinesSize = allLines.size();
            if(allLinesSize <= 100) {
                for(String line: allLines) {
                    printMsg.printIn(line + "\n");
                }
            } else {
                for (String line: allLines.subList(allLinesSize - 100, allLinesSize -1)) {
                    printMsg.printIn(line + "\n");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendFile() {
        File file = new File("C:\\Users\\Admin\\IdeaProjects\\JustChat\\src\\main\\java\\JustChat\\ClientFiles\\file.jpg");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            assert socket2 != null;
            try (OutputStream outputStream = socket2.getOutputStream()) {
                 fileInputStream.transferTo(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





