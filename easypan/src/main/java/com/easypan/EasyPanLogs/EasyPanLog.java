package com.easypan.EasyPanLogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyPanLog {
    private static final Logger logger = LoggerFactory.getLogger(EasyPanLog.class);

    public static void main(String[] args) {
        logger.debug("This is a debug message.");
    }
}
