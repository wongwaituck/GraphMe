package com.smu.graphme.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.util.PsiUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by WaiTuck on 15/01/2016.
 */
public class ASTMatrix {
    private int[][] referenceMatrix;
    private List<PsiIdentifier> psis;
    private List<PsiClass> psiClasses;

    public ASTMatrix(List<PsiClass> psiClasses) {
        psis = new ArrayList<>();
        this.psiClasses = psiClasses;
        for (PsiClass psiClass : psiClasses) {
            psis.add(psiClass.getNameIdentifier());
        }

        referenceMatrix = new int[psiClasses.size()][psiClasses.size()];

    }

    public List<PsiIdentifier> getPsis() {
        return psis;
    }

    public List<PsiClass> getPsiClasses() {
        return psiClasses;
    }

    public int getIndex(PsiIdentifier pi) {
        return psis.indexOf(pi);
    }

    public void setDependency(PsiIdentifier clazz, PsiIdentifier dependency) {
        int clazzIndex = getIndex(clazz);
        int dependencyIndex = getIndex(dependency);
        if (isWithinBounds(clazzIndex, dependencyIndex)) {
            referenceMatrix[clazzIndex][dependencyIndex]++;
        }
    }

    private boolean isWithinBounds(int clazzIndex, int dependencyIndex) {
        return clazzIndex >= 0 && clazzIndex < referenceMatrix.length &&
                dependencyIndex >= 0 && dependencyIndex < referenceMatrix[0].length;
    }

    public void printDependencyMatrix() {
        for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
            for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                if (referenceMatrix[rowIndex][colIndex] > 0) {
                    System.out.println(psis.get(rowIndex).getText() + " depends on " + psis.get(colIndex).getText());
                }
            }
        }
    }

    public void generateFromSeedSet(PsiClass[] seedPsiClasses) {
        Set<PsiIdentifier> seedIdClasses = PsiUtility.convertPsiClassesToPsiIdentifiers(seedPsiClasses);
        generateFromSeedSet(seedIdClasses);

    }

    public void generateFromSeedSet(Collection<PsiIdentifier> seedIdClasses) {
        for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
            for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                if (seedIdClasses.contains(psis.get(colIndex)) && referenceMatrix[rowIndex][colIndex] > 0) {
                    System.out.println(psis.get(rowIndex).getText() + " depends on " + psis.get(colIndex).getText());
                }
            }
        }
    }

    public void dumpDependencyToFile() {
        for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
            for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                if (referenceMatrix[rowIndex][colIndex] > 0) {
                    System.out.println(psis.get(rowIndex).getText() + "<-" + psis.get(colIndex).getText());
                }
            }
        }
    }
}
