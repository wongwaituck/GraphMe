package com.smu.graphme.util;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiSwitchStatementImpl;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class ExpressionGraphStrategy extends GraphStrategy {
    public ExpressionGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiExpression expression = (PsiExpression) getPsiElement();
        am.setDependency(currPi, getPsiIdentifier(expression.getType(), psiClasses));
    }
}
