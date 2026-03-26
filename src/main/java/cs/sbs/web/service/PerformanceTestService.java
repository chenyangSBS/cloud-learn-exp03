package cs.sbs.web.service;

import org.springframework.stereotype.Service;

@Service
public class PerformanceTestService {

    public String fastOperation() {
        simulateDelay(20);
        return "快速操作完成";
    }

    public String normalOperation() {
        simulateDelay(70);
        return "正常操作完成";
    }

    public String slowOperation() {
        simulateDelay(150);
        return "慢速操作完成";
    }

    public void batchOperation(int count) {
        for (int i = 0; i < count; i++) {
            simulateDelay(30);
        }
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
