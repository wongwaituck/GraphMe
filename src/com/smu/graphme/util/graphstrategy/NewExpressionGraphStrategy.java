package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 27/01/2016.
 */
public class NewExpressionGraphStrategy extends GraphStrategy {
    public NewExpressionGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiNewExpression pne = (PsiNewExpression) getPsiElement();


        PsiJavaCodeReferenceElement pe = pne.getClassOrAnonymousClassReference();
        if(pe != null) {
            am.setDependency(currPi, getPsiIdentifier(pe, psiClasses));
        }


        PsiElement[] elements = pne.getChildren();
        for(PsiElement element : elements){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(element);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException e){
                //
            }
        }
    }


}
