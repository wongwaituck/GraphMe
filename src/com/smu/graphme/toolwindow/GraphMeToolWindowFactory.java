package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.util.PsiUtility;
import org.jetbrains.annotations.NotNull;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.util.GraphLib;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by WaiTuck on 03/02/2016.
 */
public class GraphMeToolWindowFactory implements ToolWindowFactory {
    public static final String LABEL = "label";
    private ToolWindow myToolWindow;
    private JTextArea HELLOWORLDTextArea;
    private JPanel panel1;
    private JPanel graphView;


    public GraphMeToolWindowFactory() {

    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        GraphView v = new GraphView(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(v, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
