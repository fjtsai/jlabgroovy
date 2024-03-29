

package edu.emory.mathcs.csparsej.tdouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;

/**
 * Symmetric permutation of a sparse matrix.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Dcs_symperm {

    /**
     * Permutes a symmetric sparse matrix. C = PAP' where A and C are symmetric.
     * 
     * @param A
     *            column-compressed matrix (only upper triangular part is used)
     * @param pinv
     *            size n, inverse permutation
     * @param values
     *            allocate pattern only if false, values and pattern otherwise
     * @return C = PAP', null on error
     */
    public static Dcs cs_symperm(Dcs A, int[] pinv, boolean values) {
        int i, j, p, q, i2, j2, n, Ap[], Ai[], Cp[], Ci[], w[];
        double Cx[], Ax[];
        Dcs C;
        if (!Dcs_util.CS_CSC(A))
            return (null); /* check inputs */
        n = A.n;
        Ap = A.p;
        Ai = A.i;
        Ax = A.x;
        C = Dcs_util.cs_spalloc(n, n, Ap[n], values && (Ax != null), false); /* alloc result*/
        w = new int[n]; /* get workspace */
        Cp = C.p;
        Ci = C.i;
        Cx = C.x;
        for (j = 0; j < n; j++) /* count entries in each column of C */
        {
            j2 = pinv != null ? pinv[j] : j; /* column j of A is column j2 of C */
            for (p = Ap[j]; p < Ap[j + 1]; p++) {
                i = Ai[p];
                if (i > j)
                    continue; /* skip lower triangular part of A */
                i2 = pinv != null ? pinv[i] : i; /* row i of A is row i2 of C */
                w[Math.max(i2, j2)]++; /* column count of C */
            }
        }
        Dcs_cumsum.cs_cumsum(Cp, w, n); /* compute column pointers of C */
        for (j = 0; j < n; j++) {
            j2 = pinv != null ? pinv[j] : j; /* column j of A is column j2 of C */
            for (p = Ap[j]; p < Ap[j + 1]; p++) {
                i = Ai[p];
                if (i > j)
                    continue; /* skip lower triangular part of A*/
                i2 = pinv != null ? pinv[i] : i; /* row i of A is row i2 of C */
                Ci[q = w[Math.max(i2, j2)]++] = Math.min(i2, j2);
                if (Cx != null)
                    Cx[q] = Ax[p];
            }
        }
        return C;
    }

}
