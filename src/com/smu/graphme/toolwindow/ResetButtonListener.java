package com.smu.graphme.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiUtil;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.util.PsiUtility;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by WaiTuck on 18/05/2016.
 */
public class ResetButtonListener implements ActionListener {
    private GraphView v;
    private Project p;

    public ResetButtonListener(GraphView v, Project p){
        this.v = v;
        this.p = p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //generate new dependency graph from project
        ASTMatrix asm  = PsiUtility.generateASTMatrix(p);
        v.setGraph(asm);
    }
}
