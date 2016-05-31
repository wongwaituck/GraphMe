package com.smu.graphme.toolwindow.prefuse;


import java.awt.event.MouseEvent;
import java.util.Iterator;

import prefuse.controls.ControlAdapter;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/** <p>
        * A ControlListener that sets the highlighted status (using the
        * {@link prefuse.visual.VisualItem#setHighlighted(boolean)
        * VisualItem.setHighlighted} method) for nodes neighboring the node
        * currently under the mouse pointer. The highlight flag might then be used
        * by a color function to change node appearance as desired.
        * </p>
        *
        * @author Kevin Steppe
        */
public class DependOnHighlightControl extends ControlAdapter
{

    private String activity = null;
    private boolean highlightWithInvisibleEdge = false;

    /**
     * Creates a new highlight control.
     */
    public DependOnHighlightControl() {
        this(null);
    }

    /**
     * Creates a new highlight control that runs the given activity
     * whenever the neighbor highlight changes.
     * @param activity the update Activity to run
     */
    public DependOnHighlightControl(String activity) {
        this.activity = activity;
    }

    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem )
            setNeighborHighlight((NodeItem)item, true);
    }

    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemExited(VisualItem item, MouseEvent e) {
        if ( item instanceof NodeItem )
            setNeighborHighlight((NodeItem)item, false);
    }

    /**
     * Set the highlighted state of the neighbors of a node.
     * @param selectedNode the node under consideration
     * @param state the highlighting state to apply to neighbors
     */
    protected void setNeighborHighlight(NodeItem selectedNode, boolean state) {
        Iterator iter = selectedNode.edges();
        while ( iter.hasNext() ) {
            EdgeItem edgeItem = (EdgeItem)iter.next();
            if (edgeItem.getTargetItem().equals(selectedNode))
                continue;

            NodeItem nodeItem = edgeItem.getAdjacentItem(selectedNode);
            if (edgeItem.isVisible() || highlightWithInvisibleEdge) {
                edgeItem.setHighlighted(state);
                nodeItem.setHighlighted(state);
            }
        }
        if ( activity != null )
            selectedNode.getVisualization().run(activity);
    }

    /**
     * Indicates if neighbor nodes with edges currently not visible still
     * get highlighted.
     * @return true if neighbors with invisible edges still get highlighted,
     * false otherwise.
     */
    public boolean isHighlightWithInvisibleEdge() {
        return highlightWithInvisibleEdge;
    }

    /**
     * Determines if neighbor nodes with edges currently not visible still
     * get highlighted.
     * @param highlightWithInvisibleEdge assign true if neighbors with invisible
     * edges should still get highlighted, false otherwise.
     */
    public void setHighlightWithInvisibleEdge(boolean highlightWithInvisibleEdge) {
        this.highlightWithInvisibleEdge = highlightWithInvisibleEdge;
    }

} // end of class NeighborHighlightControl