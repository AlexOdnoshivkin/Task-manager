import http.KVServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            KVServer server = new KVServer();
            server.start();
            TaskManager manager = Managers.getDefault();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


