package com.smu.graphme.model;

import com.intellij.psi.PsiIdentifier;

/**
 * Created by WaiTuck on 13/04/2016.
 */
public class DependsOnRelationship {
    private PsiIdentifier seedModule;
    private PsiIdentifier dependentModule;

    public DependsOnRelationship(PsiIdentifier seedModule, PsiIdentifier dependentModule){
        this.seedModule = seedModule;
        this.dependentModule = dependentModule;
    }

    public PsiIdentifier getSeedModule(){
        return seedModule;
    }

    public PsiIdentifier getDependentModule(){
        return dependentModule;
    }

    @Override
    public int hashCode(){
        return seedModule.hashCode() + dependentModule.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof DependsOnRelationship){
            DependsOnRelationship dr = (DependsOnRelationship) other;
            return dr.seedModule.equals(seedModule) && dr.dependentModule.equals(dependentModule);
        }
        return false;
    }

}
