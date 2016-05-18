package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.toolwindow.prefuse.DependOnHighlightControl;
import com.smu.graphme.toolwindow.prefuse.DepenedencyHighlight;
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
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.StrokeLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.NodeItem;
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

    private Visualization m_vis;
    private Project project;

    public GraphView(Project p) {
            //Graph g, String label) {
        super(new BorderLayout());

        project = p;

        // create a new, empty visualization for our data
        m_vis = new Visualization();

        // --------------------------------------------------------------------
        // set up the renderers

        LabelRenderer tr = new LabelRenderer();
//        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization

        // adds graph to visualization and sets renderer label field
        setGraph(PsiUtility.generateASTMatrix(project));

        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
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
                    ts.addTuple(rem[0]);
                    ((VisualItem)rem[0]).setFixed(false);
                }
                */
                m_vis.run("draw");
            }
        });



        // --------------------------------------------------------------------
        // create actions to process the visual data

        int hops = 30;
        final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

        ColorAction fill = new ColorAction(nodes,
                VisualItem.FILLCOLOR, ColorLib.color(Color.WHITE));
        fill.add(VisualItem.FIXED, ColorLib.color(Color.GREEN));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));


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

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        m_vis.runAfter("draw", "layout");


        // --------------------------------------------------------------------
        // set up a display to show the visualization

        Display display = new Display(m_vis);
        display.setSize(700,700);
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
        display.addControlListener(new DependOnHighlightControl());

        // overview display
//        Display overview = new Display(vis);
//        overview.setSize(290,290);
//        overview.addItemBoundsListener(new FitOverviewListener());

        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // --------------------------------------------------------------------
        // launch the visualization

        // create a panel for editing force values
        ForceSimulator fsim = ((ForceDirectedLayout)animate.get(0)).getForceSimulator();
        JForcePanel fpanel = new JForcePanel(fsim);

//        JPanel opanel = new JPanel();
//        opanel.setBorder(BorderFactory.createTitledBorder("Overview"));
//        opanel.setBackground(Color.WHITE);
//        opanel.add(overview);

        final JValueSlider slider = new JValueSlider("Distance", 0, hops, hops);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                filter.setDistance(slider.getValue().intValue());
                m_vis.run("draw");
            }
        });
        slider.setBackground(Color.WHITE);
        slider.setPreferredSize(new Dimension(300,30));
        slider.setMaximumSize(new Dimension(300,30));

        Box cf = new Box(BoxLayout.Y_AXIS);
        cf.add(slider);
        cf.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
        fpanel.add(cf);

        //fpanel.add(opanel);

        fpanel.add(Box.createVerticalGlue());

        // create a new JSplitPane to present the interface
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(display);
        split.setRightComponent(fpanel);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(700);

        // now we run our action list
        m_vis.run("draw");

        add(split);
    }

    public void setGraph(ASTMatrix asm) {

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
        System.out.println("psi class list size " + moduleList.size());
        for (PsiClass m : moduleList)
        {
            Node n = g.addNode();
            n.setString(LABEL, m.getName());
        }

        int[][] depMatrix = asm.getMatrix();
        for (int row = 0; row < depMatrix.length; row++)
        {
            for (int col = 0; col < depMatrix[row].length; col++)
            {
                if (depMatrix[row][col] > 0)
                {
                    Node s = g.getNode(row);
                    Node t = g.getNode(col);
                    Edge e = g.addEdge(s, t);
//                    System.out.println("s = " + s + " t = " + t + " e = " + e);
                }
            }
        }

        String label = "label";


        // update labeling
        DefaultRendererFactory drf = (DefaultRendererFactory)
                m_vis.getRendererFactory();

        ((LabelRenderer)drf.getDefaultRenderer()).setTextField(LABEL);

        // update graph
        m_vis.removeGroup(graph);
        VisualGraph vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);

        VisualItem vi = (VisualItem)(vg.getEdge(0));
        float w = vi.getStroke().getLineWidth();
        BasicStroke s = StrokeLib.getStroke(w, StrokeLib.DASHES);

        m_vis.setValue(edges, null, VisualItem.STROKE, s);

        //Need to add any disconnected parts.
        List<PsiIdentifier> roots =  asm.generateRoots();

//        VisualItem f = (VisualItem)vg.getNode(0);
//        m_vis.getGroup(Visualization.FOCUS_ITEMS).addTuple(f); // setTuple(f);

        for (PsiIdentifier i : roots)
        {
            int index = asm.getIndex(i);
            VisualItem f = (VisualItem)vg.getNode(index);
            m_vis.getGroup(Visualization.FOCUS_ITEMS).addTuple(f); // setTuple(f);
//            f.setFixed(true); //this fixes the position
        }


    }

    // ------------------------------------------------------------------------
    // Main and demo methods
/*
    public static void main(String[] args) {
        UILib.setPlatformLookAndFeel();

        // create graphview
        String datafile = null;
        String label = "label";
        if ( args.length > 1 ) {
            datafile = args[0];
            label = args[1];
        }

        JFrame frame = demo(datafile, label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }*/
