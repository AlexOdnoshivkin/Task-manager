package manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    void addNewManager() throws IOException {
        manager = new InMemoryTaskManager();
        super.addNewManager();
    }
  }