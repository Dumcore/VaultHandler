package com.epk.discord;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Configuration {

    private static final String appConfigPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "application.properties";

    private static final Properties appProps = new Properties();

    static {
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String envVarName) throws IOException {
        return resolveEnvVars(appProps.getProperty(envVarName));
    }

    private static String resolveEnvVars(String input) {
        if (null == input) throw new IllegalArgumentException();
        Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
        Matcher m = p.matcher(input);

        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            var envVarName = null == m.group(1) ? m.group(2) : m.group(1);
            var envVarValue = System.getenv(envVarName);
            m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}