/*
    public static JFrame demo() {
        return demo((String)null, "label");
    }

    public static JFrame demo(String datafile, String label) {
        Graph g = null;
        if ( datafile == null ) {
            g = GraphLib.getGrid(15,15);
            label = "label";
        } else {
            try {
                g = new GraphMLReader().readGraph(datafile);
            } catch ( Exception e ) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return demo(g, label);
    }
*/
    /*
    public static JFrame demo(Graph g, String label) {
        final GraphView view = new GraphView(g, label);

        // set up menu
        JMenu dataMenu = new JMenu("Data");
        dataMenu.add(new OpenGraphAction(view));
        dataMenu.add(new GraphMenuAction("Grid","ctrl 1",view) {
            protected Graph getGraph() {
                return GraphLib.getGrid(15,15);
            }
        });
        dataMenu.add(new GraphMenuAction("Clique","ctrl 2",view) {
            protected Graph getGraph() {
                return GraphLib.getClique(10);
            }
        });
        dataMenu.add(new GraphMenuAction("Honeycomb","ctrl 3",view) {
            protected Graph getGraph() {
                return GraphLib.getHoneycomb(5);
            }
        });
        dataMenu.add(new GraphMenuAction("Balanced Tree","ctrl 4",view) {
            protected Graph getGraph() {
                return GraphLib.getBalancedTree(3,5);
            }
        });
        dataMenu.add(new GraphMenuAction("Diamond Tree","ctrl 5",view) {
            protected Graph getGraph() {
                return GraphLib.getDiamondTree(3,3,3);
            }
        });
        JMenuBar menubar = new JMenuBar();
        menubar.add(dataMenu);

        // launch window
        JFrame frame = new JFrame("p r e f u s e  |  g r a p h v i e w");
        frame.setJMenuBar(menubar);
        frame.setContentPane(view);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                view.m_vis.run("layout");
            }
            public void windowDeactivated(WindowEvent e) {
                view.m_vis.cancel("layout");
            }
        });

        return frame;
    }
*/

    // ------------------------------------------------------------------------

    /**
     * Swing menu action that loads a graph into the graph viewer.
     */
/*    public abstract static class GraphMenuAction extends AbstractAction {
        private GraphView m_view;
        public GraphMenuAction(String name, String accel, GraphView view) {
            m_view = view;
            this.putValue(AbstractAction.NAME, name);
            this.putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(accel));
        }
        public void actionPerformed(ActionEvent e) {
            m_view.setGraph(getGraph(), "label");
        }
        protected abstract Graph getGraph();
    }
*/
        /*
    public static class OpenGraphAction extends AbstractAction {
        private GraphView m_view;

        public OpenGraphAction(GraphView view) {
            m_view = view;
            this.putValue(AbstractAction.NAME, "Open File...");
            this.putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("ctrl O"));
        }
        public void actionPerformed(ActionEvent e) {
            Graph g = IOLib.getGraphFile(m_view);
            if ( g == null ) return;
            String label = getLabel(m_view, g);
            if ( label != null ) {
                m_view.setGraph(g, label);
            }
        }
        public static String getLabel(Component c, Graph g) {
            // get the column names
            Table t = g.getNodeTable();
            int  cc = t.getColumnCount();
            String[] names = new String[cc];
            for ( int i=0; i<cc; ++i )
                names[i] = t.getColumnName(i);

            // where to store the result
            final String[] label = new String[1];

            // -- build the dialog -----
            // we need to get the enclosing frame first
            while ( c != null && !(c instanceof JFrame) ) {
                c = c.getParent();
            }
            final JDialog dialog = new JDialog(
                    (JFrame)c, "Choose Label Field", true);

            // create the ok/cancel buttons
            final JButton ok = new JButton("OK");
            ok.setEnabled(false);
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    label[0] = null;
                    dialog.setVisible(false);
                }
            });

            // build the selection list
            final JList list = new JList(names);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {
                            int sel = list.getSelectedIndex();
                            if ( sel >= 0 ) {
                                ok.setEnabled(true);
                                label[0] = (String)list.getModel().getElementAt(sel);
                            } else {
                                ok.setEnabled(false);
                                label[0] = null;
                            }
                        }
                    });
            JScrollPane scrollList = new JScrollPane(list);

            JLabel title = new JLabel("Choose a field to use for node labels:");

            // layout the buttons
            Box bbox = new Box(BoxLayout.X_AXIS);
            bbox.add(Box.createHorizontalStrut(5));
            bbox.add(Box.createHorizontalGlue());
            bbox.add(ok);
            bbox.add(Box.createHorizontalStrut(5));
            bbox.add(cancel);
            bbox.add(Box.createHorizontalStrut(5));

            // put everything into a panel
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollList, BorderLayout.CENTER);
            panel.add(bbox, BorderLayout.SOUTH);
            panel.setBorder(BorderFactory.createEmptyBorder(5,2,2,2));

            // show the dialog
            dialog.setContentPane(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(c);
            dialog.setVisible(true);
            dialog.dispose();

            // return the label field selection
            return label[0];
        }
    }

    public static class FitOverviewListener implements ItemBoundsListener {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private Rectangle2D m_temp = new Rectangle2D.Double();
        private double m_d = 15;
        public void itemBoundsChanged(Display d) {
            d.getItemBounds(m_temp);
            GraphicsLib.expand(m_temp, 25/d.getScale());

            double dd = m_d/d.getScale();
            double xd = Math.abs(m_temp.getMinX()-m_bounds.getMinX());
            double yd = Math.abs(m_temp.getMinY()-m_bounds.getMinY());
            double wd = Math.abs(m_temp.getWidth()-m_bounds.getWidth());
            double hd = Math.abs(m_temp.getHeight()-m_bounds.getHeight());
            if ( xd>dd || yd>dd || wd>dd || hd>dd ) {
                m_bounds.setFrame(m_temp);
                DisplayLib.fitViewToBounds(d, m_bounds, 0);
            }
        }
    }*/

}
