package com.smu.graphme.toolwindow.prefuse;

import prefuse.Visualization;
import prefuse.controls.FocusControl;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ui.UILib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;

/**
 * Created by kevinsteppe on 13/5/16.
 */
public class SelectSeedSetControl extends FocusControl
{
    private String group = Visualization.FOCUS_ITEMS;

    public SelectSeedSetControl(int clicks)
    {
        super(clicks);
    }

    public void itemClicked(VisualItem item, MouseEvent e)
    {
        if ( !filterCheck(item) ) return;

        Visualization vis = item.getVisualization();
        TupleSet ts = vis.getFocusGroup(group);

        if ( UILib.isButtonPressed(e, button) && e.getClickCount() == ccount )
        {

            item.setFixed(false);

            boolean ctrl = e.isControlDown();
            if ( !ctrl && item != curFocus) {
                curFocus = item;
                ts.setTuple(item);
                //item.setFillColor(Color.GREEN.getRGB());

                //clear all
                DepenedencyHighlight.highlight((NodeItem)item, DepenedencyHighlight.Mode.SeedNode, DepenedencyHighlight.Change.Reset);
                item.setFixed(true);
            } else if ( ts.containsTuple(item) ) { // || ctrl ) {
                ts.removeTuple(item);
                DepenedencyHighlight.highlight((NodeItem)item, DepenedencyHighlight.Mode.SeedNode, DepenedencyHighlight.Change.Remove);
                //clear this item
                if (ctrl && item == curFocus) curFocus = null;
            } else { // if (item != curFocus){
                ts.addTuple(item);
                item.setFixed(true);
                //add this item
                DepenedencyHighlight.highlight((NodeItem)item, DepenedencyHighlight.Mode.SeedNode, DepenedencyHighlight.Change.Add);

            }
            runActivity(vis);


        }

        if ( ! (item instanceof NodeItem) )
            return;

        NodeItem selectedNode = (NodeItem)item;
/*
        Visualization vi = selectedNode.getVisualization();
        TupleSet focusGroup = vi.getFocusGroup(Visualization.FOCUS_ITEMS);

        System.out.println("itemClicked " + item);

        if (focusGroup.containsTuple(item))
        {
            //set fixed false, true
            item.setFixed(false);
            item.setFixed(true);
            DepenedencyHighlight.highlight(selectedNode, DepenedencyHighlight.Mode.SeedNode, DepenedencyHighlight.Change.Add);
        }
        else
        {
            DepenedencyHighlight.highlight(selectedNode, DepenedencyHighlight.Mode.SeedNode, DepenedencyHighlight.Change.Remove);
            //set fixed false
            item.setFixed(false);
            //if focus group size == 0 put something back in
            if (focusGroup.getTupleCount() == 0)
            {
                System.out.println("itemClicked() re-add");
                focusGroup.addTuple(item);
                item.setFixed(false);
            }
        }
*/
        if (ts.getTupleCount() == 0)
        {
            System.out.println("itemClicked() re-add");
            ts.addTuple(item);
            item.setFixed(false);
        }

        //ok focus group is set.
        vis.run("draw");
    }

    private void runActivity(Visualization vis) {
        if ( activity != null ) {
            vis.run(activity);
        }
    }
}
