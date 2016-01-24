package com.smu.graphme.util;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class StatementGraphStrategy extends GraphStrategy {
    public StatementGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiStatement statement = (PsiStatement) getPsiElement();
        PsiElement[] childElements = statement.getChildren();

        for(PsiElement childElement : childElements){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(childElement);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException e){
                //
            }
        }
    }
}
