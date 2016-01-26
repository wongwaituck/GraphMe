package com.smu.graphme.util;

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
    }


}
