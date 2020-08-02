package de.unibi.agbi.biodwh2.core.model;

import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {
    private static final String SEPARATOR = ".";

    private int major;
    private int minor;
    private int build = -1;
    private int revision = -1;

    public Version(final int major, final int minor, final int build, final int revision) {
        this(major, minor, build);
        if (revision < 0)
            throw new IllegalArgumentException("revision version must be greater or equal zero");
        this.revision = revision;
    }

    public Version(final int major, final int minor, final int build) {
        this(major, minor);
        if (build < 0)
            throw new IllegalArgumentException("build version must be greater or equal zero");
        this.build = build;
    }

    public Version(final int major, final int minor) {
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
    public int compareTo(final Version v) {
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
    public boolean equals(final Object o) {
        if (!(o instanceof Version))
            return false;
        final Version v = (Version) o;
        return this.major == v.major && this.minor == v.minor && this.build == v.build && this.revision == v.revision;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder versionString = new StringBuilder();
        versionString.append(major).append(SEPARATOR).append(minor);
        if (build != -1)
            versionString.append(SEPARATOR).append(build);
        if (revision != -1)
            versionString.append(SEPARATOR).append(revision);
        return versionString.toString();
    }

    public static Version tryParse(final String input) {
        try {
            return parse(input);
        } catch (NullPointerException | NumberFormatException ignored) {
            return null;
        }
    }

    public static Version parse(final String input) {
        if (input == null)
            throw new NullPointerException("input must not be null");
        final String[] versionStringParts = input.split(Pattern.quote(SEPARATOR));
        if (versionStringParts.length < 2 || versionStringParts.length > 4)
            throw new NumberFormatException("version string requires at least two and at most four parts");
        final int major = Integer.parseInt(versionStringParts[0]);
        final int minor = Integer.parseInt(versionStringParts[1]);
        if (versionStringParts.length > 2) {
            final int build = Integer.parseInt(versionStringParts[2]);
            if (versionStringParts.length > 3) {
                final int revision = Integer.parseInt(versionStringParts[3]);
                return new Version(major, minor, build, revision);
            }
            return new Version(major, minor, build);
        }
        return new Version(major, minor);
    }
}
