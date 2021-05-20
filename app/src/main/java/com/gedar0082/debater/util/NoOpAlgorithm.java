package com.gedar0082.debater.util;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.blox.graphview.Edge;
import de.blox.graphview.Graph;
import de.blox.graphview.Layout;
import de.blox.graphview.Node;
import de.blox.graphview.edgerenderer.EdgeRenderer;
import de.blox.graphview.edgerenderer.StraightEdgeRenderer;
import de.blox.graphview.util.Size;

public class NoOpAlgorithm implements Layout {
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

        return new Size(right - left+400, bottom - top+400);
    }

    @NotNull
    @Override
    public Size run(@NotNull Graph graph, float v, float v1) {

        List<Node> nodes = graph.getNodes();

        nodes.get(0).setX(nodes.get(0).getWidth() + 100);
        nodes.get(0).setY(0F);
        float verticalLevel = nodes.get(0).getHeight() + 100F;

        for (int i = 1; i < nodes.size(); i++){
            if (i < 3){
                if (i == 1){
                    nodes.get(i).setX(nodes.get(0).getX()-nodes.get(0).getWidth());
                    nodes.get(i).setY(verticalLevel);
                }else{
                    nodes.get(i).setX(nodes.get(0).getX()+nodes.get(0).getWidth());
                    nodes.get(i).setY(nodes.get(i-1).getY());
                }
            }else{
                if (i%2==1){
                    nodes.get(i).setX(nodes.get(0).getX()-nodes.get(0).getWidth());
                    nodes.get(i).setY(nodes.get(i-1).getY()+(nodes.get(i-1).getHeight()) + 100F);
                }else{
                    nodes.get(i).setX(nodes.get(0).getX()+nodes.get(0).getWidth());
                    nodes.get(i).setY(nodes.get(i-1).getY());
                }
            }
        }
        Node[] ar = new Node[nodes.size()];
        for(int i = 0; i < nodes.size(); i++){
            ar[i] = nodes.get(i);
        }

        Edge[] ed = new Edge[nodes.size()-1];
        for (int i = 0; i < nodes.size()-1; i++){
            ed[i] = new Edge(ar[i], ar[i+1]);
        }

        Graph newGraph = new Graph();
//        newGraph.addNodes((Node[]) nodes.toArray());//dont know
        newGraph.addNodes(ar);

        newGraph.addEdges(ed);

        return calculateGraphSize(newGraph);
    }


}