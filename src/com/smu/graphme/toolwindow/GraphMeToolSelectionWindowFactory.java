package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.util.PsiUtility;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by WaiTuck on 26/02/2016.
 */
public class GraphMeToolSelectionWindowFactory implements ToolWindowFactory{
    private ToolWindow myToolWindow;
    private JPanel panel1;
    private JList<PsiIdentifier> listOfPsis;
    private JList<PsiIdentifier> listOfSelectedPsis;
    private java.util.List<PsiIdentifier> currentSelectedList = new ArrayList<>();
    private Project project;
    private static final int GAP  = 20;

    public GraphMeToolSelectionWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        this.project = project;

        ASTMatrix am = PsiUtility.generateASTMatrix(project);
        java.util.List<PsiIdentifier> psis = new ArrayList<>(am.getPsis());

        listOfPsis = new com.intellij.ui.components.JBList(psis);
        listOfSelectedPsis = new com.intellij.ui.components.JBList(currentSelectedList);
        listOfPsis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        Component component = toolWindow.getComponent();
        JPanel customPanel = new JPanel();
        customPanel.setBorder(BorderFactory.createEmptyBorder(GAP/2, 0, GAP/2, 0));

        JTextField textField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchButtonListener(textField, psis, listOfPsis));

        JButton addButton = new JButton("Add to seed set");
        addButton.addActionListener(new AddButtonListener(currentSelectedList, am.getPsis(),
                listOfPsis, listOfSelectedPsis));

        JButton clearButton = new JButton("Clear from Seed Set");
        clearButton.addActionListener(new ClearButtonListener(currentSelectedList, listOfSelectedPsis));

        customPanel.add(textField);
        customPanel.add(searchButton);
        customPanel.add(addButton);

        customPanel.add(clearButton);

        customPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        JButton generateButton = new JButton("Generate Dependent Set");
        generateButton.addActionListener(new GenerateDependentSetButtonListener(currentSelectedList, am));


        customPanel.add(new JLabel("Select your seed set"));
        customPanel.add(generateButton);
        customPanel.add(listOfPsis);
        customPanel.add(listOfSelectedPsis);


        component.getParent().add(customPanel);


    }

}
