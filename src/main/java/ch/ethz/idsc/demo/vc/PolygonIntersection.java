// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.DeleteDuplicates;
import ch.ethz.idsc.tensor.sca.Chop;

/** The purpose of this class is to find, if any, the polygon that results from the intersection
 * of two polygons specified as Tensors that contain the polygon's vertices coordinates in
 * counter clock-wise order. */
enum PolygonIntersection {
  ;
  public static Tensor of(Tensor clipper, Tensor subject) {
    return clipPolygon(subject, clipper);
  }

  private static Tensor clipPolygon(Tensor subject, Tensor clipper) {
    int len = clipper.length();
    Tensor tensor = subject.copy();
    for (int i = 0; i < len; ++i) {
      Tensor A = clipper.get((i + len - 1) % len);
      Tensor B = clipper.get(i);
      Tensor input = tensor;
      int len2 = input.length();
      tensor = Tensors.empty();
      for (int j = 0; j < len2; ++j) {
        Tensor P = input.get((j + len2 - 1) % len2);
        Tensor Q = input.get(j);
        if (isInside(A, B, Q)) {
          if (!isInside(A, B, P))
            tensor.append(intersection(A, B, P, Q));
          tensor.append(Q);
        } else //
        if (isInside(A, B, P))
          tensor.append(intersection(A, B, P, Q));
      }
    }
    return DeleteDuplicates.of(tensor);
  }

  /** @param a
   * @param b
   * @param c
   * @return sign of signed area of triangle spanned by a, b, c */
  private static boolean isInside(Tensor a, Tensor b, Tensor c) {
    Tensor ac = a.subtract(c);
    Tensor bc = b.subtract(c);
    return Scalars.lessThan( //
        ac.Get(1).multiply(bc.Get(0)), //
        ac.Get(0).multiply(bc.Get(1)) //
    );
  }

  // package for testing
  /* package */ static Tensor intersection(Tensor a, Tensor b, Tensor p, Tensor q) {
    Tensor ab = a.subtract(b);
    Tensor pq = p.subtract(q);
    Scalar denom = det(ab, pq);
    if (Chop._14.allZero(denom))
      throw TensorRuntimeException.of(a, b, p, q);
    return pq.multiply(det(ab, a)).subtract(ab.multiply(det(pq, p))).divide(denom);
  }

  private static Scalar det(Tensor p, Tensor q) {
    return Cross2D.of(p).dot(q).Get();
  }
}