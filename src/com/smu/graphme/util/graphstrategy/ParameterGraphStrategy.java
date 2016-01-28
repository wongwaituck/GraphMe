package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiParameter;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 27/01/2016.
 */
public class ParameterGraphStrategy extends GraphStrategy{
    public ParameterGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiParameter pp = (PsiParameter) getPsiElement();
        am.setDependency(currPi, getPsiIdentifier(pp.getTypeElement(), psiClasses));

    }


}
