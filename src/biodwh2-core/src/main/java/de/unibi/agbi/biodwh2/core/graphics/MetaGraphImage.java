package de.unibi.agbi.biodwh2.core.graphics;

import de.unibi.agbi.biodwh2.core.math.CubicBezier;
import de.unibi.agbi.biodwh2.core.math.Vector2D;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaEdge;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public final class MetaGraphImage {
    private static class MetaNodeLayout {
        Color color;
        String label;
        double x;
        double y;
        double displacementX;
        double displacementY;
    }

    private static class MetaEdgeLayout {
        String fromLabel;
        String toLabel;
        String label;
        CubicBezier line;
    }

    private static final int MULTI_EDGE_SPACING = 40;
    private static final int ARROW_TIP_LENGTH = 10;
    private static final int NODE_SIZE = 50;
    private static final int NODE_SIZE_HALF = NODE_SIZE / 2;
    private static final int NODE_BORDER_WIDTH = 3;
    private static final double COOLING_RATE = 0.25;
    private static final double CRITERION = 15;
    private static final double C = 0.4;
    private final int width;
    private final int height;
    private final Map<String, MetaNodeLayout> nodes;
    private final List<MetaEdgeLayout> edges;
    private final Map<String, List<MetaEdgeLayout>> edgesPerNodePair;
    private double k = 0;
    private double t = 0;
    private int cropRectMinX;
    private int cropRectMinY;
    private int cropRectMaxX;
    private int cropRectMaxY;

    public MetaGraphImage(final MetaGraph graph, final int width, final int height) {
        this.width = width;
        this.height = height;
        nodes = new HashMap<>();
        edges = new ArrayList<>();
        edgesPerNodePair = new HashMap<>();
        int currentColor = 0;
        for (final MetaNode node : graph.getNodes()) {
            final MetaNodeLayout layout = new MetaNodeLayout();
            layout.label = node.label;
            layout.color = Color.getHSBColor(currentColor / (float) graph.getNodeLabelCount(), 0.85f, 1.0f);
            currentColor++;
            nodes.put(node.label, layout);
        }
        for (final MetaEdge edge : graph.getEdges()) {
            final MetaEdgeLayout metaEdge = new MetaEdgeLayout();
            metaEdge.fromLabel = edge.fromLabel;
            metaEdge.toLabel = edge.toLabel;
            metaEdge.label = edge.label;
            edges.add(metaEdge);
            final String nodePairKey = edge.fromLabel.compareTo(edge.toLabel) > 0 ?
                                       edge.toLabel + "|" + edge.fromLabel : edge.fromLabel + "|" + edge.toLabel;
            if (!edgesPerNodePair.containsKey(nodePairKey))
                edgesPerNodePair.put(nodePairKey, new ArrayList<>());
            edgesPerNodePair.get(nodePairKey).add(metaEdge);
        }
        calculateNodeLocations();
        calculateEdgeLocations();
    }

    /**
     * Force directed layout adapted from https://github.com/Benjoyo/ForceDirectedPlacement/
     */
    private void calculateNodeLocations() {
        float area = Math.min(width * width, height * height);
        k = C * Math.sqrt(area / nodes.size());
        t = width * 0.1;
        final Random rand = new Random();
        for (final MetaNodeLayout node : nodes.values()) {
            node.x = rand.nextInt(width);
            node.y = rand.nextInt(height);
        }
        boolean equilibriumReached = false;
        int iteration = 0;
        while (!equilibriumReached && iteration < 1000) {
            equilibriumReached = simulateForceDirectedStep();
            iteration++;
        }
    }

    private boolean simulateForceDirectedStep() {
        for (final MetaNodeLayout v : nodes.values()) {
            v.displacementX = 0;
            v.displacementY = 0;
            // Light attraction to center of canvas
            final double centerOffsetX = (width * 0.5) - v.x;
            final double centerOffsetY = (height * 0.5) - v.y;
            final double distanceFromCenter = vectorLength(centerOffsetX, centerOffsetY);
            final double centerAttraction = forceAttractive(distanceFromCenter, k);
            v.displacementX += centerOffsetX * (1 / distanceFromCenter) * centerAttraction * 0.25;
            v.displacementY += centerOffsetY * (1 / distanceFromCenter) * centerAttraction * 0.25;
            // Repulsion from other nodes
            for (final MetaNodeLayout u : nodes.values()) {
                if (v.equals(u))
                    continue;
                double deltaPosX = v.x - u.x;
                double deltaPosY = v.y - u.y;
                double length = vectorLength(deltaPosX, deltaPosY);
                double repulsiveForce = forceRepulsive(length, k);
                deltaPosX *= (1 / length) * repulsiveForce;
                deltaPosY *= (1 / length) * repulsiveForce;
                v.displacementX += deltaPosX;
                v.displacementY += deltaPosY;
            }
        }
        // Attraction between connected nodes
        for (final MetaEdgeLayout e : edges) {
            final MetaNodeLayout v = nodes.get(e.fromLabel);
            final MetaNodeLayout u = nodes.get(e.toLabel);
            if (v.equals(u))
                continue;
            double deltaPosX = v.x - u.x;
            double deltaPosY = v.y - u.y;
            double length = vectorLength(deltaPosX, deltaPosY);
            double attractiveForce = forceAttractive(length, k);
            deltaPosX *= (1 / length) * attractiveForce;
            deltaPosY *= (1 / length) * attractiveForce;
            nodes.get(e.fromLabel).displacementX -= deltaPosX;
            nodes.get(e.fromLabel).displacementY -= deltaPosY;
            nodes.get(e.toLabel).displacementX += deltaPosX;
            nodes.get(e.toLabel).displacementY += deltaPosY;
        }
        boolean equilibriumReached = true;
        for (final MetaNodeLayout v : nodes.values()) {
            double displacementX = v.displacementX;
            double displacementY = v.displacementY;
            double length = vectorLength(displacementX, displacementY);
            if (length > CRITERION)
                equilibriumReached = false;
            displacementX *= (1 / length) * Math.min(length, t);
            displacementY *= (1 / length) * Math.min(length, t);
            v.x += displacementX;
            v.y += displacementY;
            v.x = Math.min(width - NODE_SIZE - 10, Math.max(10.0, v.x));
            v.y = Math.min(height - NODE_SIZE - 10, Math.max(10.0, v.y));
        }
        t = Math.max(t * (1 - COOLING_RATE), 1);
        return equilibriumReached;
    }

    private double vectorLength(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    private double forceAttractive(double d, double k) {
        return d < 200 ? 0 : (d * d) / k;
    }

    private double forceRepulsive(double d, double k) {
        return (k * k) / d;
    }

    private void calculateEdgeLocations() {
        for (final String nodePairKey : edgesPerNodePair.keySet()) {
            final List<MetaEdgeLayout> pairEdges = edgesPerNodePair.get(nodePairKey);
            final double allEdgesSizeHalf = (pairEdges.size() - 1) * MULTI_EDGE_SPACING * 0.5;
            final MetaNodeLayout firstNode = nodes.get(pairEdges.get(0).fromLabel);
            final MetaNodeLayout secondNode = nodes.get(pairEdges.get(0).toLabel);
            final Vector2D firstVector = new Vector2D(firstNode.x, firstNode.y);
            final Vector2D secondVector = new Vector2D(secondNode.x, secondNode.y);
            final Vector2D direction = secondVector.subtract(firstVector).normalize();
            final Vector2D normal = direction.rotate(Math.PI * 0.5);
            final Vector2D normalOffset = normal.multiply(allEdgesSizeHalf, allEdgesSizeHalf);
            final int nodeCenterOffset = NODE_SIZE_HALF + NODE_BORDER_WIDTH;
            int edgeOffsetIndex = 0;
            for (final MetaEdgeLayout edge : pairEdges) {
                final MetaNodeLayout fromNode = nodes.get(edge.fromLabel);
                final MetaNodeLayout toNode = nodes.get(edge.toLabel);
                final Vector2D lineFrom = new Vector2D(fromNode.x, fromNode.y).add(NODE_SIZE_HALF, NODE_SIZE_HALF);
                final Vector2D lineTo = new Vector2D(toNode.x, toNode.y).add(NODE_SIZE_HALF, NODE_SIZE_HALF);
                final Vector2D controlPoint1;
                final Vector2D controlPoint2;
                if (fromNode.equals(toNode)) {
                    controlPoint1 = lineFrom.subtract(50, 80);
                    controlPoint2 = lineTo.subtract(-50, 80);
                } else {
                    final Vector2D arrowSource = lineFrom.add(direction.multiply(nodeCenterOffset, nodeCenterOffset));
                    final Vector2D arrowTarget = lineTo.subtract(
                            direction.multiply(nodeCenterOffset, nodeCenterOffset));
                    final Vector2D offset = normal.multiply(edgeOffsetIndex * MULTI_EDGE_SPACING,
                                                            edgeOffsetIndex * MULTI_EDGE_SPACING);
                    controlPoint1 = arrowSource.subtract(normalOffset).add(offset);
                    controlPoint2 = arrowTarget.subtract(normalOffset).add(offset);
                }
                edge.line = new CubicBezier(lineFrom, controlPoint1, controlPoint2, lineTo);
                edgeOffsetIndex++;
            }
        }
    }

    public void drawAndSaveImage(final Path outputFilePath) {
        cropRectMinX = width;
        cropRectMinY = height;
        cropRectMaxX = 0;
        cropRectMaxY = 0;
        try {
            final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = (Graphics2D) image.getGraphics();
            g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            drawEdgeLines(g);
            final Font nodeFont = new Font("Arial", Font.PLAIN, 10);
            final FontMetrics nodeFontMetrics = g.getFontMetrics(nodeFont);
            drawNodeCircles(g, nodeFont, nodeFontMetrics);
            final Font edgeFont = new Font("Arial", Font.PLAIN, 8);
            final FontMetrics edgeFontMetrics = g.getFontMetrics(edgeFont);
            drawEdgeLabels(g, edgeFont, edgeFontMetrics);
            cropRectMinX = Math.max(0, cropRectMinX - 10);
            cropRectMinY = Math.max(0, cropRectMinY - 10);
            cropRectMaxX = Math.min(width, cropRectMaxX + 10);
            cropRectMaxY = Math.min(height, cropRectMaxY + 10);
            final BufferedImage croppedImage = image.getSubimage(cropRectMinX, cropRectMinY,
                                                                 cropRectMaxX - cropRectMinX,
                                                                 cropRectMaxY - cropRectMinY);
            ImageIO.write(croppedImage, "png", outputFilePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawEdgeLines(final Graphics2D g) {
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLACK);
        for (final MetaEdgeLayout edge : edges) {
            final CubicCurve2D shape = new CubicCurve2D.Double();
            shape.setCurve(edge.line.from.x, edge.line.from.y, edge.line.controlPoint1.x, edge.line.controlPoint1.y,
                           edge.line.controlPoint2.x, edge.line.controlPoint2.y, edge.line.to.x, edge.line.to.y);
            g.draw(shape);
            double tipPointOffset = 1.0;
            double distance;
            do {
                tipPointOffset -= 0.01;
                final Vector2D point = edge.line.pointAt(tipPointOffset);
                distance = edge.line.to.subtract(point).length();
            } while (tipPointOffset > 0 && distance < NODE_SIZE_HALF);
            final Vector2D tipPointStart = edge.line.pointAt(tipPointOffset - 0.01);
            final Vector2D tipPointEnd = edge.line.pointAt(tipPointOffset);
            final double angle = Math.toRadians(30);
            final Vector2D dir = tipPointEnd.subtract(tipPointStart).normalize();
            final Vector2D arrowLeftEnd = dir.negate().rotate(angle);
            final Vector2D arrowRightEnd = dir.negate().rotate(-angle);
            g.drawLine((int) tipPointEnd.x, (int) tipPointEnd.y,
                       (int) (tipPointEnd.x + arrowLeftEnd.x * ARROW_TIP_LENGTH),
                       (int) (tipPointEnd.y + arrowLeftEnd.y * ARROW_TIP_LENGTH));
            g.drawLine((int) tipPointEnd.x, (int) tipPointEnd.y,
                       (int) (tipPointEnd.x + arrowRightEnd.x * ARROW_TIP_LENGTH),
                       (int) (tipPointEnd.y + arrowRightEnd.y * ARROW_TIP_LENGTH));
        }
    }

    private void drawNodeCircles(final Graphics2D g, final Font font, final FontMetrics metrics) {
        g.setStroke(new BasicStroke(NODE_BORDER_WIDTH));
        g.setFont(font);
        for (final MetaNodeLayout node : nodes.values()) {
            g.setColor(node.color);
            g.fillOval((int) node.x, (int) node.y, NODE_SIZE, NODE_SIZE);
            g.setColor(node.color.darker());
            g.drawOval((int) node.x, (int) node.y, NODE_SIZE, NODE_SIZE);
            final int stringWidth = metrics.stringWidth(node.label);
            final int textX = (int) node.x + (NODE_SIZE - stringWidth) / 2;
            final int textY = (int) node.y + ((NODE_SIZE - metrics.getHeight()) / 2) + metrics.getAscent();
            drawLabelWithOutline(g, font, node.label, textX, textY);
            updateCropRectangle((int) node.x, (int) node.y);
            updateCropRectangle((int) node.x + NODE_SIZE, (int) node.y + NODE_SIZE);
            updateCropRectangle(textX, textY);
            updateCropRectangle(textX + stringWidth, textY + metrics.getHeight() + metrics.getAscent());
        }
    }

    private void drawLabelWithOutline(final Graphics2D g, final Font font, final String text, final int x,
                                      final int y) {
        final AffineTransform transform = g.getTransform();
        g.translate(x, y);
        final FontRenderContext frc = g.getFontRenderContext();
        final TextLayout tl = new TextLayout(text, font, frc);
        final Shape shape = tl.getOutline(null);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.draw(shape);
        g.setColor(Color.BLACK);
        g.fill(shape);
        g.setTransform(transform);
    }

    private void updateCropRectangle(double x, double y) {
        updateCropRectangle((int) x, (int) y);
    }

    private void updateCropRectangle(int x, int y) {
        cropRectMinX = Math.min(cropRectMinX, x);
        cropRectMinY = Math.min(cropRectMinY, y);
        cropRectMaxX = Math.max(cropRectMaxX, x);
        cropRectMaxY = Math.max(cropRectMaxY, y);
    }

    private void drawEdgeLabels(final Graphics2D g, final Font font, final FontMetrics metrics) {
        g.setFont(font);
        for (final MetaEdgeLayout edge : edges) {
            final Vector2D halfPoint = edge.line.pointAt(0.5);
            final Vector2D direction = edge.line.to.subtract(edge.line.from).normalize();
            final Vector2D normal = direction.rotate(Math.PI * 0.5);
            final Vector2D textCenter;
            if (edge.fromLabel.equals(edge.toLabel))
                textCenter = new Vector2D(halfPoint.x, halfPoint.y);
            else
                textCenter = new Vector2D(halfPoint.x, halfPoint.y).add(normal.multiply(10, 10));
            final int stringWidth = metrics.stringWidth(edge.label);
            double rotation;
            if (edge.fromLabel.equals(edge.toLabel)) {
                final Vector2D selfEdgeNormal = edge.line.pointAt(0.5).subtract(edge.line.from);
                rotation = Math.atan2(selfEdgeNormal.x, selfEdgeNormal.y);
            } else
                rotation = Math.atan2(edge.line.to.y - edge.line.from.y, edge.line.to.x - edge.line.from.x);
            rotation = (rotation + 3 * Math.PI) % (Math.PI * 2);
            if (rotation > Math.PI * 0.5 && rotation < Math.PI * 1.5)
                rotation += Math.PI;
            final AffineTransform previousTransform = g.getTransform();
            g.translate((int) textCenter.x, (int) textCenter.y);
            g.rotate(rotation);
            drawLabelWithOutline(g, font, edge.label, (int) (-stringWidth * 0.5), (int) (metrics.getHeight() * 0.5));
            g.setTransform(previousTransform);
            updateCropRectangle(textCenter.x, textCenter.y);
            updateCropRectangle(textCenter.x + stringWidth, textCenter.y + metrics.getHeight() + metrics.getAscent());
        }
    }
}
