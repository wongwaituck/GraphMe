package com.smu.graphme.util;

import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.model.DefaultViewSettings;
import com.smu.graphme.util.graphstrategy.GraphStrategy;
import com.smu.graphme.util.graphstrategy.GraphStrategyException;
import com.smu.graphme.util.graphstrategy.GraphStrategyFactory;

import java.util.*;

/**
 * Created by WaiTuck on 29/01/2016.
 */
public class PsiUtility {
    public static Set<PsiPackage> getPsiTopLevelPackages(Project project){
        ProjectViewSettings viewSettings = new DefaultViewSettings();
        final List<VirtualFile> sourceRoots = new ArrayList<>();
        final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        ContainerUtil.addAll(sourceRoots, projectRootManager.getContentSourceRoots());

        final PsiManager psiManager = PsiManager.getInstance(project);
        final List<AbstractTreeNode> children = new ArrayList<>();
        final Set<PsiPackage> topLevelPackages = new HashSet<>();

        for (final VirtualFile root : sourceRoots) {
            final PsiDirectory directory = psiManager.findDirectory(root);
            if (directory == null) {
                continue;
            }
            final PsiPackage directoryPackage = JavaDirectoryService.getInstance().getPackage(directory);
            if (directoryPackage == null || PackageUtil.isPackageDefault(directoryPackage)) {
                // add subpackages
                final PsiDirectory[] subdirectories = directory.getSubdirectories();
                for (PsiDirectory subdirectory : subdirectories) {
                    final PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(subdirectory);
                    if (aPackage != null && !PackageUtil.isPackageDefault(aPackage)) {
                        topLevelPackages.add(aPackage);
                    }
                }
                // add non-dir items
                children.addAll(ProjectViewDirectoryHelper.getInstance(project).getDirectoryChildren(directory, viewSettings, false));
            } else {
                // this is the case when a source root has package prefix assigned
                topLevelPackages.add(directoryPackage);
            }
        }
        return topLevelPackages;
    }

    public static Set<PsiPackage> getPsiTopLevelPackages(AnActionEvent e){
        Project project = e.getProject();
        return getPsiTopLevelPackages(project);
    }

    public static ASTMatrix generateASTMatrix(Set<PsiPackage> topLevelPackages){
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
        return am;
    }

    public static ASTMatrix generateASTMatrix(AnActionEvent e){
        Set<PsiPackage> topLevelPackages = PsiUtility.getPsiTopLevelPackages(e);
        return generateASTMatrix(topLevelPackages);
    }

    public static ASTMatrix generateASTMatrix(Project p){
        Set<PsiPackage> topLevelPackages = PsiUtility.getPsiTopLevelPackages(p);
        return generateASTMatrix(topLevelPackages);
    }

    public static Set<PsiClass> getAllUserImplementedClasses(Set<PsiPackage> topLevelPackages){
        Set<PsiClass> psiClasses = new LinkedHashSet<>();
        for(PsiPackage pkg : topLevelPackages){
            getLeafPackages(pkg, psiClasses);
        }


        //add all psi inner classes before continuing
        Set<PsiClass> psiClassesWithInner = new LinkedHashSet<>();
        psiClassesWithInner.addAll(psiClasses);


        for(PsiClass c : psiClasses) {
            psiClassesWithInner.addAll(Arrays.asList(c.getInnerClasses()));
        }
        return psiClassesWithInner;

    }

    private static void getLeafPackages(PsiPackage root, Set<PsiClass> psiClasses){
        //collect all relevant classes along the way
        if(!root.getQualifiedName().contains("com.sun") && !root.getQualifiedName().contains("com.oracle")){
            psiClasses.addAll(Arrays.asList(root.getClasses()));
        }
        if(root.getSubPackages().length != 0){
            for(PsiPackage b : root.getSubPackages()){
               getLeafPackages(b, psiClasses);
            }
        }

    }

    public static Set<PsiIdentifier> convertPsiClassesToPsiIdentifiers(PsiClass[] psiClasses){
        Set<PsiIdentifier> classes = new LinkedHashSet<>();
        for(PsiClass psiClass : psiClasses){
            classes.add(psiClass.getNameIdentifier());
        }
        return classes;
    }
}
