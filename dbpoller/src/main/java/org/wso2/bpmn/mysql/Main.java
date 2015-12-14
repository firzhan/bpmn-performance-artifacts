package org.wso2.bpmn.mysql;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ParseException {

        DBConnector dbConnector = DBConnector.getDBConnector();
        dbConnector.calculateValues();;
    }
}
