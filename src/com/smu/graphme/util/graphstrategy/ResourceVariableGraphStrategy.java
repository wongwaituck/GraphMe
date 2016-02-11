package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 12/02/2016.
 */
public class ResourceVariableGraphStrategy extends GraphStrategy {
    public ResourceVariableGraphStrategy(PsiElement e){
        super(e);
    }


    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiResourceVariable plv = (PsiResourceVariable) getPsiElement();

        PsiElement[] plvChildren = plv.getChildren();
        for(PsiElement plvChild : plvChildren){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(plvChild);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException exception) {
                //exception.printStackTrace();
            }

        }
        PsiIdentifier pi = getPsiIdentifier(plv.getTypeElement(), psiClasses);
        am.setDependency(currPi, pi);
    }
}
