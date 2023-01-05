/*
 * Copyright 2004-2019 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 *
 * Modified to a minimal version for BioDWH2 graph databases
 */
package org.h2.mvstore;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

/**
 * This utility class contains miscellaneous functions.
 */
public class Utils {

    /**
     * An 0-size byte array.
     */
    public static final byte[] EMPTY_BYTES = {};

    private Utils() {
        // utility class
    }

    /**
     * Create an array of bytes with the given size. If this is not possible because not enough memory is available, an
     * OutOfMemoryError with the requested size in the message is thrown.
     * <p>
     * This method should be used if the size of the array is user defined, or stored in a file, so wrong size data can
     * be distinguished from regular out-of-memory.
     * </p>
     *
     * @param len the number of bytes requested
     * @return the byte array
     * @throws OutOfMemoryError if the allocation was too large
     */
    public static byte[] newBytes(int len) {
        if (len == 0) {
            return EMPTY_BYTES;
        }
        try {
            return new byte[len];
        } catch (OutOfMemoryError e) {
            Error e2 = new OutOfMemoryError("Requested memory: " + len);
            e2.initCause(e);
            throw e2;
        }
    }

    /**
     * Get the system property. If the system property is not set, or if a security exception occurs, the default value
     * is returned.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the value
     */
    public static String getProperty(String key, String defaultValue) {
        try {
            return System.getProperty(key, defaultValue);
        } catch (SecurityException se) {
            return defaultValue;
        }
    }

    /**
     * Scale the value with the available memory. If 1 GB of RAM is available, the value is returned, if 2 GB are
     * available, then twice the value, and so on.
     *
     * @param value the value to scale
     * @return the scaled value
     */
    public static int scaleForAvailableMemory(int value) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory != Long.MAX_VALUE) {
            // we are limited by an -XmX parameter
            return (int) (value * maxMemory / (1024 * 1024 * 1024));
        }
        try {
            OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
            // this method is only available on the class
            // com.sun.management.OperatingSystemMXBean, which mxBean
            // is an instance of under the Oracle JDK, but it is not present on
            // Android and other JDK's
            Method method = Class.forName("com.sun.management.OperatingSystemMXBean").getMethod(
                    "getTotalPhysicalMemorySize");
            long physicalMemorySize = ((Number) method.invoke(mxBean)).longValue();
            return (int) (value * physicalMemorySize / (1024 * 1024 * 1024));
        } catch (Exception e) {
            // ignore
        }
        return value;
    }

    /**
     * Round the value up to the next block size. The block size must be a power of two. As an example, using the block
     * size of 8, the following rounding operations are done: 0 stays 0; values 1..8 results in 8, 9..16 results in 16,
     * and so on.
     *
     * @param x                 the value to be rounded
     * @param blockSizePowerOf2 the block size
     * @return the rounded value
     */
    public static int roundUpInt(int x, int blockSizePowerOf2) {
        return (x + blockSizePowerOf2 - 1) & (-blockSizePowerOf2);
    }
}
