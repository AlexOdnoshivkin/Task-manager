package manager;

import http.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class HTTPTaskManagerTest extends TaskManagerTest{
    private KVServer server;


    @Override
    @BeforeEach
    void addNewManager() throws IOException {
        server = new KVServer();
        server.start();
        manager = Managers.getDefault();
        super.addNewManager();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }
}
