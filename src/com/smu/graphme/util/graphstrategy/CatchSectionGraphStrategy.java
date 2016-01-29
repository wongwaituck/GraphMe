package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 29/01/2016.
 */
public class CatchSectionGraphStrategy extends GraphStrategy {
    public  CatchSectionGraphStrategy(PsiElement e){
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiCatchSection pcs = (PsiCatchSection) getPsiElement();

        //add exception type
        PsiType pt = pcs.getCatchType();
        PsiIdentifier pi = getPsiIdentifier(pt, psiClasses);
        am.setDependency(currPi, pi);


        //parse block in expection code block
        PsiCodeBlock pcb = pcs.getCatchBlock();
        try{
            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pcb);
            gs.handleCase(am, currPi, psiClasses);
        } catch (GraphStrategyException e){

        }
    }


}
