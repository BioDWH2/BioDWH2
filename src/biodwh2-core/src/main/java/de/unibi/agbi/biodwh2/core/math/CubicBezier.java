package de.unibi.agbi.biodwh2.core.math;

public final class CubicBezier {
    public final Vector2D from;
    public final Vector2D to;
    public final Vector2D controlPoint1;
    public final Vector2D controlPoint2;

    public CubicBezier(final Vector2D from, final Vector2D controlPoint1, final Vector2D controlPoint2,
                       final Vector2D to) {
        this.from = from;
        this.to = to;
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
    }

    public Vector2D pointAt(final double t) {
        return new Vector2D((1 - t) * (1 - t) * (1 - t) * from.x + 3 * (1 - t) * (1 - t) * t * controlPoint1.x +
                            3 * (1 - t) * t * t * controlPoint2.x + t * t * t * to.x,
                            (1 - t) * (1 - t) * (1 - t) * from.y + 3 * (1 - t) * (1 - t) * t * controlPoint1.y +
                            3 * (1 - t) * t * t * controlPoint2.y + t * t * t * to.y);
    }
}
