package test.demo.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {

    public static void logThisShit(String log) {
        Date time = new Date();
        DateFormat df = new SimpleDateFormat("ddMMMyyy", Locale.ENGLISH);
        String currentDate = df.format(time);
        File file = new File("C:\\liferay\\log " + currentDate + ".txt");
        String output = "\n" + time + " : " + log;
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
