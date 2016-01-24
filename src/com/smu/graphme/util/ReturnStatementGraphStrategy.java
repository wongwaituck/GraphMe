package com.smu.graphme.util;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiConditionalExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReturnStatementImpl;
import com.intellij.psi.impl.source.tree.java.PsiSwitchStatementImpl;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class ReturnStatementGraphStrategy extends GraphStrategy {
    public ReturnStatementGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiReturnStatement statement = (PsiReturnStatement) getPsiElement();
        PsiExpression expr =  statement.getReturnValue();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(expr);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e) {
            //
        }

    }

}
