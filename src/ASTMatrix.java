import com.intellij.psi.PsiClass;

/**
 * Created by WaiTuck on 15/01/2016.
 */
public class ASTMatrix {
    private int[][] referenceMatrix;
    private PsiClass[] psiClasses;

    public ASTMatrix(PsiClass[] psiClasses){
        this.psiClasses = psiClasses;
        referenceMatrix = new int[psiClasses.length][psiClasses.length];
        constructMatrix();
    }

    private void constructMatrix(){

    }
}
