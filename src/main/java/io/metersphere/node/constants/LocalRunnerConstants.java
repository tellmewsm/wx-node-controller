package io.metersphere.node.constants;

import org.apache.commons.lang.StringUtils;

import java.io.File;

public class LocalRunnerConstants {
    public static final String LOCAL_FILE_PATH = StringUtils.join(new String[]{"", "opt", "node-data"}, File.separator);
}
