package io.metersphere.jmeter;

import io.metersphere.constants.LocalRunnerConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;

public class LocalRunner {
    private final String testId;
    private final HashTree jmxTree;
    private StandardJMeterEngine engine;

    public LocalRunner(String testId, HashTree jmxTree) {
        this.testId = testId;
        this.jmxTree = jmxTree;
    }

    public void run() {
        this.engine = new StandardJMeterEngine();

        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
        // Store execution results into a .jtl file
        String logFile = StringUtils.join(new String[]{LocalRunnerConstants.LOCAL_FILE_PATH, testId, testId + ".jtl"}, File.separator);
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        jmxTree.add(jmxTree.getArray()[0], logger);

        engine.configure(jmxTree);
        try {
            engine.runTest();
        } catch (JMeterEngineException e) {
            engine.stopTest(true);
        }
    }

    public void stop() {
        if (engine == null) {
            return;
        }
        if (engine.isActive()) {
            engine.stopTest(true);
        }
    }

    public boolean isActive() {
        if (engine == null) {
            return false;
        }
        return engine.isActive();
    }
}
