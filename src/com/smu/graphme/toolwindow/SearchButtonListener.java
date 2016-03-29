package com.smu.graphme.toolwindow;

import com.intellij.psi.PsiIdentifier;
import com.smu.graphme.model.ASTMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by WaiTuck on 18/03/2016.
 */
public class SearchButtonListener implements ActionListener {
    private List<PsiIdentifier> psis;
    private JList<PsiIdentifier> listOfPsis;
    private JTextField tf;
    private ASTMatrix am;

    public SearchButtonListener(JTextField tf, List<PsiIdentifier> psis, JList<PsiIdentifier> listOfPsis){
        this.tf = tf;
        this.psis = psis;
        this.listOfPsis = listOfPsis;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        String searchText = tf.getText();
        List<PsiIdentifier> filteredList = new ArrayList<>();
        for(int i = 0; i < psis.size(); i++) {
            PsiIdentifier pi = psis.get(i);

            if(pi.toString().contains(searchText)){
                filteredList.add(pi);
            }
        }

        PsiIdentifier[] identifiers = new PsiIdentifier[filteredList.size()];
        listOfPsis.setListData(filteredList.toArray(identifiers));
        listOfPsis.updateUI();
    }
}
