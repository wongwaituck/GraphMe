package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class SwitchLabelStatementGraphStrategy extends GraphStrategy {
    public SwitchLabelStatementGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiSwitchLabelStatement statement = (PsiSwitchLabelStatement) getPsiElement();
        PsiExpression expr = statement.getCaseValue();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(expr);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e) {
            //
        }

    }
}
