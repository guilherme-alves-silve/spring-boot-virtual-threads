package com.mudra.bestpricebookstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class RestCallStatistics {

    private static final Logger LOG = Logger.getGlobal();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-M-yyyy-hh-mm-ss");
    private static final Path TEXT_FILE_PATH = Paths.get("timing.log");

    private final Lock lock = new ReentrantLock();
    private final Map<String, Long> timeMap = new HashMap<>();
    
    static {

        try {
            boolean exists = Files.exists(TEXT_FILE_PATH);
            if (exists) {
                String dateStr = DATE_FORMAT.format(new Date());
                Files.move(TEXT_FILE_PATH, Paths.get("timing-till-" + dateStr + ".log"));
            }
            
            Files.createFile(TEXT_FILE_PATH);
        } catch (IOException ex) {
            LOG.severe("Error: " + ex.getMessage());
        }
    }
    
    void addTiming(String storeName, long time) {
        lock.lock();
        try {
            timeMap.put(storeName, time);
        } finally {
            lock.unlock();
        }
    }

    public void dumpTiming() {
        lock.lock();
        try {
            Files.write(TEXT_FILE_PATH,
                    String.format("%s;%s;%s\n", 
                            timeMap.get("Wonder Book Store"), 
                            timeMap.get("Mascot Book Store"), 
                            timeMap.get("Best Price Store")).getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException ex) {
            LOG.severe("Error: " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
