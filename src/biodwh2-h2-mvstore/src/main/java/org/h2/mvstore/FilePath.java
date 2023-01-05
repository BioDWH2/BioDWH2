/*
 * Copyright 2004-2019 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 *
 * Modified to a minimal version for BioDWH2 graph databases
 */
package org.h2.mvstore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FilePath {
    /**
     * System property <code>user.home</code> (empty string if not set).<br /> It is usually set by the system, and used
     * as a replacement for ~ in file names.
     */
    public static final String USER_HOME = Utils.getProperty("user.home", "");

    /**
     * The complete path (which may be absolute or relative, depending on the file system).
     */
    private final String name;

    FilePath(String fileName) {
        fileName = fileName.replace('\\', '/');
        if (fileName.startsWith("file:")) {
            fileName = fileName.substring("file:".length());
        }
        this.name = expandUserHomeDirectory(fileName);
    }

    /**
     * Expand '~' to the user home directory. It is only be expanded if the '~' stands alone, or is followed by '/' or
     * '\'.
     *
     * @param fileName the file name
     * @return the native file name
     */
    public static String expandUserHomeDirectory(String fileName) {
        if (fileName.startsWith("~") && (fileName.length() == 1 || fileName.startsWith("~/"))) {
            fileName = USER_HOME + fileName.substring(1);
        }
        return fileName;
    }

    public FileChannel open(String mode) throws IOException {
        return new FileBase(name, mode);
    }

    public boolean canWrite() {
        File file = new File(name);
        try {
            if (!file.canWrite()) {
                return false;
            }
        } catch (Exception e) {
            // workaround for GAE which throws a java.security.AccessControlException
            return false;
        }
        // File.canWrite() does not respect Windows user permissions, so we must try to open it using the mode "rw".
        // See also http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4420020
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(file, "rw");
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public boolean exists() {
        return new File(name).exists();
    }

    public FilePath getParent() {
        String p = new File(name).getParent();
        return p == null ? null : new FilePath(p);
    }
}
