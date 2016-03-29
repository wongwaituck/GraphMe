package com.smu.graphme.toolwindow;

import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.model.ASTMatrix;
import com.smu.graphme.util.PsiUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by WaiTuck on 26/02/2016.
 */
public class GenerateDependentSetButtonListener implements ActionListener {
    private List<PsiIdentifier> selectedList;
    private ASTMatrix am;

    public GenerateDependentSetButtonListener(List<PsiIdentifier> selectedList, ASTMatrix am){
        this.selectedList = selectedList;
        this.am = am;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        am.generateFromSeedSet(selectedList);
    }


}
