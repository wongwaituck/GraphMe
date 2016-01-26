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
        PsiElement[] elements = methodImpl.getChildren();

        PsiMethod method = methodImpl.resolveMethod();

        //resolve owning class
        PsiIdentifier owningClazz = method.getContainingClass().getNameIdentifier();
        am.setDependency(currPi, owningClazz);

        //resolve parameters
        PsiExpression[] pel = methodImpl.getArgumentList().getExpressions();

        for(PsiExpression psiExpression : pel){
            try{
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(psiExpression);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException e){

            }
        }

        for(PsiElement element : elements){
            if(element instanceof PsiReferenceExpression){
                //get the current one and at the
                PsiReferenceExpression pre = (PsiReferenceExpression) element;
                for(PsiElement preElement : pre.getChildren()){
                    try{
                        GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(preElement);
                        gs.handleCase(am, currPi, psiClasses);
                    } catch (GraphStrategyException e){

                    }
                }

            }
        }

    }
}
