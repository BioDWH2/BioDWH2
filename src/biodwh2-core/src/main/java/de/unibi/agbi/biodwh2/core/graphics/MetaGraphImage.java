package de.unibi.agbi.biodwh2.core.graphics;

import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaEdge;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class MetaGraphImage {
    private static class MetaNodeLayout {
        Color color;
        String[] labels;
        double x;
        double y;
        double displacementX;
        double displacementY;
    }

    private static final int NODE_SIZE = 50;
    private static final int NODE_SIZE_HALF = NODE_SIZE / 2;
    private static final double COOLING_RATE = 0.25;
    private static final double CRITERION = 15;
    private static final double C = 0.4;
    private final int width;
    private final int height;
    private final Map<String, MetaNodeLayout> nodes;
    private final Map<String, MetaEdge> edges;
    private double k = 0;
    private double t = 0;
    private int cropRectMinX;
    private int cropRectMinY;
    private int cropRectMaxX;
    private int cropRectMaxY;

    public MetaGraphImage(final MetaGraph graph) {
        this(graph, 512, 512);
    }

    public MetaGraphImage(final MetaGraph graph, final int width, final int height) {
        this.width = width;
        this.height = height;
        nodes = new HashMap<>();
        edges = new HashMap<>();
        int currentColor = 0;
        for (final MetaNode node : graph.getNodes()) {
            final MetaNodeLayout layout = new MetaNodeLayout();
            layout.labels = node.labels;
            layout.color = Color.getHSBColor(currentColor / (float) graph.getNodeCount(), 0.85f, 1.0f);
            currentColor++;
            nodes.put(node.id, layout);
        }
        for (final MetaEdge edge : graph.getEdges()) {
            final MetaEdge metaEdge = new MetaEdge();
            metaEdge.fromId = edge.fromId;
            metaEdge.toId = edge.toId;
            metaEdge.label = edge.label;
            metaEdge.id = edge.id;
            edges.put(edge.id, metaEdge);
        }
        calculateNodeLocations();
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
        for (final MetaEdge e : edges.values()) {
            final MetaNodeLayout v = nodes.get(e.fromId);
            final MetaNodeLayout u = nodes.get(e.toId);
            double deltaPosX = v.x - u.x;
            double deltaPosY = v.y - u.y;
            double length = vectorLength(deltaPosX, deltaPosY);
            double attractiveForce = forceAttractive(length, k);
            deltaPosX *= (1 / length) * attractiveForce;
            deltaPosY *= (1 / length) * attractiveForce;
            nodes.get(e.fromId).displacementX -= deltaPosX;
            nodes.get(e.fromId).displacementY -= deltaPosY;
            nodes.get(e.toId).displacementX += deltaPosX;
            nodes.get(e.toId).displacementY += deltaPosY;
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

    public void drawAndSaveImage(final String outputFilePath) {
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
            final Font font = new Font("Arial", Font.PLAIN, 12);
            FontMetrics metrics = g.getFontMetrics(font);
            drawEdgeLines(g);
            drawNodeCircles(g, font, metrics);
            drawEdgeLabels(g, font, metrics);
            cropRectMinX = Math.max(0, cropRectMinX - 10);
            cropRectMinY = Math.max(0, cropRectMinY - 10);
            cropRectMaxX = Math.min(width, cropRectMaxX + 10);
            cropRectMaxY = Math.min(height, cropRectMaxY + 10);
            final BufferedImage croppedImage = image.getSubimage(cropRectMinX, cropRectMinY,
                                                                 cropRectMaxX - cropRectMinX,
                                                                 cropRectMaxY - cropRectMinY);
            ImageIO.write(croppedImage, "png", new File(outputFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawEdgeLines(final Graphics2D g) {
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLACK);
        for (final MetaEdge edge : edges.values()) {
            final MetaNodeLayout fromNode = nodes.get(edge.fromId);
            final MetaNodeLayout toNode = nodes.get(edge.toId);
            g.drawLine((int) fromNode.x + NODE_SIZE_HALF, (int) fromNode.y + NODE_SIZE_HALF,
                       (int) toNode.x + NODE_SIZE_HALF, (int) toNode.y + NODE_SIZE_HALF);
        }
    }

    private void drawNodeCircles(final Graphics2D g, final Font font, final FontMetrics metrics) {
        g.setStroke(new BasicStroke(3));
        g.setFont(font);
        for (final MetaNodeLayout node : nodes.values()) {
            g.setColor(node.color);
            g.fillOval((int) node.x, (int) node.y, NODE_SIZE, NODE_SIZE);
            g.setColor(node.color.darker());
            g.drawOval((int) node.x, (int) node.y, NODE_SIZE, NODE_SIZE);
            g.setColor(Color.BLACK);
            final String label = String.join("\n", node.labels);
            final int stringWidth = metrics.stringWidth(label);
            int textX = (int) node.x + (NODE_SIZE - stringWidth) / 2;
            int textY = (int) node.y + ((NODE_SIZE - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(label, textX, textY);
            updateCropRectangle((int) node.x, (int) node.y);
            updateCropRectangle((int) node.x + NODE_SIZE, (int) node.y + NODE_SIZE);
            updateCropRectangle(textX, textY);
            updateCropRectangle(textX + stringWidth, textY + metrics.getHeight() + metrics.getAscent());
        }
    }

    private void updateCropRectangle(int x, int y) {
        cropRectMinX = Math.min(cropRectMinX, x);
        cropRectMinY = Math.min(cropRectMinY, y);
        cropRectMaxX = Math.max(cropRectMaxX, x);
        cropRectMaxY = Math.max(cropRectMaxY, y);
    }

    private void drawEdgeLabels(final Graphics2D g, final Font font, final FontMetrics metrics) {
        g.setFont(font);
        for (final MetaEdge edge : edges.values()) {
            final MetaNodeLayout fromNode = nodes.get(edge.fromId);
            final MetaNodeLayout toNode = nodes.get(edge.toId);
            final int stringWidth = metrics.stringWidth(edge.label);
            final double textX = fromNode.x + NODE_SIZE_HALF + (toNode.x - fromNode.x) / 2 - (stringWidth * 0.5);
            final double textY = fromNode.y + NODE_SIZE_HALF + (toNode.y - fromNode.y) / 2;
            GlyphVector glyphVector = font.createGlyphVector(g.getFontRenderContext(), edge.label);
            Shape textShape = glyphVector.getOutline((int) textX, (int) textY);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(4));
            g.draw(textShape);
            g.setColor(Color.DARK_GRAY);
            g.drawString(edge.label, (int) textX, (int) textY);
            updateCropRectangle((int) textX, (int) textY);
            updateCropRectangle((int) textX + stringWidth, (int) textY + metrics.getHeight() + metrics.getAscent());
        }
    }
}
