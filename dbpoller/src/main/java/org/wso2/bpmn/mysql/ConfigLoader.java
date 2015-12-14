package org.wso2.bpmn.mysql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private String processDefID;
    private String dburl;
    private String dbUsername;
    private String dbPassword;
    private int initialTruncatingTime;



    private String filePath;

    private static ConfigLoader _instance;

    private ConfigLoader() throws IOException {
        loadConfigFromFile();
    }

    public static ConfigLoader getInstance() throws IOException {
        if(_instance == null){
            synchronized (ConfigLoader.class){
                if(_instance == null){
                    _instance = new ConfigLoader();
                }
            }
        }

        return _instance;
    }

    private void loadConfigFromFile() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config/config.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        // get the property value and print it out
        processDefID = prop.getProperty("processDefID");
        dburl = prop.getProperty("db_url");
        dbUsername = prop.getProperty("db_username");
        dbPassword = prop.getProperty("db_password");
        filePath   = prop.getProperty("file_path");
        initialTruncatingTime = Integer.parseInt(prop.getProperty("initial_truncating_time"));

        System.out.println(processDefID + " " + dburl + " " + dbUsername + " " + dbPassword + " " + filePath);
    }

    public String getProcessDefID() {
        return processDefID;
    }

    public String getDburl() {
        return dburl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getInitialTruncatingTime() {
        return initialTruncatingTime;
    }

}
