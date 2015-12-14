package org.wso2.bpmn.mysql;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBConnector {

    private static Timestamp startTime;
    private static Timestamp stopTime;
    private static Timestamp initialStartTime;
    private static Timestamp leastCalculationTime;


    private static DBConnector _instance;

    private Connection connection = null;
    private Statement statement = null;



    private DBConnector(){
        try {
            initiateDBOperation();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DBConnector getDBConnector(){

        if(_instance == null){
            synchronized (DBConnector.class){
                if(_instance == null){
                    _instance = new DBConnector();
                }
            }
        }

        return _instance;
    }


    private void initiateDBOperation() throws ClassNotFoundException, SQLException, IOException {

        System.out.println("-------- MySQL JDBC Connection Testing ------------");
        Class.forName("com.mysql.jdbc.Driver");

        ConfigLoader configLoader = ConfigLoader.getInstance();

        connection = DriverManager
                .getConnection(configLoader.getDburl(),configLoader.getDbUsername(), configLoader.getDbPassword());
        statement = connection.createStatement();

    }


    public void calculateValues() throws SQLException, IOException, ParseException {

        ResultSet resultSet = null;

        try {
            ConfigLoader configLoader = ConfigLoader.getInstance();
            resultSet = statement.executeQuery("select START_TIME_, END_TIME_, DURATION_ FROM ACT_HI_PROCINST WHERE " +
                    "PROC_DEF_ID_ " +
                    "= '" + configLoader.getProcessDefID() + "' AND END_TIME_ IS NOT NULL ORDER BY START_TIME_ ASC");

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("INSTANCE_COUNT__ \t\t");
            stringBuffer.append("START_TIME_ \t\t");
            stringBuffer.append("END_TIME_ \t\t\t");
            stringBuffer.append("DURATION_ \t");
            int rowCount = 0;
            Long totalDuration = 0l;
            while (resultSet.next()){

                Timestamp rowStartTime = resultSet.getTimestamp(1);
                Timestamp rowStopTime = resultSet.getTimestamp(2);
                Long duration = resultSet.getLong(3);

                if(initialStartTime == null){

                    initialStartTime = rowStartTime;

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(rowStartTime.getTime());
                    cal.add(Calendar.SECOND, configLoader.getInitialTruncatingTime());

                    leastCalculationTime =  new Timestamp(cal.getTime().getTime());

                }

                if( rowStartTime.before(leastCalculationTime) == true ){
                    continue;
                }

                if(startTime == null){
                    startTime = rowStartTime;
                }

                if (stopTime == null){
                    stopTime = rowStopTime;
                }

                if ( rowStopTime.after(stopTime) ){
                    stopTime = rowStopTime;
                }

                totalDuration += duration;
                ++rowCount;

                String resultString = rowCount + "\t\t\t" + rowStartTime.toString() + "\t\t" + rowStopTime + "\t\t" +
                        duration;

                stringBuffer.append("\n");
                stringBuffer.append(resultString);

            }

            if(rowCount ==0){
                System.out.println("Error : Row count not found : ");
            }

            double averageDuration = (double)totalDuration/rowCount;


            double tps = ((double)rowCount)/calculateTimeDifferenceInSeconds();
            String resultValue = "\tstartTime: " + startTime + "\n" + "\tendTime: " + stopTime + "\n" + "\tTotal " +
                    "duration: " +
                    totalDuration + "\n" + "\tAverage duration: " + averageDuration + "\n" + "\tInstance count: " +
                    rowCount + "\n" + "\tTPS: "
                    + tps;

            stringBuffer.append("\n\n\n\n");
            stringBuffer.append(resultValue);
            FileUtils.writeStringToFile(new File(configLoader.getFilePath()), stringBuffer.toString());

            System.out.println("===================Completed Calculation========================");
            System.out.println(resultValue);
        } finally {


            if( resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if( connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if( statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    private static double calculateTimeDifferenceInSeconds() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = format.parse(startTime.toString());
            endDate = format.parse(stopTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //in milliseconds
        long diff = endDate.getTime() - startDate.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.print(diff + " diff, ");
        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");

        return (double) diff/1000;

    }
}
