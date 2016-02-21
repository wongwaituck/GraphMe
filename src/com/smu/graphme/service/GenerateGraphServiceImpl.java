package com.smu.graphme.service;

import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.model.DefaultViewSettings;
import com.smu.graphme.util.PsiUtility;
import com.smu.graphme.util.graphstrategy.GraphStrategy;
import com.smu.graphme.util.graphstrategy.GraphStrategyException;
import com.smu.graphme.util.graphstrategy.GraphStrategyFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by WaiTuck on 11/01/2016.
 */
public class GenerateGraphServiceImpl implements GenerateGraphService, ApplicationComponent {

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GenerateGraph";
    }

    @Override
    public void generateGraph(AnActionEvent e) {
        Set<PsiPackage> topLevelPackages = PsiUtility.getPsiTopLevelPackages(e);
        Set<PsiClass> psiClassesWithInner = PsiUtility.getAllUserImplementedClasses(topLevelPackages);

        ASTMatrix am = new ASTMatrix(new ArrayList<>(psiClassesWithInner));

        for(PsiClass c: psiClassesWithInner){

            //resolve implementation and extends list
            PsiClass[] psiSuperClasses = c.getSupers();
            PsiIdentifier currPi = c.getNameIdentifier();
            for(PsiClass pc : psiSuperClasses){
                PsiIdentifier pi = pc.getNameIdentifier();
                am.setDependency(currPi, pi);
            }

            //resolve field references
            PsiField[] psiFields = c.getAllFields();
            for(PsiField psiField : psiFields){
                if(psiField != null) {
                    PsiElement[] elements = psiField.getChildren();
                    for(PsiElement element : elements) {
                        try {
                            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(element);
                            gs.handleCase(am, currPi, psiClassesWithInner);
                        } catch (GraphStrategyException exception){

                        }
                    }
                    PsiIdentifier pi = GraphStrategy.getPsiIdentifier(psiField, psiClassesWithInner);
                    am.setDependency(currPi, pi);
                }
            }

            //static block handler
            PsiElement[] elements = c.getChildren();
            //get class initializer element
            for(PsiElement element : elements){
                if(element instanceof PsiClassInitializer){
                    PsiClassInitializer pci = (PsiClassInitializer) element;
                    for(PsiElement pciElement : pci.getChildren()) {
                        try {
                            GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(pciElement);
                            gs.handleCase(am, currPi, psiClassesWithInner);
                        } catch (GraphStrategyException exception) {

                        }
                    }
                }
            }

            //resolve method dependencies
            PsiMethod[] psiMethods = c.getMethods();
            for(PsiMethod psiMethod : psiMethods){
                try {
                    GraphStrategy gs = GraphStrategyFactory.getRelevantStrategy(psiMethod);
                    gs.handleCase(am, currPi, psiClassesWithInner);
                } catch (GraphStrategyException exception){

                }
            }

        }
        am.printDependencyMatrix();

        //test for seed set of Main
        System.out.println("Testing generate from set");
        PsiClass[] testSeedClass = new PsiClass[2];
        am.generateFromSeedSet(psiClassesWithInner.stream().limit(2).collect(Collectors.toList()).toArray(testSeedClass));
    }




}
