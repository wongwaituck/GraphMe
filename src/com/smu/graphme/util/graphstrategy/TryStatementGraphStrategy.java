package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.List;
import java.util.Set;

/**
 * Created by WaiTuck on 05/02/2016.
 */
public class TryStatementGraphStrategy extends GraphStrategy{
    public TryStatementGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiTryStatement pts = (PsiTryStatement) getPsiElement();

        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pts.getTryBlock());
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }

        PsiParameter[] params = pts.getCatchBlockParameters();
        for(PsiParameter param : params){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(param);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException e){
                //
            }
        }

        PsiCodeBlock[] catchBlocks = pts.getCatchBlocks();

        for(PsiCodeBlock catchBlock : catchBlocks){
            try {
                GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(catchBlock);
                gs.handleCase(am, currPi, psiClasses);
            } catch (GraphStrategyException e){
                //
            }
        }

        try {
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pts.getFinallyBlock());
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){
            //
        }
    }
}
