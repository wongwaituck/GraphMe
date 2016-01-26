package com.smu.graphme.util;

import com.intellij.icons.AllIcons;
import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 26/01/2016.
 */
public class WhileStatementGraphStrategy extends GraphStrategy {
    public WhileStatementGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiWhileStatement statement = (PsiWhileStatement) getPsiElement();

        //get condition
        PsiExpression expr = statement.getCondition();
        try{
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(expr);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }

        //get
        PsiStatement psiBodyStatement = statement.getBody();
        try{
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(psiBodyStatement);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }
    }


}
