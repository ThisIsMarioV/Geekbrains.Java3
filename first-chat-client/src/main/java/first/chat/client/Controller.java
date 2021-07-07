package first.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Controller {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;

    @FXML
    TextArea chatWindow;
    @FXML
    TextField messageWindow;
    @FXML
    TextField usernameWindow, chatName;
    @FXML
    HBox authPanel, messagePanel, namePanel;
    @FXML
    ListView<String> clientsListView;
    @FXML
    PasswordField passwordWindow;


    public void sendToMessage() { // Отправка сообщений на сервер
        try {
            if (!messageWindow.getText().isEmpty()) {
                out.writeUTF(messageWindow.getText());
                messageWindow.clear();
                messageWindow.requestFocus();
            }
        } catch (IOException e) {
            showError("Невозможно отправить запрос авторизации на сервер");
        }
    }

    public void clientsListDoubleClick(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String selectedUser = clientsListView.getSelectionModel().getSelectedItem();
            messageWindow.setText("/w " + selectedUser + " ");
            messageWindow.requestFocus();
            messageWindow.selectEnd();
        }
    }


    public void tryToAuth() { // авторизация
        connect();
        try {
            chatName.appendText("Вы авторизованы как: " + usernameWindow.getText());
            outputStream = new FileOutputStream("history_"+usernameWindow.getText()+".txt", true);
            inputStream = new FileInputStream("history_"+usernameWindow.getText()+".txt");
            out.writeUTF("/auth " + usernameWindow.getText() + " " + passwordWindow.getText());
            usernameWindow.clear();
            passwordWindow.clear();

        } catch (IOException e) {
            showError("Невозможно отправить запрос авторизации на сервер");
        }
    }

    public void connect() { // подключение к серверу
        if (socket != null && !socket.isClosed()) {
            return;
        }
        try {
            socket = new Socket("localhost", 8189);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            new Thread(this::logic).start();

        } catch (IOException e) {
            showError("Не удалось подключиться к серверу");

        }
    }

    public void sendCloseRequest() {
        try {
            if (out != null) {
                out.writeUTF("/exit");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void logic() {
        try {
            while (true) {
                String inputMessage = in.readUTF();
                if (inputMessage.startsWith("/exit ")) { // после авторизации убираем панель авторизации и показываем панель с отправкой сообщений
                    closeConnection();
                }
                if (inputMessage.startsWith("/authok ")) { // после авторизации убираем панель авторизации и показываем панель с отправкой сообщений
                    setAuthorized(true);
                    break;
                }
                chatWindow.appendText(inputMessage + "\n");
            }
            while (true) {
                String inputMessage = in.readUTF();
//                if(!inputMessage.startsWith("/")){
//                    for (int i = 0; i < 100; i++) {
//                        chatWindow.appendText(String.valueOf(inputStream.read()));
//                    }
//                } загрузку истории не успел доделать
                if(!inputMessage.startsWith("/")){
                    outputStream.write(inputMessage.getBytes());
                    outputStream.write("\n".getBytes());
                }
                if (inputMessage.startsWith("/")) { // проверка на служебные сообщения
                    if (inputMessage.equals("/exit")) { // проверка на отключение от сервера
                        chatName.clear();
                        break;
                    }
                    if (inputMessage.startsWith("/clients_list ")) { // список подключенных имен
                        Platform.runLater(() -> {
                            String[] tokens = inputMessage.split("\\s+");
                            clientsListView.getItems().clear();
                            for (int i = 1; i < tokens.length; i++) {
                                clientsListView.getItems().add(tokens[i]);
                            }
                        });


                    }
                    continue;
                }
                chatWindow.appendText(inputMessage + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    public void setAuthorized(boolean authorized) { //проверка на авторизацию на сервере
        messagePanel.setVisible(authorized);
        messagePanel.setManaged(authorized);
        authPanel.setVisible(!authorized);
        authPanel.setManaged(!authorized);
        namePanel.setVisible(authorized);
        namePanel.setManaged(authorized);
        clientsListView.setVisible(authorized);
        clientsListView.setManaged(authorized);
    }

    private void closeConnection() { // закрытие потоков и отключение от сервера
        setAuthorized(false);
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
