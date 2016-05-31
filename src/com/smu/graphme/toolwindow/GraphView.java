package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.toolwindow.prefuse.SelectSeedSetControl;
import com.smu.graphme.util.PsiUtility;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.*;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.StrokeLib;
import prefuse.util.force.*;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.List;

/**
 * Created by WaiTuck on 04/02/2016.
 */
public class GraphView extends JPanel {
    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    private static final String LABEL = "label";

    private Display display;
    private Visualization m_vis;
    private Project project;

    public GraphView(Project p) {
        super(new BorderLayout());
        project = p;
        m_vis = new Visualization();
        setGraph(PsiUtility.generateASTMatrix(project));


        // create a new, empty visualization for our data



        JToolBar toolBar = new JToolBar("Still draggable");
        addButtons(toolBar);
        setPreferredSize(new Dimension(450, 130));
        add(toolBar, BorderLayout.PAGE_START);
        add(display, BorderLayout.CENTER);


    }

    private void addButtons(JToolBar toolBar) {
        JButton reloadButton = new JButton("Reload");
        reloadButton.addActionListener(new ResetButtonListener(this, project));
        toolBar.add(reloadButton);
    }

    private void setForces(ForceSimulator fsim) {
        Force[] forces = fsim.getForces();
        for (Force f : forces) {
            if (f.isSpringForce()) {
                System.out.println("f: " + f);
                for (int i = 0; i < f.getParameterCount(); i++) {
                    if (f.getParameterName(i).contains("SpringLength")) {
                        f.setParameter(i, 80);
                    }
                }
            } else {
                if (f instanceof DragForce) {
                    for (int i = 0; i < f.getParameterCount(); i++) {
                        if (f.getParameterName(i).contains("DragCo")) {
                            f.setParameter(i, 0.060f);
                        }
                    }
                }

                if (f instanceof NBodyForce) {
                    for (int i = 0; i < f.getParameterCount(); i++) {
                        if (f.getParameterName(i).contains("GravitationalConstant")) {
                            f.setParameter(i, -0.1f);
                        }
                    }
                }
            }
        }

    }

    public void setGraph(ASTMatrix asm) {
        m_vis = new Visualization();
        // --------------------------------------------------------------------
        // set up the renderers

        LabelRenderer tr = new LabelRenderer();
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization

        // adds graph to visualization and sets renderer label field
        //set g based on asm
        Graph g;
        ASTMatrix.setSingleton(asm);

        //Option 1 - create a Table and pass table to graph constructor
        // Table table = ...?
        // table ...?
        // g = new Graph(table, true);

        //Option 2 - create empty graph then add nodes and edges
        g = new Graph(true); // directed graph
        g.getNodeTable().addColumn(LABEL, LABEL.getClass());

        java.util.List<PsiClass> moduleList = asm.getPsiClasses();

        for (PsiClass m : moduleList) {
            Node n = g.addNode();
            n.setString(LABEL, m.getName());
        }

        int[][] depMatrix = asm.getMatrix();
        for (int row = 0; row < depMatrix.length; row++) {
            for (int col = 0; col < depMatrix[row].length; col++) {
                if (depMatrix[row][col] > 0) {
                    Node s = g.getNode(row);
                    Node t = g.getNode(col);
                    Edge e = g.addEdge(s, t);
                }
            }
        }

        String label = "label";


        // update labeling
        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();

        ((LabelRenderer) drf.getDefaultRenderer()).setTextField(LABEL);

        // update graph
        m_vis.removeGroup(graph);
        VisualGraph vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);

        VisualItem vi = (VisualItem) (vg.getEdge(0));
        float w = vi.getStroke().getLineWidth();
        BasicStroke s = StrokeLib.getStroke(w, StrokeLib.DASHES);

        m_vis.setValue(edges, null, VisualItem.STROKE, s);

        //Need to add any disconnected parts.
        List<PsiIdentifier> roots = asm.generateRoots();

        for (PsiIdentifier i : roots) {
            int index = asm.getIndex(i);
            VisualItem f = (VisualItem) vg.getNode(index);
            m_vis.getGroup(Visualization.FOCUS_ITEMS).addTuple(f); // setTuple(f);
        }



        // --------------------------------------------------------------------
        // create actions to process the visual data

        int hops = 30;
        final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

        ColorAction fill = new ColorAction(nodes,
                VisualItem.FILLCOLOR, ColorLib.color(Color.WHITE));
        fill.add(VisualItem.FIXED, ColorLib.color(Color.YELLOW));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.color(Color.GREEN));


        ActionList draw = new ActionList();
        draw.add(filter);
        draw.add(fill);

        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, ColorLib.color(Color.BLACK)));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.color(Color.BLACK)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.color(Color.BLACK)));// .gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.color(Color.BLACK)));


        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(new ForceDirectedLayout(graph));
        animate.add(fill);
        animate.add(new RepaintAction());

        ForceSimulator fsim = ((ForceDirectedLayout) animate.get(0)).getForceSimulator();


        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        m_vis.runAfter("draw", "layout");


        // --------------------------------------------------------------------
        // set up a display to show the visualization
        if(this.display == null) {
            this.display = new Display(m_vis);
            display.setSize(700, 700);
            display.pan(350, 350);
            display.setForeground(Color.GRAY);
            display.setBackground(Color.WHITE);

            // main display controls
            display.addControlListener(new SelectSeedSetControl(1));
            display.addControlListener(new DragControl());
            display.addControlListener(new PanControl());
            display.addControlListener(new ZoomControl());
            display.addControlListener(new WheelZoomControl());
            display.addControlListener(new ZoomToFitControl());

            display.setForeground(Color.GRAY);
            display.setBackground(Color.WHITE);
        } else {
            display.setVisualization(m_vis);
        }

        // --------------------------------------------------------------------
        // launch the visualization

        // create a panel for editing force values

        JForcePanel fpanel = new JForcePanel(fsim);


        final JValueSlider slider = new JValueSlider("Distance", 0, hops, hops);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                filter.setDistance(slider.getValue().intValue());
                m_vis.run("draw");
            }
        });
        slider.setBackground(Color.WHITE);
        slider.setPreferredSize(new Dimension(300, 30));
        slider.setMaximumSize(new Dimension(300, 30));

        Box cf = new Box(BoxLayout.Y_AXIS);
        cf.add(slider);
        cf.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
        fpanel.add(cf);

        fpanel.add(Box.createVerticalGlue());

        // now we run our action list
        m_vis.run("draw");

        //now wait 2s before adjusting forces
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            setForces(fsim);
        }


    }

}

// fix selected focus nodes
  /*          TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
    focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                for ( int i=0; i<rem.length; ++i )
                {
                    ((VisualItem) rem[i]).setFixed(false);
                    if (rem[i] instanceof  NodeItem)
                        DepenedencyHighlight.highlight((NodeItem) rem[i], DepenedencyHighlight.Mode.SeedNode, DepenedencyHighlight.Change.Remove);
                }
                for ( int i=0; i<add.length; ++i ) {
                    ((VisualItem)add[i]).setFixed(false);
                    ((VisualItem)add[i]).setFixed(true);
                }
/*                if ( ts.getTupleCount() == 0 ) {
                    System.out.println("tupleSetChanged() re-add");
                    ts.addTuple(rem[0]);
                    ((VisualItem)rem[0]).setFixed(false);
                }
*//*
                m_vis.run("draw");
            }
        });
*/

  /*

        // create a new JSplitPane to present the interface
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(display);
        split.setRightComponent(fpanel);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(700);

        add(split);
        */