package com.smu.graphme.service;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ApplicationComponent;
import com.smu.graphme.action.ChooseSeedSetAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by WaiTuck on 29/01/2016.
 */
public class ChoosetSeedSetServiceImpl implements ChooseSeedSetService, ApplicationComponent {
    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public void chooseSeedSet(AnActionEvent e) {
        //do some UI stuff
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Choose Seed Set";
    }
}
