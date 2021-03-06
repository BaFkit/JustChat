package JustChat.Server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket socket;
    private final Socket socket2;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final InputStream inputStreamForFiles;
    private final String puthStorage = "C:\\Users\\Admin\\IdeaProjects\\JustChat\\src\\main\\java\\JustChat\\Server\\Files\\";

    private String name;

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public static ExecutorService clientPool = Executors.newCachedThreadPool();
    private static final Logger log = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(MyServer myServer, Socket socket, Socket socket2) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.socket2 = socket2;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.inputStreamForFiles = socket2.getInputStream();
            this.name = "";
            clientPool.execute(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            });
        } catch (IOException e) {
            log.error("client handler creation error");
            throw new RuntimeException("client handler creation error");
        }
    }

    public void authentication() throws IOException {
        while (true){
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s+");
                String nick = myServer.getAuthorizationService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMessages("/authok " + nick + " " + parts[1]);
                        name = nick;
                        myServer.broadcastMsg(name + " ?????????? ?? ??????");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMessages("?????????????? ???????????? ?????? ????????????????????????");
                    }
                } else {
                    sendMessages("???????????????? ??????????/????????????");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        try {
            while (true) {
                String strFromClient = in.readUTF();
                log.info("{}{}{}", name," wrote: ", strFromClient);
                if(strFromClient.startsWith("/")) {
                    if (strFromClient.equals("/end")) {
                        return;
                    }
                    if (strFromClient.startsWith("/w")) {
                        String[] parts = strFromClient.split("\\s+");
                        String whom = parts[1];
                        String msg = strFromClient.substring(4 + whom.length());
                        if (myServer.sendPrivateMsg(whom, "???????????? ?????????????????? ???? " + name + ": " + msg)) {
                            out.writeUTF("???????????? ?????????????????? ?????? " + whom + ": " + msg);
                        } else {
                            out.writeUTF("???????????????? " + whom + " ???? ?? ????????");
                        }
                    }
                    if (strFromClient.startsWith("/change")){
                        String[] parts = strFromClient.split("\\s+");
                        if(parts.length != 2){
                            sendMessages("?????? ???????????????? ???? ??????????????");
                        }else{
                            String nick = parts[1];
                            if(myServer.getAuthorizationService().changeNick(name, nick)){
                                myServer.broadcastMsg("????????????????????????: " + name + " ?????????????? ?????? ???? - " + nick);
                                this.name = nick;
                                myServer.broadcastClientsList();
                                sendMessages("?????? ?????????????? ?????????????? ????: " + nick);
                            } else {
                                sendMessages("?????? ???????????????? ???? ??????????????");
                            }
                        }
                    }
                    if (strFromClient.startsWith("/sf")){
                        getFile();
                        sendMessages("???????? ?????????????? ???? ????????????");
                    }
                }else{
                    myServer.broadcastMsg(name + ": " + strFromClient);
                }
            }
        } catch (IOException e) {
            log.error("{}{}{}{}", e, " - Client: ", name, " closed the application");
        }
    }

    public void sendMessages(String msg){
        try{
            out.writeUTF(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " ?????????? ???? ????????");
        try{
            in.close();
            out.close();
            socket.close();
            socket2.close();
            inputStreamForFiles.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void getFile() {
        try (socket2) {
            FileOutputStream fileOutputStream = new FileOutputStream(puthStorage + name + "_file.jpg");
            inputStreamForFiles.transferTo(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


