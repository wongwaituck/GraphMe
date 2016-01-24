package com.smu.graphme.util;

import com.intellij.icons.AllIcons;
import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class LocalVariableGraphStrategy extends GraphStrategy {
    public LocalVariableGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiLocalVariable plv = (PsiLocalVariable) getPsiElement();

        PsiElement[] plvChildren = plv.getChildren();
        for(PsiElement plvChild : plvChildren){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(plvChild);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException exception) {
                //exception.printStackTrace();
            }

        }
        PsiIdentifier pi = GraphStrategy.getPsiIdentifier(plv.getTypeElement(), psiClasses);
        am.setDependency(currPi, pi);
    }
}
