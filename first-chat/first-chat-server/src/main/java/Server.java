import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clientHandlerList; // список подключенных клиентов

    public Server() {
        try {
            this.clientHandlerList = new ArrayList<>(); // создаем список
            ServerSocket serverSocket = new ServerSocket(8189); // указываем порт подключения
            System.out.println("Сервер запущен. Ожидаем подключения клиентов.");

            while (true) { // ждем подключения новых клиентов
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket); // добавляем клиента в список подключенных клиентов

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler c) { // добавление в список подключенных клиентов

        broadcastMessage(c.getUserName() + " подключился к чату");
        clientHandlerList.add(c);
        broadcastClientList();


    }

    public synchronized void unsubscribe(ClientHandler c) { // удаление из списка подключенных клиентов

        clientHandlerList.remove(c);
        broadcastMessage(c.getUserName() + " отключился от чата");
        broadcastClientList();



    }

    public synchronized void broadcastMessage(String message) { // рассылка сообщения всем подключенным клиентам
        for (ClientHandler c : clientHandlerList) {
            c.sendMessage(message);
        }

    }
    public synchronized void broadcastClientList() {
        StringBuilder stringBuilder = new StringBuilder(clientHandlerList.size()*10);
        stringBuilder.append("/clients_list ");
        for (ClientHandler c : clientHandlerList) {
            stringBuilder.append(c.getUserName()).append(" ");
        }
        String clientsListStr = stringBuilder.toString();
        broadcastMessage(clientsListStr);

    }

    public boolean chekName(String userName) { // Проверяем есть ли другие пользователи с таким именем.  Думал ещё сделать через HasMap, но что-то запутался, хотя до сих пор кажется что через мапу будет лучше
        for (int i = 0; i < clientHandlerList.size(); i++) {
            if (clientHandlerList.get(i).getUserName().equalsIgnoreCase(userName)) {
                return false;
            }
        }
        return true;
    }
    public synchronized void personalMessage(ClientHandler sender, String receiverUsername, String message){
        if(sender.getUserName().equalsIgnoreCase(receiverUsername)){
            sender.sendMessage("Нельзя отправлять личные сообщения самому себе");
            return;
        }
        for (ClientHandler c: clientHandlerList){
            if(c.getUserName().equalsIgnoreCase(receiverUsername)){
                c.sendMessage("от "+sender.getUserName()+" : "+message);
                sender.sendMessage("Пользователю "+ receiverUsername+": "+message);
                return;
            }
        }
        sender.sendMessage("Пользователь "+receiverUsername+" не в сети");
    }
}
