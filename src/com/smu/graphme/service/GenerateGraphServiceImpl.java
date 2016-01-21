package com.smu.graphme.service;

import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper;
import com.intellij.ide.projectView.impl.nodes.PsiMethodNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.util.containers.ContainerUtil;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.model.DefaultViewSettings;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by WaiTuck on 11/01/2016.
 */
public class GenerateGraphServiceImpl implements GenerateGraphService, ApplicationComponent {
    private Set<PsiClass> psiClasses  = new LinkedHashSet<>();

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

        for(PsiPackage pkg : topLevelPackages){
            for(PsiPackage a : getLeafPackages(pkg)){}
        }

        ASTMatrix am = new ASTMatrix(new ArrayList<>(psiClasses));
        for(PsiClass c : psiClasses){

            //resolve implementation and extends list
            PsiClass[] psiClasses = c.getSupers();
            PsiIdentifier currPi = c.getNameIdentifier();
            for(PsiClass pc : psiClasses){
                PsiIdentifier pi = pc.getNameIdentifier();
                am.setDependency(currPi, pi);
            }

            //resolve field references
            PsiField[] psiFields = c.getAllFields();
            for(PsiField psiField : psiFields){
                if(psiField != null) {
                    PsiIdentifier pi = getPsiIdentifier(psiField);
                    am.setDependency(currPi, pi);
                }
            }

            //resolve method dependencies
            PsiMethod[] psiMethods = c.getMethods();
            for(PsiMethod psiMethod : psiMethods){
                //resolve parameter dependencies
                PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
                for(PsiParameter pp : psiParameters) {
                    PsiIdentifier pi = getPsiIdentifier(pp.getTypeElement());
                    if (pi != null) {
                        am.setDependency(currPi, pi);
                    }
                }

                //resolve return type dependencies
                PsiIdentifier returnTypei = getPsiIdentifier(psiMethod.getReturnTypeElement());
                am.setDependency(currPi, returnTypei);

                //resolve method body dependencies
                PsiCodeBlock pcb = psiMethod.getBody();
                if(pcb != null) {
                    PsiStatement[] statements = pcb.getStatements();
                    for(PsiStatement statement: statements){

                        PsiElement[] childElements = statement.getChildren();

                        for(PsiElement childElement : childElements){
                            if(childElement instanceof PsiLocalVariable){
                                //add declaration type to dependency
                                PsiLocalVariable plv = (PsiLocalVariable) childElement;

                                PsiElement[] plvChildren = plv.getChildren();
                                for(PsiElement plvChild : plvChildren){
                                    if(plvChild instanceof PsiMethodCallExpressionImpl){
                                        PsiMethodCallExpressionImpl methodImpl = (PsiMethodCallExpressionImpl) plvChild;
                                        PsiMethod method = methodImpl.resolveMethod();

                                        //resolve owning class
                                        PsiIdentifier owningClazz = method.getContainingClass().getNameIdentifier();
                                        am.setDependency(currPi, owningClazz);

                                        //resolve parameters
                                        PsiParameter[] params = method.getParameterList().getParameters();
                                        for(PsiParameter param : params){
                                            PsiIdentifier paramClazz = getPsiIdentifier(param.getTypeElement());
                                            am.setDependency(currPi, paramClazz);
                                        }
                                    }
                                }

                                PsiIdentifier pi = getPsiIdentifier(plv.getTypeElement());
                                am.setDependency(currPi, pi);
                            }

                        }
                    }
                }
            }
                /*

                ASTNode astnode = c.getLBrace().getContext().getNode();
                ASTNode[] nodes = astnode.getChildren(null);
                for(ASTNode node: nodes){
                    ProcessNodeStrategy pns = ProcessNodeStrategyFactory.getNodeStrategy(node.getElementType());
                    pns.processNode(node);
                }
                */
        }
        am.printDependencyMatrix();

    }

    private PsiIdentifier getPsiIdentifier(PsiField field){
        for(PsiClass pc : psiClasses){
            if(field.getTypeElement() != null && pc.getName().equals(field.getTypeElement().getText())){
                return pc.getNameIdentifier();
            }
        }
        return null;
    }

    private PsiIdentifier getPsiIdentifier(PsiTypeElement typeElement){
        if(typeElement == null){
            return null;
        }

        for(PsiClass pc : psiClasses){
            if(pc.getName().equals(typeElement.getText())){
                return pc.getNameIdentifier();
            }
        }
        return null;
    }

    private PsiPackage[] getLeafPackages(PsiPackage root){
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
                psiPackages.addAll(Arrays.asList(getLeafPackages(b)));
            }
            PsiPackage[] packages = new PsiPackage[psiPackages.size()];
            return psiPackages.toArray(packages);
        }

    }

}
