package com.smu.graphme.util.graphstrategy;

import com.intellij.psi.*;
import com.smu.graphme.model.ASTMatrix;

import java.util.Set;

/**
 * Created by WaiTuck on 24/01/2016.
 */
public abstract class GraphStrategy {
    private PsiElement e;

    public GraphStrategy(PsiElement e){
        this.e = e;
    }

    public abstract void handleCase(ASTMatrix am, PsiIdentifier currPi, Set<PsiClass> psiClasses);

    public PsiElement getPsiElement(){
        return e;
    }

    public static PsiIdentifier getPsiIdentifier(PsiField field, Set<PsiClass> psiClasses){
        for(PsiClass pc : psiClasses){
            if(field.getTypeElement() != null && pc.getQualifiedName().equals(field.getTypeElement().getType().getCanonicalText())){
                return pc.getNameIdentifier();
            }
        }
        return null;
    }

    public static PsiIdentifier getPsiIdentifier(PsiType type, Set<PsiClass> psiClasses){
        if(type instanceof PsiWildcardType){
            PsiWildcardType pwt = (PsiWildcardType) type;
            return getPsiIdentifier(pwt.getBound(), psiClasses);
        }
        for(PsiClass pc : psiClasses){
            if(type != null && pc.getQualifiedName().equals(type.getCanonicalText())){

                return pc.getNameIdentifier();
            }
        }
        return null;
    }

    public static PsiIdentifier getPsiIdentifier(PsiJavaCodeReferenceElement type, Set<PsiClass> psiClasses){

        for(PsiClass pc : psiClasses){
            if(type != null && pc.getQualifiedName().equals(type.getQualifiedName())){

                return pc.getNameIdentifier();
            }
        }
        return null;
    }

    public static PsiIdentifier getPsiIdentifier(PsiTypeElement typeElement, Set<PsiClass> psiClasses){
        if(typeElement == null){
            return null;
        }

        for(PsiClass pc : psiClasses){
            if( pc.getQualifiedName().equals(typeElement.getType().getCanonicalText())){
                return pc.getNameIdentifier();
            }
        }
        return null;
    }

}
