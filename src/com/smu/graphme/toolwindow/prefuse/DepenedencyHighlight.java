package com.smu.graphme.toolwindow.prefuse;

import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.model.ASTMatrix;
import prefuse.Visualization;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by kevinsteppe on 13/5/16.
 */
public class DepenedencyHighlight
{
    public enum Mode { SeedNode, DependentNode }
    public enum Change { Add, Remove, Reset }

    public static void highlight(NodeItem selectedNode, Mode mode, Change change)
    {
        //if seed node then candidtes = sources
        //if dependent node then candidates = targets

        //if add, then highlight all candidates
        //if remove, then un-highlight candidates + highlight candidates in focus group.
        VisualGraph vg = (VisualGraph) selectedNode.getGraph();


        switch (change) {
            case Reset:
                Iterator it = vg.getNodes().tuples();
                while (it.hasNext())
                {
                    NodeItem nodeItem = (NodeItem)it.next();
                    nodeItem.setHighlighted(false); //remove from dependent Set
                }
                break;

            case Remove:
                System.out.println("Remove in Dependency Highlight.");
                Iterator<EdgeItem> iter = selectedNode.edges();
                while (iter.hasNext())
                {
                    EdgeItem edgeItem = iter.next();
                    if (edgeItem.getSourceItem().equals(selectedNode))
                        continue;

                    NodeItem nodeItem = edgeItem.getAdjacentItem(selectedNode);
                    nodeItem.setHighlighted(false); //remove from dependent Set
                }
                break;
            case Add:
                System.out.println("Add in Dependency Highlight.");

        }



        Visualization vi = selectedNode.getVisualization();
        TupleSet focusGroup = vi.getFocusGroup(Visualization.FOCUS_ITEMS);

        int[] seeds = new int[focusGroup.getTupleCount()];
        int i = 0;
        Iterator<Tuple> iter = focusGroup.tuples();
        while (iter.hasNext())
        {
            Tuple t = iter.next();
            seeds[i++] = t.getRow();
        }

        if (mode == Mode.SeedNode)
        {
            ASTMatrix asm = ASTMatrix.getSingleton();
            Set<PsiIdentifier> dependentSet = asm.dependentSetFromSeedSet(seeds);

            for (PsiIdentifier pi : dependentSet)
            {
                int j = asm.getIndex(pi);
                NodeItem dependentNode = (NodeItem) vg.getNode(j);
                dependentNode.setHighlighted(true);
            }
        }
    }

    /**
     *  The following tranverses edges, removes the highlights of dependent nodes
     *  Then re-highlights if the node is dependent on other seeds.

        Iterator<EdgeItem> iter = selectedNode.edges();
        while (iter.hasNext())
        {
            EdgeItem edgeItem = iter.next();
            if (edgeItem.getSourceItem().equals(selectedNode))
                continue;

            NodeItem nodeItem = edgeItem.getAdjacentItem(selectedNode);
            nodeItem.setHighlighted(false); //remove from dependent Set

            //now have to check if it is still dependent on anything in the remaining seed set
            Iterator<EdgeItem> candidateIter = nodeItem.edges();
            while (candidateIter.hasNext())
            {
                EdgeItem candidateEdge = candidateIter.next();
                if (candidateEdge.getTargetItem().equals(nodeItem))
                    continue;
                NodeItem checkNode = candidateEdge.getAdjacentItem(nodeItem);
                if (focusSet.containsTuple(checkNode))
                {
                    nodeItem.setHighlighted(true);
                    break;
                }
            }
        }
     */

}
