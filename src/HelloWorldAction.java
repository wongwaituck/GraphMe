import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;

import java.util.*;

/**
 * Created by WaiTuck on 11/01/2016.
 */
public class HelloWorldAction extends AnAction {
    private List<PsiClass> psiClasses  = new ArrayList<>();
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        ProjectViewSettings viewSettings = new ProjectViewSettings() {
            @Override
            public boolean isShowExcludedFiles() {
                return false;
            }

            @Override
            public boolean isShowMembers() {
                return false;
            }

            @Override
            public boolean isStructureView() {
                return false;
            }

            @Override
            public boolean isShowModules() {
                return false;
            }

            @Override
            public boolean isFlattenPackages() {
                return false;
            }

            @Override
            public boolean isAbbreviatePackageNames() {
                return false;
            }

            @Override
            public boolean isHideEmptyMiddlePackages() {
                return false;
            }

            @Override
            public boolean isShowLibraryContents() {
                return false;
            }
        };
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
            for(PsiClass c : psiClasses){
                ASTNode astnode = c.getLBrace().getContext().getNode();
                ASTNode[] nodes = astnode.getChildren(null);
                for(ASTNode node: nodes){
                    System.out.println(node);
                }
            }
        }

        DayOfWeekService dayOfWeekService = ServiceManager.getService(DayOfWeekService.class);
        System.out.println("Hello World Action Performed!");
        Messages.showMessageDialog(dayOfWeekService.getDayOfWeek(), "Information", Messages.getInformationIcon());

    }

    private PsiPackage[] getLeafPackages(PsiPackage root){
        //collect all relevant classes along the way
        List<PsiPackage> psiPackages = new ArrayList<>();
        if(root.getSubPackages().length == 0){
            PsiPackage[] nodeContainer = new PsiPackage[1];
            nodeContainer[0] = root;
            return nodeContainer;
        } else{
            for(PsiPackage b : root.getSubPackages()){
                if(!b.getQualifiedName().contains("com.sun") && !b.getQualifiedName().contains("com.oracle")){
                    psiClasses.addAll(Arrays.asList(b.getClasses()));
                }
                psiPackages.addAll(Arrays.asList(getLeafPackages(b)));
            }
        }
        PsiPackage[] packages = new PsiPackage[psiPackages.size()];
        return psiPackages.toArray(packages);
    }
}
