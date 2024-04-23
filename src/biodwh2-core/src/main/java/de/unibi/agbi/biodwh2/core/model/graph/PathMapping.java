package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.ArrayList;
import java.util.List;

public final class PathMapping {
    public static final class Segment {
        public final String fromNodeLabel;
        public final String edgeLabel;
        public final String toNodeLabel;
        public final EdgeDirection direction;

        public Segment(final String fromNodeLabel, final String edgeLabel, final String toNodeLabel) {
            this(fromNodeLabel, edgeLabel, toNodeLabel, EdgeDirection.BIDIRECTIONAL);
        }

        public Segment(final String fromNodeLabel, final String edgeLabel, final String toNodeLabel,
                       final EdgeDirection direction) {
            this.fromNodeLabel = fromNodeLabel;
            this.edgeLabel = edgeLabel;
            this.toNodeLabel = toNodeLabel;
            this.direction = direction;
        }
    }

    private final List<Segment> segments;

    public PathMapping() {
        segments = new ArrayList<>();
    }

    public PathMapping add(final String fromNodeLabel, final String edgeLabel, final String toNodeLabel) {
        return add(new Segment(fromNodeLabel, edgeLabel, toNodeLabel));
    }

    public PathMapping add(final String fromNodeLabel, final String edgeLabel, final String toNodeLabel,
                           final EdgeDirection direction) {
        return add(new Segment(fromNodeLabel, edgeLabel, toNodeLabel, direction));
    }

    public PathMapping add(final Segment segment) {
        if (segments.size() > 0) {
            final String previousToLabel = segments.get(segments.size() - 1).toNodeLabel;
            if (!previousToLabel.equals(segment.fromNodeLabel))
                throw new IllegalArgumentException("The segments fromNodeLabel '" + segment.fromNodeLabel +
                                                   "' needs to match the previous segments toNodeLabel '" +
                                                   previousToLabel + "'");
        }
        segments.add(segment);
        return this;
    }

    public int getSegmentCount() {
        return segments.size();
    }

    public Segment get(final int i) {
        return segments.get(i);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            final PathMapping.Segment segment = segments.get(i);
            if (i == 0)
                builder.append("(:").append(segment.fromNodeLabel).append(')');
            if (segment.direction == EdgeDirection.BACKWARD)
                builder.append('<');
            builder.append("-[:").append(segment.edgeLabel).append("]-");
            if (segment.direction == EdgeDirection.FORWARD)
                builder.append('>');
            builder.append("(:").append(segment.toNodeLabel).append(')');
        }
        return builder.toString();
    }

    public String toString(final String labelPrefix) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            final PathMapping.Segment segment = segments.get(i);
            if (i == 0)
                builder.append("(:").append(labelPrefix).append(segment.fromNodeLabel).append(')');
            if (segment.direction == EdgeDirection.BACKWARD)
                builder.append('<');
            builder.append("-[:").append(labelPrefix).append(segment.edgeLabel).append("]-");
            if (segment.direction == EdgeDirection.FORWARD)
                builder.append('>');
            builder.append("(:").append(labelPrefix).append(segment.toNodeLabel).append(')');
        }
        return builder.toString();
    }
}
