package Logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Simple logger cause i was too lazy to write System.out.println()
 * + adds some neat features like time
 * @author Manueluz
 * @version 1.0
 */
public class SimpleLogger {

    
    /**
     * Logs a message to the console with the time it was logged
     * @param log The message to log
     */
    public static void log(String log){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        System.out.println("["+date+"][LOG]" + log);
    }
}
