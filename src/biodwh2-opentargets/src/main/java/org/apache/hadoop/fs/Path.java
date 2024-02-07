package org.apache.hadoop.fs;

import java.io.ObjectInputValidation;
import java.io.Serializable;

public class Path implements Comparable<Path>, Serializable, ObjectInputValidation {
    private static final long serialVersionUID = 7458630188318482936L;

    @Override
    public void validateObject() {
    }

    @Override
    public int compareTo(Path o) {
        return 0;
    }
}