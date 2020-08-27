package io.metersphere.jmeter;

import io.metersphere.config.JmeterProperties;
import io.metersphere.util.LogUtil;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

@Service
public class JMeterService {

    @Resource
    private JmeterProperties jmeterProperties;

    public LocalRunner run(String testId, File script) {
        String JMETER_HOME = jmeterProperties.getHome();

        String JMETER_PROPERTIES = JMETER_HOME + "/bin/jmeter.properties";
        JMeterUtils.loadJMeterProperties(JMETER_PROPERTIES);
        JMeterUtils.setJMeterHome(JMETER_HOME);
        try {
            HashTree testPlan = SaveService.loadTree(script);

            LocalRunner runner = new LocalRunner(testId, testPlan);
            runner.run();
            return runner;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
