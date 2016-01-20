package com.smu.graphme.model;

import com.intellij.ide.projectView.ProjectViewSettings;

/**
 * Created by WaiTuck on 21/01/2016.
 */
public class DefaultViewSettings implements ProjectViewSettings {
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
}
