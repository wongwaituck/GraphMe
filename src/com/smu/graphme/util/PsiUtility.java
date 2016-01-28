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
import com.smu.graphme.model.DefaultViewSettings;

import java.util.*;

/**
 * Created by WaiTuck on 29/01/2016.
 */
public class PsiUtility {
    public static Set<PsiPackage> getPsiTopLevelPackages(AnActionEvent e){
        Project project = e.getProject();
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

    public static Set<PsiClass> getAllUserImplementedClasses(Set<PsiPackage> topLevelPackages){
        Set<PsiClass> psiClasses = new LinkedHashSet<>();
        for(PsiPackage pkg : topLevelPackages){
            for(PsiPackage a : getLeafPackages(pkg, psiClasses)){}
        }


        //add all psi inner classes before continuing
        Set<PsiClass> psiClassesWithInner = new LinkedHashSet<>();
        psiClassesWithInner.addAll(psiClasses);


        for(PsiClass c : psiClasses) {
            psiClassesWithInner.addAll(Arrays.asList(c.getAllInnerClasses()));
        }

        return psiClassesWithInner;

    }

    private static PsiPackage[] getLeafPackages(PsiPackage root, Set<PsiClass> psiClasses){
        //collect all relevant classes along the way
        List<PsiPackage> psiPackages = new ArrayList<>();
        if(!root.getQualifiedName().contains("com.sun") && !root.getQualifiedName().contains("com.oracle")){
            psiClasses.addAll(Arrays.asList(root.getClasses()));
        }
        if(root.getSubPackages().length == 0){
            PsiPackage[] nodeContainer = new PsiPackage[1];
            nodeContainer[0] = root;
            return nodeContainer;
        } else{

            for(PsiPackage b : root.getSubPackages()){
                psiPackages.addAll(Arrays.asList(getLeafPackages(b, psiClasses)));
            }
            PsiPackage[] packages = new PsiPackage[psiPackages.size()];
            return psiPackages.toArray(packages);
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
