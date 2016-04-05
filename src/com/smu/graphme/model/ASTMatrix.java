package com.smu.graphme.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.util.PsiUtility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

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

    private int getFullyTransitiveDependencyForOnePi(PsiIdentifier pi, boolean[] hasVisited){
        int count = 0;
        int referenceIndex = getIndex(pi);
        for (int i = 0; i < psiClasses.size(); i++) {
            if (referenceMatrix[i][referenceIndex] > 0 && !hasVisited[i]) {
                count++;
                hasVisited[i] = true;
                count += getFullyTransitiveDependencyForOnePi(psiClasses.get(i).getNameIdentifier(), hasVisited);
            }
        }
        return count;
    }


    public int getFullyTransitiveDependencySize(Collection<PsiIdentifier> pies, boolean[] hasVisited){

        //create a matrix called hasVisited to prevent cyclic dependencies
        if(hasVisited == null) {
            hasVisited = new boolean[psiClasses.size()];
        }
        //resolve first level dependencies which are not itself
        int count = 0;

        //get index of reference
        for(PsiIdentifier pi : pies) {
            int referenceIndex = getIndex(pi);
            for (int i = 0; i < psiClasses.size(); i++) {
                if (referenceMatrix[i][referenceIndex] > 0 && !hasVisited[i]) {
                    count++;
                    hasVisited[i] = true;
                    count += getFullyTransitiveDependencyForOnePi(psiClasses.get(i).getNameIdentifier(), hasVisited);
                }
            }
        }

        return count;

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
        Set<PsiIdentifier> uniqueDependencySet = new HashSet<>();
        for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
            for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                if (seedIdClasses.contains(psis.get(colIndex)) && referenceMatrix[rowIndex][colIndex] > 0) {
                    System.out.println(psis.get(rowIndex).getText() + " depends on " + psis.get(colIndex).getText());
                    uniqueDependencySet.add(psis.get(rowIndex));
                }
            }
        }
        for(PsiIdentifier pi: seedIdClasses){
            uniqueDependencySet.remove(pi);
        }
        System.out.println(uniqueDependencySet);
        System.out.println("Size of dependency set: " + uniqueDependencySet.size());
    }

    public void dumpDependencyToFile() {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("dependency.dot"));
            for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
                for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                    if (referenceMatrix[rowIndex][colIndex] > 0) {
                        pw.println( psis.get(colIndex).getText()+ "->" + psis.get(rowIndex).getText());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
