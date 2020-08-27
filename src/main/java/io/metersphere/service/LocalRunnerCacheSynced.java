package io.metersphere.service;

import io.metersphere.jmeter.LocalRunner;
import io.metersphere.util.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.metersphere.constants.LocalRunnerConstants.LOCAL_FILE_PATH;

@Service
public class LocalRunnerCacheSynced {
    private final Map<String, LocalRunner> cached = new ConcurrentHashMap<>();
    private static final ExecutorService exec = Executors.newSingleThreadExecutor();
    private boolean isRunning = true;

    @PostConstruct
    public void sync() {
        exec.submit(() -> {
            try {
                while (isRunning) {
                    for (String testId : cached.keySet()) {
                        if (!cached.get(testId).isActive()) {
                            cached.remove(testId);
                            String filePath = StringUtils.join(new String[]{LOCAL_FILE_PATH, testId}, File.separator);
                            // todo 删除文件
//                            FileUtils.forceDelete(new File(filePath));
                            LogUtil.info("test is done, remove dir: " + filePath);
                        }
                    }
                    Thread.sleep(1000L);
                }
            } catch (Exception e) {
                LogUtil.error(e);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        isRunning = false;
    }

    public void put(String testId, LocalRunner runner) {
        cached.put(testId, runner);
    }

    public LocalRunner get(String testId) {
        return cached.get(testId);
    }
}
