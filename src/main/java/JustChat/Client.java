package JustChat;

import java.io.*;
import java.net.Socket;

public class Client {

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;

    private Socket socket;
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
                if (!login.isEmpty()) {
                    writeToLog(logMessage, login);
                }
            }
            try {
                if(socket == null || socket.isClosed()) {
                    socket = new Socket(SERVER_ADDR, SERVER_PORT);
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
            System.out.println(strFromServer);
            String[] parts = strFromServer.split("\\s+");
            nick = parts[1];
            this.login = parts[2];
            System.out.println(nick);
            transferMsg("Авторизация успешна \n" + "Welcome " + nick + "\n");
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
}




