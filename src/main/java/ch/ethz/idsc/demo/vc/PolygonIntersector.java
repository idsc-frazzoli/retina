// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;

/** The purpose of this class is to find, if any, the polygon that results from the intersection
 * of two polygons specified as Tensors that contain the polygon's vertices coordinates. */
enum PolygonIntersector {
  ;
  public static Tensor polygonIntersect(Tensor clip, Tensor subj) {
    // Tensor clip1 = reverse(clip);
    Tensor subj1 = Reverse.of(subj); // to handle clockwise inputs
    Tensor result = clipPolygon(subj1, clip);
    return result;
  }

  // private static Tensor reverse(Tensor x) {
  // Tensor s = Tensors.empty();
  // for (int i = x.length() - 1; i >= 0; i--) {
  // s.append(x.get(i));
  // }
  // return s;
  // }
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
    // return (a.Get(0).number().doubleValue() - c.Get(0).number().doubleValue()) //
    // * (b.Get(1).number().doubleValue() - c.Get(1).number().doubleValue()) //
    // > (a.Get(1).number().doubleValue() - c.Get(1).number().doubleValue()) //
    // * (b.Get(0).number().doubleValue() - c.Get(0).number().doubleValue());
  }

  private static Tensor intersection(Tensor a, Tensor b, Tensor p, Tensor q) {
    double a1 = a.Get(1).number().doubleValue();
    double a0 = a.Get(0).number().doubleValue();
    double A1 = b.Get(1).number().doubleValue() - a1;
    double B1 = a0 - b.Get(0).number().doubleValue();
    double C1 = A1 * a0 + B1 * a1;
    double p0 = p.Get(0).number().doubleValue();
    double p1 = p.Get(1).number().doubleValue();
    double A2 = q.Get(1).number().doubleValue() - p1;
    double B2 = p0 - q.Get(0).number().doubleValue();
    double C2 = A2 * p0 + B2 * p1;
    double det = A1 * B2 - A2 * B1;
    double x = (B2 * C1 - B1 * C2) / det;
    double y = (A1 * C2 - A2 * C1) / det;
    return Tensors.vectorDouble(x, y);
  }
}