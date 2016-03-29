package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.model.ASTMatrix;


import java.util.Set;

public class ConditionalExpressionGraphStrategy extends GraphStrategy {
    public ConditionalExpressionGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiExpression expression = (PsiExpression) getPsiElement();
        PsiElement[] elements = expression.getChildren();
        for(PsiElement element : elements){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(element);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException e){
                //e.printStackTrace();
            }
        }
    }
}
