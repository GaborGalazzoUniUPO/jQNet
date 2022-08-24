package uniupo.valpre.bcnnsim.gui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.node.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

public class NetworkVisualizer {

	public static BufferedImage networkToImg(QueueNetwork network) {

		Graph g = graph("example1").directed()
				.graphAttr().with(Rank.dir(LEFT_TO_RIGHT), attr("splines", "ortho"))
				.nodeAttr().with(Font.name("arial"))
				.linkAttr().with("class", "link-class");
		for (Node n : network.getNodes()) {
			var shape = "box3d";
			var width = 1.0;
			var height = 0.5;
			if (n instanceof Source) {
				shape = "circle";
				width = 0.5;
			} else if (n instanceof Sink) {
				shape = "doublecircle";
				width = 0.5;
			} else if (n instanceof Delay) {
				shape = "rectangle";
				width = 0.5;
				height = 1;
			}
			g = g.with(node(n.getName()).with(attr("shape", shape), attr("width", width), attr("height", height)).link(n.getOutputs().stream().map(Node::getName).toArray(String[]::new)));

		}
		return Graphviz.fromGraph(g).render(Format.PNG).toImage();


	}

	public static void networkToGraph(QueueNetwork currentNetwork, mxGraph graph) {
		var parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
			var nodeMap = new HashMap<String, Object>();
			for (Node node : currentNetwork.getNodes()) {
				var style = "labelPosition=center;verticalLabelPosition=bottom;fontColor=#ffffff;";
				var width = 80;
				var height = 30;

				if (node instanceof Source) {
					style += "rounded=0;whiteSpace=wrap;strokeColor=#ffffff;fillColor=none;strokeWidth=7;";
					width = 30;
				} else if (node instanceof Sink) {
					style += "ellipse;shape=doubleEllipse;whiteSpace=wrap;html=1;aspect=fixed;fontFamily=Helvetica;fontSize=12;strokeColor=none;fillColor=#ffffff;strokeWidth=2;";
					width = 30;
				} else if (node instanceof Delay) {
					style = "shape=rectangle;whiteSpace=wrap;html=1;fontFamily=Helvetica;fontSize=12;fontColor=#ffffff;strokeColor=#ffffff;fillColor=none;strokeWidth=5;";
					height = 80;
					width = 30;
				} else if (node instanceof Queue) {
					style = "shape=rectangle;whiteSpace=wrap;html=1;fontFamily=Helvetica;fontSize=12;fontColor=#ffffff;strokeColor=#ffffff;fillColor=none;strokeWidth=5;";
				}

				var n = graph.insertVertex(parent, null, node.getName(), 0, 0, width, height, style);

				nodeMap.put(node.getName(), n);
			}
			for (Node node : currentNetwork.getNodes()) {
				for (Node o : node.getOutputs()) {
					graph.insertEdge(parent, null, null, nodeMap.get(node.getName()), nodeMap.get(o.getName()),
							"strokeColor=#ffffff;strokeWidth=3;");//"edgeStyle=orthogonalEdgeStyle;curved=1;rounded=0;orthogonalLoop=1;jettySize=auto;exitX=1;exitY=0.5;exitDx=0;exitDy=0;entryX=0;entryY=0.5;entryDx=0;entryDy=0;");
				}
			}
			var l = new mxHierarchicalLayout(graph);


			l.execute(graph.getDefaultParent());
		} finally {
			graph.getModel().endUpdate();
		}
	}
}
