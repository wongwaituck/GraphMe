package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;

/**
 * Created by WaiTuck on 24/01/2016.
 */
public class GraphStrategyFactory {
    public static GraphStrategy getRelevantStrategy(PsiElement e) throws GraphStrategyException{
        if (e instanceof PsiMethodCallExpressionImpl){
            return new MethodCallExpressionImplGraphStrategy(e);
        } else if (e instanceof PsiMethod){
            return new MethodGraphStrategy(e);
        } else if (e instanceof PsiCodeBlock){
            return new CodeBlockGraphStrategy(e);
        } else if (e instanceof PsiSwitchStatement){
            return new SwitchStatementGraphStrategy(e);
        } else if (e instanceof PsiSwitchLabelStatement){
            return new SwitchLabelStatementGraphStrategy(e);
        } else if (e instanceof PsiReturnStatement){
            return new ReturnStatementGraphStrategy(e);
        } else if (e instanceof PsiWhileStatement){
            return new WhileStatementGraphStrategy(e);
        } else if (e instanceof PsiCatchSection){
            return new CatchSectionGraphStrategy(e);
        } else if (e instanceof PsiNewExpression){
            return new NewExpressionGraphStrategy(e);
        } else if (e instanceof PsiTypeElement){
            return new TypeElementGraphStrategy(e);
        } else if (e instanceof PsiParameter){
            return new ParameterGraphStrategy(e);
        } else if (e instanceof PsiLocalVariable){
            return new LocalVariableGraphStrategy(e);
        } else if (e instanceof PsiInstanceOfExpression) {
            return new InstanceofExpressionGraphStrategy(e);
        } else if (e instanceof PsiExpression){
            return new ExpressionGraphStrategy(e);
        } else if (e instanceof PsiIfStatement){
            return new IfStatementGraphStrategy(e);
        } else if (e instanceof PsiStatement){
            return new StatementGraphStrategy(e);
        } else{
            throw new GraphStrategyException("Not yet implemented!");

        }
    }
}
