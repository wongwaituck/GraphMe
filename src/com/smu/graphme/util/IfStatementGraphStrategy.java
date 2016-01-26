package com.smu.graphme.util;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 24/01/2016.
 */
public class IfStatementGraphStrategy extends GraphStrategy{
    public IfStatementGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiIfStatement statement = (PsiIfStatement) getPsiElement();

        //get condition
        PsiExpression expr = statement.getCondition();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(expr);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }

        //get else branch
        PsiStatement elseStatement = statement.getElseBranch();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(elseStatement);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }

        //get then branch
        PsiStatement thenStatement = statement.getThenBranch();
        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(thenStatement);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }
    }
}
