package de.unibi.agbi.biodwh2.core.model;

import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {
    private static final String SEPARATOR = ".";

    private int major;
    private int minor;
    private int build = -1;
    private int revision = -1;

    public Version(int major, int minor, int build, int revision) {
        this(major, minor, build);
        if (revision < 0)
            throw new IllegalArgumentException("revision version must be greater or equal zero");
        this.revision = revision;
    }

    public Version(int major, int minor, int build) {
        this(major, minor);
        if (build < 0)
            throw new IllegalArgumentException("build version must be greater or equal zero");
        this.build = build;
    }

    public Version(int major, int minor) {
        if (major < 0)
            throw new IllegalArgumentException("major version must be greater or equal zero");
        if (minor < 0)
            throw new IllegalArgumentException("minor version must be greater or equal zero");
        this.major = major;
        this.minor = minor;
    }

    private Version() {
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getBuild() {
        return build;
    }

    public int getRevision() {
        return revision;
    }

    @Override
    public int compareTo(Version v) {
        if (v == null)
            return 1;
        if (major != v.major)
            return major > v.major ? 1 : -1;
        if (minor != v.minor)
            return minor > v.minor ? 1 : -1;
        if (build != v.build)
            return build > v.build ? 1 : -1;
        if (revision != v.revision)
            return revision > v.revision ? 1 : -1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Version))
            return false;
        Version v = (Version) o;
        return this.major == v.major && this.minor == v.minor && this.build == v.build && this.revision == v.revision;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder versionString = new StringBuilder();
        versionString.append(major);
        versionString.append(SEPARATOR).append(minor);
        if (build != -1)
            versionString.append(SEPARATOR).append(build);
        if (revision != -1)
            versionString.append(SEPARATOR).append(revision);
        return versionString.toString();
    }

    public static Version tryParse(String input) {
        try {
            return parse(input);
        } catch (NullPointerException | NumberFormatException ex) {
            // ignore
        }
        return null;
    }

    public static Version parse(String input) throws NullPointerException, NumberFormatException {
        if (input == null)
            throw new NullPointerException("input must not be null");
        String[] versionStringParts = input.split(Pattern.quote(SEPARATOR));
        if (versionStringParts.length < 2 || versionStringParts.length > 4)
            throw new NumberFormatException("version string requires at least two and at most four parts");
        int major = Integer.parseInt(versionStringParts[0]);
        int minor = Integer.parseInt(versionStringParts[1]);
        if (versionStringParts.length > 2) {
            int build = Integer.parseInt(versionStringParts[2]);
            if (versionStringParts.length > 3) {
                int revision = Integer.parseInt(versionStringParts[3]);
                return new Version(major, minor, build, revision);
            }
            return new Version(major, minor, build);
        }
        return new Version(major, minor);
    }
}
