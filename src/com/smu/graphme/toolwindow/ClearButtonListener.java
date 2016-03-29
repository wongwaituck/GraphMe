package com.smu.graphme.toolwindow;

import com.intellij.psi.PsiIdentifier;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by WaiTuck on 22/03/2016.
 */
public class ClearButtonListener implements ActionListener {
    private List<PsiIdentifier> currentSelectedList;
    private JList<PsiIdentifier> listOfSelectedPsis;

    public ClearButtonListener(List<PsiIdentifier> currentSelectedList, JList<PsiIdentifier>  listOfSelectedPsis ) {
        this.currentSelectedList = currentSelectedList;

        this.listOfSelectedPsis = listOfSelectedPsis;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //look at current selected list
        List<PsiIdentifier> selectedValues = listOfSelectedPsis.getSelectedValuesList();
        //add to the current selected list
        for(PsiIdentifier pi : selectedValues){
            currentSelectedList.remove(pi);
        }

        listOfSelectedPsis.setListData(selectedValues.toArray(new PsiIdentifier[selectedValues.size()]));
        listOfSelectedPsis.updateUI();
    }
}
