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

        ASTMatrix am = PsiUtility.generateASTMatrix(e);

        am.printDependencyMatrix();

    }




}
