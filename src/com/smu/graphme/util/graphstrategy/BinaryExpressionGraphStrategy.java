package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 11/02/2016.
 */
public class BinaryExpressionGraphStrategy extends GraphStrategy {

    public BinaryExpressionGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiBinaryExpression pbe = (PsiBinaryExpression) getPsiElement();

        //parse left operand
        PsiExpression lpe = pbe.getLOperand();

        try{
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(lpe);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){

        }

        //parse right operand
        PsiExpression rpe = pbe.getROperand();

        try{
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(rpe);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){

        }
    }
}
