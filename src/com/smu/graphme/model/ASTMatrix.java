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
    public static void setSingleton(ASTMatrix singleton) {
        ASTMatrix.singleton = singleton;
    }

    public static ASTMatrix getSingleton() {
        return singleton;
    }

    private static ASTMatrix singleton;
    private int[][] referenceMatrix;
    private List<PsiIdentifier> psis;
    private List<PsiClass> psiClasses;
    private int[] breadthMatrix;
    private List<PsiIdentifier> roots;

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

    private void resolveDependencyForonePi(PsiIdentifier pi, Set<PsiIdentifier> hasVisited) {
        int referenceIndex = getIndex(pi);
        for (int i = 0; i < psiClasses.size(); i++) {
            if (referenceMatrix[i][referenceIndex] > 0 && !hasVisited.contains(getPsis().get(i))) {
                hasVisited.add(getPsis().get(i));
                resolveDependencyForonePi(psiClasses.get(i).getNameIdentifier(), hasVisited);
            }
        }
    }


    public int getFullyTransitiveDependencySize(Collection<PsiIdentifier> seedSet, Set<PsiIdentifier> hasVisited) {

        //create a matrix called hasVisited to prevent cyclic dependencies
        if (hasVisited == null) {
            hasVisited = new HashSet<>();
        }

        //get index of reference
        for (PsiIdentifier pi : seedSet) {
            resolveDependencyForonePi(pi, hasVisited);
        }

        //remove classes in visitedset from seedset
        for(PsiIdentifier pi : seedSet){
            hasVisited.remove(pi);
        }

        return hasVisited.size();

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

    public Set<PsiIdentifier> dependentSetFromSeedSet(PsiClass[] seedPsiClasses) {
        Set<PsiIdentifier> seedIdClasses = PsiUtility.convertPsiClassesToPsiIdentifiers(seedPsiClasses);
        return dependentSetFromSeedSet(seedIdClasses);
    }

    public Set<PsiIdentifier> dependentSetFromSeedSet(Collection<PsiIdentifier> seedIdClasses) {
        int[] seedIndexes = new int[seedIdClasses.size()];

        int i = 0;
        for (PsiIdentifier pi : seedIdClasses) {
            seedIndexes[i++] = getIndex(pi);
        }

        return dependentSetFromSeedSet(seedIndexes);
/*
        Set<PsiIdentifier> uniqueDependencySet = new HashSet<>();
        for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
            for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                if (seedIdClasses.contains(psis.get(colIndex)) && referenceMatrix[rowIndex][colIndex] > 0) {
                    System.out.println(psis.get(rowIndex).getText() + " depends on " + psis.get(colIndex).getText());
                    uniqueDependencySet.add(psis.get(rowIndex));
                }
            }
        }
        for (PsiIdentifier pi : seedIdClasses) {
            uniqueDependencySet.remove(pi);
        }
        System.out.println(uniqueDependencySet);
        System.out.println("Size of dependency set: " + uniqueDependencySet.size());

        return uniqueDependencySet;
*/
    }

    public Set<PsiIdentifier>  dependentSetFromSeedSet(int[] seedIndexes)
    {
        Set<PsiIdentifier> uniqueDependencySet = new HashSet<>();

        for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
            for (int seed : seedIndexes)
            {
                if (rowIndex == seed)
                    continue;

                if (referenceMatrix[rowIndex][seed] > 0) {
                    System.out.println(psis.get(rowIndex).getText() + " depends on " + psis.get(seed).getText());
                    uniqueDependencySet.add(psis.get(rowIndex));
                }
            }
        }

//        System.out.println(uniqueDependencySet);
//        System.out.println("Size of dependency set: " + uniqueDependencySet.size());

        return uniqueDependencySet;
    }

    public void dumpDependencyToFile(List<PsiIdentifier> selectedList) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("C:/Users/WaiTuck/Desktop/dependency.dot"));
            pw.println("digraph dependency{");
            for (int rowIndex = 0; rowIndex < referenceMatrix.length; rowIndex++) {
                for (int colIndex = 0; colIndex < referenceMatrix[rowIndex].length; colIndex++) {
                    if (selectedList.contains(psis.get(colIndex)) && referenceMatrix[rowIndex][colIndex] > 0) {
                        pw.println(psis.get(rowIndex).getText() + " -> " + psis.get(colIndex).getText() + ";");
                        pw.flush();
                    }
                }
            }
            pw.println("}");
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void dumpFullDependencyToFile(List<PsiIdentifier> selectedList, PrintWriter pw, Set<DependsOnRelationship> hasVisited) {
        try {
            if (pw == null) {
                pw = new PrintWriter(new FileOutputStream("C:/Users/WaiTuck/Desktop/fulldependency.dot"));
                pw.println("digraph fulldependency{");
            }
            if (hasVisited == null) {
                hasVisited = new HashSet<>();
            }

            //get index of reference
            for (PsiIdentifier pi : selectedList) {
                dumpFullDependencyToFileForOnePi(pi, pw, hasVisited);
            }
            pw.println("}");
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void dumpFullDependencyToFileForOnePi(PsiIdentifier pi, PrintWriter pw, Set<DependsOnRelationship> hasVisited) {
        int referenceIndex = getIndex(pi);
        for (int i = 0; i < psiClasses.size(); i++) {

            if (referenceMatrix[i][referenceIndex] > 0) {
                //Create the dependency relation edge
                DependsOnRelationship dor = new DependsOnRelationship(psis.get(referenceIndex), psis.get(i));
                //check if the edge has been traversed
                if(!hasVisited.contains(dor)){
                    pw.println(psis.get(i).getText() + " -> " + psis.get(referenceIndex).getText() + ";");
                    pw.flush();
                    hasVisited.add(dor);
                    dumpFullDependencyToFileForOnePi(psiClasses.get(i).getNameIdentifier(), pw, hasVisited);
                }
            }
        }

    }

    public int[][] getMatrix()
    {
        return this.referenceMatrix;
    }

    public List<PsiIdentifier> generateRoots()
    {
        //start at 0

        //set M-0 to #dependencies
        //walk each dependency
            //if M-i < 0 then
            //set M-i to #dep and
            //DFS to set M-i to #dependencies

        //move to 1 ...

        roots = new ArrayList<>();

        //initialize to -1
        breadthMatrix = new int[referenceMatrix.length];
        for (int i = 0; i < breadthMatrix.length; i++) {breadthMatrix[i] = -1;}

        for (int currentNode = 0; currentNode < breadthMatrix.length; currentNode++)
        {
            countRoot(currentNode);
        }

        return roots;
    }

    private void countRoot(int currentNode)
    {
        if (breadthMatrix[currentNode] != -1)
            return;

        breadthMatrix[currentNode] = 0; //initialize
        for (int d = 0; d < referenceMatrix[currentNode].length; d++)
        {
            breadthMatrix[currentNode] += referenceMatrix[currentNode][d];
            if (referenceMatrix[currentNode][d] > 0)
                countRoot(d);
        }

        if (breadthMatrix[currentNode] == 0)
            roots.add(psis.get(currentNode));

    }
}
