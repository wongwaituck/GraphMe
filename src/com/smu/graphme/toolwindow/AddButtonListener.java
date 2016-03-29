package com.smu.graphme.toolwindow;

import com.intellij.psi.PsiIdentifier;
import com.sun.pisces.PiscesRenderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by WaiTuck on 22/03/2016.
 */
public class AddButtonListener implements ActionListener {
    private List<PsiIdentifier> currentSelectedList;
    private List<PsiIdentifier> originalList;
    private JList<PsiIdentifier> listOfPsis;
    private JList<PsiIdentifier> listOfSelectedPsis;

    public AddButtonListener(List<PsiIdentifier> currentSelectedList,
                             List<PsiIdentifier> originalList,
                             JList<PsiIdentifier> listOfPsis, JList<PsiIdentifier>  listOfSelectedPsis ) {
        this.currentSelectedList = currentSelectedList;
        this.originalList = originalList;
        this.listOfPsis = listOfPsis;
        this.listOfSelectedPsis = listOfSelectedPsis;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //look at current selected list
        List<PsiIdentifier> selectedValues = listOfPsis.getSelectedValuesList();
        //add to the current selected list
        currentSelectedList.addAll(selectedValues);
        listOfPsis.clearSelection();


        PsiIdentifier[] selected = new PsiIdentifier[currentSelectedList.size()];
        listOfSelectedPsis.setListData(currentSelectedList.toArray(selected));
        listOfSelectedPsis.updateUI();
    }
}
