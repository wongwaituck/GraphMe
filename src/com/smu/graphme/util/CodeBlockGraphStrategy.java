package com.smu.graphme.util;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 25/01/2016.
 */
public class CodeBlockGraphStrategy extends GraphStrategy {
    public CodeBlockGraphStrategy(PsiElement e) {
        super(e);
    }

    @Override
    public void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses) {
        PsiCodeBlock pcb = (PsiCodeBlock) getPsiElement();
        if(pcb != null) {
            PsiStatement[] statements = pcb.getStatements();
            for(PsiStatement statement: statements){
                try {
                    GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(statement);
                    gs.handleCase(am, currPi, psiClasses);
                } catch (GraphStrategyException e){
                    //e.printStackTrace();
                }

            }
        }
    }
}
