package de.unibi.agbi.biodwh2.core.math;

public final class Vector2D {
    public final double x;
    public final double y;

    public Vector2D(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        final double oneOverLength = 1.0 / length();
        return new Vector2D(x * oneOverLength, y * oneOverLength);
    }

    public Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    public Vector2D add(final double x, final double y) {
        return new Vector2D(this.x + x, this.y + y);
    }

    public Vector2D add(final Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(final double x, final double y) {
        return new Vector2D(this.x - x, this.y - y);
    }

    public Vector2D subtract(final Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D multiply(final double x, final double y) {
        return new Vector2D(this.x * x, this.y * y);
    }

    @SuppressWarnings("unused")
    public Vector2D multiply(final Vector2D other) {
        return new Vector2D(x * other.x, y * other.y);
    }

    public Vector2D rotate(final double radians) {
        return new Vector2D(Math.cos(radians) * x - Math.sin(radians) * y,
                            Math.sin(radians) * x + Math.cos(radians) * y);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
