package com.smu.graphme.util;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class MethodGraphStrategy extends GraphStrategy{
    public MethodGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        //resolve parameter dependencies
        PsiMethod psiMethod = (PsiMethod) getPsiElement();
        PsiParameter[] params = psiMethod.getParameterList().getParameters();
        for(PsiParameter param : params){
            am.setDependency(currPi, getPsiIdentifier(param.getTypeElement(), psiClasses));
        }
        //resolve return type dependencies
        PsiIdentifier returnTypei = GraphStrategy.getPsiIdentifier(psiMethod.getReturnTypeElement(), psiClasses);

        am.setDependency(currPi, returnTypei);

        //resolve method body dependencies
        PsiCodeBlock pcb = psiMethod.getBody();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pcb);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }
    }
}
