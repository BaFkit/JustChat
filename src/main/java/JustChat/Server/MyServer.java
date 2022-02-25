package JustChat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyServer {
    private final int PORT = 8189;
    private final int PORT2 = 8190;

    private static final Logger log = LogManager.getLogger(MyServer.class);
    private List<ClientHandler> clients;
    private AuthorizationService authorizationService;
    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }
    public MyServer() {
        try(ServerSocket server = new ServerSocket(PORT); ServerSocket server2 = new ServerSocket(PORT2)) {
            log.info("Server started");
            authorizationService = new BaseAuthService();
            authorizationService.start();
            clients = new ArrayList<>();
            while (true) {
                Socket socket = server.accept();
                Socket socket2 = server2.accept();
                new ClientHandler(this, socket, socket2);
            }
        }catch (IOException | ClassNotFoundException | SQLException e) {
            log.fatal("Server error");
            e.printStackTrace();
        }finally {
            if(authorizationService != null) {
                authorizationService.stop();
            }
            log.info("Server stopped");
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
            sb.append(a.getName()).append(" ");
        }
        broadcastMsg(sb.toString());
    }


    public synchronized void subscribe(ClientHandler ch) {
        clients.add(ch);
        log.info("{}{}{}", "Client ", ch.getName(), " connected");
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler ch) {
        clients.remove(ch);
        log.info("{}{}{}", "Client ", ch.getName(), " disconnected");
        broadcastClientsList();
    }

}

