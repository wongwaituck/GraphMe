package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import prefuse.data.Graph;
import prefuse.util.GraphLib;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by WaiTuck on 03/02/2016.
 */
public class GraphMeToolWindowFactory implements ToolWindowFactory {
    private ToolWindow myToolWindow;
    private JTextArea HELLOWORLDTextArea;
    private JPanel panel1;
    private JPanel graphView;


    public GraphMeToolWindowFactory() {

    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        Graph g = GraphLib.getBalancedTree(3,5);
        String label = "label";
        GraphView v = new GraphView(g, label);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(v, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
