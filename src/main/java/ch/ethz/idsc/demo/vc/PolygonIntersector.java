// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** The purpose of this class is to find, if any, the polygon that results from the intersection
 * of two polygons specified as Tensors that contain the polygon's vertices coordinates in
 * counter clock-wise order. */
enum PolygonIntersector {
  ;
  public static Tensor polygonIntersect(Tensor clip, Tensor subj) {
    // Tensor clip1 = Reverse.of(clip);
    // Tensor subj1 = Reverse.of(subj); // to handle clockwise inputs
    Tensor result = clipPolygon(subj, clip);
    return result;
  }

  private static Tensor clipPolygon(Tensor subject, Tensor clipper) {
    int len = clipper.length();
    Tensor res = subject;
    for (int i = 0; i < len; i++) {
      Tensor A = clipper.get((i + len - 1) % len);
      Tensor B = clipper.get(i);
      Tensor input = res;
      int len2 = input.length();
      res = Tensors.empty();
      for (int j = 0; j < len2; j++) {
        Tensor P = input.get((j + len2 - 1) % len2);
        Tensor Q = input.get(j);
        if (isInside(A, B, Q)) {
          if (!isInside(A, B, P))
            res.append(intersection(A, B, P, Q));
          res.append(Q);
        } else //
        if (isInside(A, B, P)) {
          res.append(intersection(A, B, P, Q));
        }
      }
    }
    return res;
  }

  // sign of signed area of triangle spanned by a, b, c
  private static boolean isInside(Tensor a, Tensor b, Tensor c) {
    Tensor ac = a.subtract(c);
    Tensor bc = b.subtract(c);
    return Scalars.lessThan( //
        ac.Get(1).multiply(bc.Get(0)), //
        ac.Get(0).multiply(bc.Get(1)) //
    );
  }

  private static Tensor intersection(Tensor a, Tensor b, Tensor p, Tensor q) {
    Tensor ab = a.subtract(b);
    Tensor pq = p.subtract(q);
    return pq.multiply(det(ab, a)).subtract(ab.multiply(det(pq, p))).divide(det(ab, pq));
  }

  private static Scalar det(Tensor p, Tensor q) {
    return Cross2D.of(p).dot(q).Get();
  }
}