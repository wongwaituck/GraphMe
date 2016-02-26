package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiIdentifier;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.util.PsiUtility;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by WaiTuck on 26/02/2016.
 */
public class GraphMeToolSelectionWindowFactory implements ToolWindowFactory{
    private ToolWindow myToolWindow;
    private JPanel panel1;
    private JList<PsiIdentifier> listOfPsis;
    private Project project;

    public GraphMeToolSelectionWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        this.project = project;

        ASTMatrix am = PsiUtility.generateASTMatrix(project);
        listOfPsis = new com.intellij.ui.components.JBList(am.getPsis());
        listOfPsis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane listScroller = new com.intellij.ui.components.JBScrollPane(listOfPsis);
        listScroller.setPreferredSize(new Dimension(250, 80));

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Component component = toolWindow.getComponent();
        JPanel customPanel = new JPanel();
        JButton button = new JButton("Generate Dependent Set");
        button.addActionListener(new GenerateDependentSetButtonListener(listOfPsis, am));

        customPanel.add(new JLabel("Select your seed set"));
        customPanel.add(listOfPsis);
        customPanel.add(button);
        component.getParent().add(customPanel);


    }

}
