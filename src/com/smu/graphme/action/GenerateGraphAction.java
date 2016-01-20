package com.smu.graphme.action;

import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.model.DefaultViewSettings;
import com.smu.graphme.service.GenerateGraphService;

import java.util.*;

/**
 * Created by WaiTuck on 11/01/2016.
 */
public class GenerateGraphAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        GenerateGraphService generateGraphService = ServiceManager.getService(GenerateGraphService.class);
        generateGraphService.generateGraph(e);
        Messages.showMessageDialog("Graph generated in sysout!", "Information", Messages.getInformationIcon());

    }
}
