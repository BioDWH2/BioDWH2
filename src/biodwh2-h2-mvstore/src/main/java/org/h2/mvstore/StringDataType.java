/*
 * Copyright 2004-2019 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 *
 * Modified to a minimal version for BioDWH2 graph databases
 */
package org.h2.mvstore;

import java.nio.ByteBuffer;

/**
 * A string type.
 */
public class StringDataType implements DataType {
    public static final StringDataType INSTANCE = new StringDataType();

    @Override
    public int compare(Object a, Object b) {
        return a.toString().compareTo(b.toString());
    }

    @Override
    public int getMemory(Object obj) {
        return 24 + 2 * obj.toString().length();
    }

    @Override
    public void read(ByteBuffer buff, Object[] obj, int len, boolean key) {
        for (int i = 0; i < len; i++) {
            obj[i] = read(buff);
        }
    }

    @Override
    public void write(WriteBuffer buff, Object[] obj, int len, boolean key) {
        for (int i = 0; i < len; i++) {
            write(buff, obj[i]);
        }
    }

    @Override
    public String read(ByteBuffer buff) {
        return DataUtils.readString(buff);
    }

    @Override
    public void write(WriteBuffer buff, Object obj) {
        String s = obj.toString();
        int len = s.length();
        buff.putVarInt(len).putStringData(s, len);
    }
}
