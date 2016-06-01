package com.heresysoft.arsenal.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter extends PrintStream {
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    public LogWriter(final String fileName, final int maxFiles, final int maxFileSize) {
        super(new OutputStream() {
            private FileOutputStream stream = null;
            private File logFile = new File(fileName);

            private void archiveLogFile() {
                File file = null;

                try {
                    // Remove older file
                    file = new File(fileName + "." + (maxFiles - 1));
                    if (file.exists())
                        file.delete();

                    // Rename archived files
                    for (int i = maxFiles - 1; i > 0; i--) {
                        file = new File(fileName + "." + (i));
                        if (file.exists())
                            file.renameTo(new File(fileName + "." + (i + 1)));
                    }

                    // Archive current file
                    logFile.renameTo(new File(fileName + ".1"));
                } catch (Exception e) {
                }
            }

            @Override
            public void write(int b) throws IOException {
                if (maxFileSize > 0) {
                    if (stream == null || ((b == 13 || b == 10) && logFile.length() >= maxFileSize * 1048576)) {
                        // Close current file
                        if (stream != null) {
                            stream.close();
                            archiveLogFile();
                        }

                        // Open new file
                        stream = new FileOutputStream(logFile, true);
                    }

                    stream.write(b);
                }
            }

        });
    }

    @Override
    public void println(String str) {
        super.println(timestampFormat.format(new Date()) + " - " + str);
    }
}
