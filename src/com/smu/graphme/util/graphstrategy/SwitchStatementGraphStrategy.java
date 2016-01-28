package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiSwitchStatementImpl;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class SwitchStatementGraphStrategy  extends GraphStrategy {
    public SwitchStatementGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiSwitchStatementImpl statement = (PsiSwitchStatementImpl) getPsiElement();
        PsiCodeBlock pcb = statement.getBody();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pcb);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e) {
            //
        }

    }
}