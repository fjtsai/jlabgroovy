

package edu.emory.mathcs.csparsej.tfloat;

import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scs;
import edu.emory.mathcs.csparsej.tfloat.Scs_common.Scss;

/**
 * Symbolic QR or LU ordering and analysis.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class Scs_sqr {
    /* compute nnz(V) = S->lnz, S->pinv, S->leftmost, S->m2 from A and S->parent */
    private static boolean cs_vcount(Scs A, Scss S) {
        int i, k, p, pa, n = A.n, m = A.m, Ap[] = A.p, Ai[] = A.i, next[], head[], tail[], nque[], pinv[], leftmost[], w[], parent[] = S.parent;
        S.pinv = pinv = new int[m + n]; /* allocate pinv, */
        S.leftmost = leftmost = new int[m]; /* and leftmost */
        w = new int[m + 3 * n]; /* get workspace */
        next = w;
        head = w;
        int head_offset = m;
        tail = w;
        int tail_offset = m + n;
        nque = w;
        int nque_offset = m + 2 * n;
        for (k = 0; k < n; k++)
            head[head_offset + k] = -1; /* queue k is empty */
        for (k = 0; k < n; k++)
            tail[tail_offset + k] = -1;
        for (k = 0; k < n; k++)
            nque[nque_offset + k] = 0;
        for (i = 0; i < m; i++)
            leftmost[i] = -1;
        for (k = n - 1; k >= 0; k--) {
            for (p = Ap[k]; p < Ap[k + 1]; p++) {
                leftmost[Ai[p]] = k; /* leftmost[i] = min(find(A(i,:)))*/
            }
        }
        for (i = m - 1; i >= 0; i--) /* scan rows in reverse order */
        {
            pinv[i] = -1; /* row i is not yet ordered */
            k = leftmost[i];
            if (k == -1)
                continue; /* row i is empty */
            if (nque[nque_offset + k]++ == 0)
                tail[tail_offset + k] = i; /* first row in queue k */
            next[i] = head[head_offset + k]; /* put i at head of queue k */
            head[head_offset + k] = i;
        }
        S.lnz = 0;
        S.m2 = m;
        for (k = 0; k < n; k++) /* find row permutation and nnz(V)*/
        {
            i = head[head_offset + k]; /* remove row i from queue k */
            S.lnz++; /* count V(k,k) as nonzero */
            if (i < 0)
                i = S.m2++; /* add a fictitious row */
            pinv[i] = k; /* associate row i with V(:,k) */
            if (--nque[nque_offset + k] <= 0)
                continue; /* skip if V(k+1:m,k) is empty */
            S.lnz += nque[nque_offset + k]; /* nque [nque_offset+k] is nnz (V(k+1:m,k)) */
            if ((pa = parent[k]) != -1) /* move all rows to parent of k */
            {
                if (nque[nque_offset + pa] == 0)
                    tail[tail_offset + pa] = tail[tail_offset + k];
                next[tail[tail_offset + k]] = head[head_offset + pa];
                head[head_offset + pa] = next[i];
                nque[nque_offset + pa] += nque[nque_offset + k];
            }
        }
        for (i = 0; i < m; i++)
            if (pinv[i] < 0)
                pinv[i] = k++;
        w = null;
        return (true);
    }

    /**
     * Symbolic QR or LU ordering and analysis.
     * 
     * @param order
     *            ordering method to use (0 to 3)
     * @param A
     *            column-compressed matrix
     * @param qr
     *            analyze for QR if true or LU if false
     * @return symbolic analysis for QR or LU, null on error
     */
    public static Scss cs_sqr(int order, Scs A, boolean qr) {
        int n, k, post[];
        Scss S;
        boolean ok = true;
        if (!Scs_util.CS_CSC(A))
            return (null); /* check inputs */
        n = A.n;
        S = new Scss(); /* allocate result S */
        S.q = Scs_amd.cs_amd(order, A); /* fill-reducing ordering */
        if (order > 0 && S.q == null)
            return (null);
        if (qr) /* QR symbolic analysis */
        {
            Scs C = order > 0 ? Scs_permute.cs_permute(A, null, S.q, false) : A;
            S.parent = Scs_etree.cs_etree(C, true); /* etree of C'*C, where C=A(:,q) */
            post = Scs_post.cs_post(S.parent, n);
            S.cp = Scs_counts.cs_counts(C, S.parent, post, true); /* col counts chol(C'*C) */
            ok = C != null && S.parent != null && S.cp != null && cs_vcount(C, S);
            if (ok)
                for (S.unz = 0, k = 0; k < n; k++)
                    S.unz += S.cp[k];
            ok = ok && S.lnz >= 0 && S.unz >= 0; /* int overflow guard */
        } else {
            S.unz = 4 * (A.p[n]) + n; /* for LU factorization only, */
            S.lnz = S.unz; /* guess nnz(L) and nnz(U) */
        }
        return (ok ? S : null); /* return result S */
    }
}
