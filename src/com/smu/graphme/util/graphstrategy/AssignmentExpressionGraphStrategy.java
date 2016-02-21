package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 12/02/2016.
 */
public class AssignmentExpressionGraphStrategy extends GraphStrategy {

    public AssignmentExpressionGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiAssignmentExpression pae = (PsiAssignmentExpression) getPsiElement();
        PsiExpression ple = pae.getLExpression();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(ple);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }

        PsiExpression pre = pae.getRExpression();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pre);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }
    }
}
