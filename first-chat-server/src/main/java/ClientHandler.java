import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler{
    private Server server;
    private Socket socket;
    private String userName = null;
    private DataInputStream in;
    private DataOutputStream out;
    private ExecutorService singleServerThread = Executors.newSingleThreadExecutor();

    public String getUserName() {
        return userName;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            singleServerThread.execute(() -> { // поток общения с клиентом
                logic();
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            singleServerThread.shutdown();
        }

    }

    private void logic() {
        try {
            while (!consumeAuthorizeMessage(in.readUTF())) ;
            while (regularConsumeMessage(in.readUTF())) ;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally { // удаление из списка подключенных клиентов
            System.out.println("Клиент " + userName + " отключился");
            server.unsubscribe(this);
            closeConnection();
        }

    }

    private boolean consumeAuthorizeMessage(String message) throws SQLException {
        {
            if (message.startsWith("/auth ")) {
                String[] tokens = message.split("\\s+");
                if (tokens.length != 3) {
                    sendMessage("Server: не указано имя пользователия или пароль");
                    return false;
                }
                String password = tokens[2];
                String login = tokens[1];
                userName = server.getAuthProvider().getUsernameBuLoginAndPassword(login, password);
                if(userName == null){
                    sendMessage("Server: неверно указан логин или пароль");
                    return false;
                }
                if (server.chekName(userName)) {
                    sendMessage("/authok " + userName);
                    sendMessage("Вы вошли в чат под именем: " + userName);
                    server.subscribe(this);
                    return true;
                } else {
                    sendMessage("Имя занято");
                    return false;
                }
            } else {
                sendMessage("Server: Вам необходимо авторизоваться");
                return false;
            }
        }

    }
    private boolean regularConsumeMessage(String inputMessage) {

        if (inputMessage.startsWith("/")) {
            if (inputMessage.equalsIgnoreCase("/exit")) { // проверка на отключение от сервера
                sendMessage("/exit");
                return false;
            }
            if (inputMessage.startsWith("/w ")) {
                String[] tokens = inputMessage.split("\\s+", 3);
                server.personalMessage(this, tokens[1], tokens[2]);
            }
            return true;
        }
        server.broadcastMessage(userName + ": " + inputMessage);
        return true;
    }


    public void sendMessage(String message) { // отправка сообщения конкретному клиенту, с которым общаемся
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() { // закрытие потоков и отключение клиента
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
