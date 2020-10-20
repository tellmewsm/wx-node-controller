package io.metersphere.node.service;


import io.metersphere.node.constants.LocalRunnerConstants;
import io.metersphere.node.controller.request.TestRequest;
import io.metersphere.node.jmeter.JMeterService;
import io.metersphere.node.jmeter.LocalRunner;
import io.metersphere.node.util.LogUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class LocalRunnerService {
    @Resource
    private JMeterService jMeterService;
    @Resource
    private LocalRunnerCacheSynced localRunnerCacheSynced;

    public void startTest(TestRequest testRequest) throws IOException {
        String testId = testRequest.getTestId();

        String filePath = StringUtils.join(new String[]{LocalRunnerConstants.LOCAL_FILE_PATH, testId}, File.separator);
        String fileName = testId + ".jmx";

        File script = new File(filePath + File.separator + fileName);
        FileUtils.writeStringToFile(script, testRequest.getFileString(), StandardCharsets.UTF_8);
        // 保存测试数据文件
        Map<String, String> testData = testRequest.getTestData();
        if (!CollectionUtils.isEmpty(testData)) {
            for (String k : testData.keySet()) {
                String v = testData.get(k);
                FileUtils.writeStringToFile(new File(filePath + File.separator + k), v, StandardCharsets.UTF_8);
            }
        }
        LogUtil.info("Start test:" + testRequest.getTestId());
        LocalRunner localRunner = jMeterService.run(testId, script);
        //
        localRunnerCacheSynced.put(testId, localRunner);
    }

    public void stopTest(String testId) {
        final LocalRunner localRunner = localRunnerCacheSynced.get(testId);
        if (localRunner != null) {
            LogUtil.info("Stop test: " + testId);
            localRunner.stop();
        }
    }
}
