package manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    void addNewManager(){
        manager = new InMemoryTaskManager();
        super.addNewManager();
    }
  }