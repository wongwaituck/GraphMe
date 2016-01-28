package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 28/01/2016.
 */
public class TypeElementGraphStrategy extends GraphStrategy {
    public TypeElementGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiTypeElement pte = (PsiTypeElement) getPsiElement();
        PsiJavaCodeReferenceElement pe = pte.getInnermostComponentReferenceElement();
        if (pe != null) {
            PsiType[] types = pe.getTypeParameters();
            for(PsiType type: types){
                PsiIdentifier pi = getPsiIdentifier(type,psiClasses);
                am.setDependency(currPi, pi);
            }
        }
    }


}
