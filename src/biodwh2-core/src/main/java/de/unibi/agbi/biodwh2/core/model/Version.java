package de.unibi.agbi.biodwh2.core.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {
    private static final String SEPARATOR = ".";

    public final int major;
    public final int minor;
    public final int build;
    public final int revision;

    private Version() {
        major = -1;
        minor = -1;
        build = -1;
        revision = -1;
    }

    public Version(final int major, final int minor) {
        checkComponentNonNegative(major, "major");
        checkComponentNonNegative(minor, "minor");
        this.major = major;
        this.minor = minor;
        build = -1;
        revision = -1;
    }

    private void checkComponentNonNegative(final int value, final String name) {
        if (value < 0)
            throw new IllegalArgumentException(name + " version must be greater or equal zero");
    }

    public Version(final int major, final int minor, final int build) {
        checkComponentNonNegative(major, "major");
        checkComponentNonNegative(minor, "minor");
        checkComponentNonNegative(build, "build");
        this.major = major;
        this.minor = minor;
        this.build = build;
        revision = -1;
    }

    public Version(final int major, final int minor, final int build, final int revision) {
        checkComponentNonNegative(major, "major");
        checkComponentNonNegative(minor, "minor");
        checkComponentNonNegative(build, "build");
        checkComponentNonNegative(revision, "revision");
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
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
        Objects.requireNonNull(input, "input must not be null");
        final int[] components = convertStringToIntegerComponents(input);
        return parse(components);
    }

    private static int[] convertStringToIntegerComponents(final String input) {
        final String[] versionStringParts = input.split(Pattern.quote(SEPARATOR));
        return Arrays.stream(versionStringParts).mapToInt(Integer::parseInt).toArray();
    }

    private static Version parse(final int[] components) {
        switch (components.length) {
            case 2:
                return new Version(components[0], components[1]);
            case 3:
                return new Version(components[0], components[1], components[2]);
            case 4:
                return new Version(components[0], components[1], components[2], components[3]);
            default:
                throw new NumberFormatException("version string requires at least two and at most four parts");
        }
    }
}
