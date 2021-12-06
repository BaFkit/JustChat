package JustChat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthorizationService authorizationService;

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }



    public MyServer() {
        try(ServerSocket server = new ServerSocket(PORT)) {
            authorizationService = new BaseAuthService();
            authorizationService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        }catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
            e.printStackTrace();
        }finally {
            if(authorizationService != null) {
                authorizationService.stop();
            }
        }
    }

    public synchronized  boolean isNickBusy(String nick) {
        for (ClientHandler a: clients) {
            if(a.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean sendPrivateMsg(String nick, String msg) {
        for (ClientHandler a: clients) {
            if(a.getName().equals(nick)){
                a.sendMessages(msg);
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler a: clients) {
            a.sendMessages(msg);
        }
    }

    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients");
        for (ClientHandler a: clients) {
            sb.append(a.getName() + " ");
        }
        broadcastMsg(sb.toString());
    }


    public synchronized void subscribe(ClientHandler ch) {
        clients.add(ch);
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler ch) {
        clients.remove(ch);
        broadcastClientsList();
    }
}
