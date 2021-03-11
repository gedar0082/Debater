package com.gedar0082.debater.util;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;

import de.blox.graphview.Graph;
import de.blox.graphview.Layout;
import de.blox.graphview.Node;
import de.blox.graphview.edgerenderer.EdgeRenderer;
import de.blox.graphview.edgerenderer.StraightEdgeRenderer;
import de.blox.graphview.util.Size;

public class NoOpAlgorithm implements Layout {
    private Size graphSize = new Size(0, 0);
    private EdgeRenderer edgeRenderer = new StraightEdgeRenderer(); // or ArrowEdgeRender() or custom implementation

    @Override
    public void drawEdges(@NotNull Canvas canvas, @NotNull Graph graph, @NotNull Paint linePaint) {
        edgeRenderer.render(canvas, graph, linePaint);
    }

    @Override
    public void setEdgeRenderer(@NotNull EdgeRenderer renderer) {
        edgeRenderer = renderer;
    }

    private de.blox.graphview.util.Size calculateGraphSize(Graph graph) {

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;
        for (Node node : graph.getNodes()) {
            left = (int) Math.min(left, node.getX());
            top = (int) Math.min(top, node.getY());
            right = (int) Math.max(right, node.getX() + node.getWidth());
            bottom = (int) Math.max(bottom, node.getY() + node.getHeight());
        }

        return new Size(right - left, bottom - top);
    }

    @NotNull
    @Override
    public Size run(@NotNull Graph graph, float v, float v1) {
        return calculateGraphSize(graph);
    }


}