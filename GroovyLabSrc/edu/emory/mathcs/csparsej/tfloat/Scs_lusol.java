
package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;
import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scsn;
import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scss;

/**
 * Solve Ax=b using sparse LU factorization.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_lusol {

    /**
     * Solves Ax=b, where A is square and nonsingular. b overwritten with
     * solution. Partial pivoting if tol = 1.f
     * 
     * @param order
     *            ordering method to use (0 to 3)
     * @param A
     *            column-compressed matrix
     * @param b
     *            size n, b on input, x on output
     * @param tol
     *            partial pivoting tolerance
     * @return true if successful, false on error
     */
    public static boolean cs_lusol(int order, Scs A, float[] b, float tol) {
        float[] x;
        Scss S;
        Scsn N;
        int n;
        boolean ok;
        if (!Scs_util.CS_CSC(A) || b == null)
            return (false); /* check inputs */
        n = A.n;
        S = Scs_sqr.cs_sqr(order, A, false); /* ordering and symbolic analysis */
        N = Scs_lu.cs_lu(A, S, tol); /* numeric LU factorization */
        x = new float[n]; /* get workspace */
        ok = (S != null && N != null);
        if (ok) {
            Scs_ipvec.cs_ipvec(N.pinv, b, x, n); /* x = b(p) */
            Scs_lsolve.cs_lsolve(N.L, x); /* x = L\x */
            Scs_usolve.cs_usolve(N.U, x); /* x = U\x */
            Scs_ipvec.cs_ipvec(S.q, x, b, n); /* b(q) = x */
        }
        return (ok);
    }

}
