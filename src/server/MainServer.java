

package server;

import server.model.Client;
import java.util.concurrent.LinkedBlockingQueue;
import server.model.Request;

public class MainServer {
    public static void main(String[] args) {
        MainServer server = new MainServer("localhost", 16801);
    }
    
    private final LinkedBlockingQueue<Client> clients;
    private final ConnectionListener listener;
    private MessageSender messageSender;
    private RequestProcessor requestProcessor;
    
    public MainServer(String serverName, int port) {
        int maxClients = Integer.MAX_VALUE;
        this.clients = new LinkedBlockingQueue<>(maxClients);
        this.requestProcessor = new RequestProcessor(this);
        this.messageSender = new MessageSender();
        // dont add any lines after this. the thread hangs in listener
        this.listener = new ConnectionListener(this, port);
    }
    
    public void addClient(Client client) {
        
        synchronized(clients) {
            clients.add(client);
            //messageSender.updateClients(clients);
        }
    }
    
    public void sendWelcomeMessage(Client client) {
        messageSender.sendWelcomeMessage(client);
        initMessageReceiver(client);
    }
    
    private void initMessageReceiver(Client client) {
        MessageReceiver messageReceiver = new MessageReceiver(this, client);
        new Thread(messageReceiver).start();
        System.out.println("server: client connected and message receiver started");
    }

    void submitRequest(Request request) {
        requestProcessor.submitRequest(request);
    }

    void submitResponse(Request request) {
        messageSender.submitResponseToBeSent(request);
    }
}
