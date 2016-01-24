package com.smu.graphme.util;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 24/01/2016.
 */
public class MethodCallExpressionImplGraphStrategy extends GraphStrategy{
    public MethodCallExpressionImplGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiMethodCallExpressionImpl methodImpl = (PsiMethodCallExpressionImpl) getPsiElement();
        PsiMethod method = methodImpl.resolveMethod();

        //resolve owning class
        PsiIdentifier owningClazz = method.getContainingClass().getNameIdentifier();
        am.setDependency(currPi, owningClazz);

        //resolve parameters
        PsiParameter[] params = method.getParameterList().getParameters();
        for(PsiParameter param : params){
            PsiIdentifier paramClazz = getPsiIdentifier(param.getTypeElement(), psiClasses);
            am.setDependency(currPi, paramClazz);
        }

    }
}
