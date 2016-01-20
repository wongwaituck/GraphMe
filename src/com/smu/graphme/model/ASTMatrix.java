package com.smu.graphme.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WaiTuck on 15/01/2016.
 */
public class ASTMatrix {
    private int[][] referenceMatrix;
    private List<PsiIdentifier> psis;

    public ASTMatrix(List<PsiClass> psiClasses){
        psis = new ArrayList<>();

        for(PsiClass psiClass : psiClasses){
            psis.add(psiClass.getNameIdentifier());
        }

        referenceMatrix = new int[psiClasses.size()][psiClasses.size()];
        constructMatrix();
    }

    private void constructMatrix(){

    }

    public int getIndex(PsiIdentifier pi){
        return psis.indexOf(pi);
    }

    public void setDependency(PsiIdentifier clazz, PsiIdentifier dependency){
        int clazzIndex = getIndex(clazz);
        int dependencyIndex = getIndex(dependency);
        if(isWithinBounds(clazzIndex, dependencyIndex)) {
            referenceMatrix[clazzIndex][dependencyIndex]++;
        }
    }

    private boolean isWithinBounds(int clazzIndex, int dependencyIndex){
        return clazzIndex >= 0 && clazzIndex < referenceMatrix.length &&
                dependencyIndex >= 0 && dependencyIndex < referenceMatrix[0].length;
    }

    public void printDependencyMatrix(){
        for(int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++){
            for(int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++){
                if(referenceMatrix[rowIndex][colIndex] > 0){
                    System.out.println(psis.get(rowIndex).getText() + " depends on " + psis.get(colIndex).getText());
                }
            }
        }
    }

    public void dumpDependencyToFile(){

    }
}
