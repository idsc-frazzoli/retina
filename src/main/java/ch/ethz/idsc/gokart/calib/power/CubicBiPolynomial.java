// code by mh and jph
package ch.ethz.idsc.gokart.calib.power;

/** the polynomial depends on two variables: x and y
 * the polynomial is defined by 10 coeffients
 * and evaluates all terms up to degree 3. */
/* package */ class CubicBiPolynomial {
  private final float p00;
  private final float p10;
  private final float p01;
  private final float p20;
  private final float p11;
  private final float p02;
  private final float p30;
  private final float p21;
  private final float p12;
  private final float p03;

  public CubicBiPolynomial( //
      float p00, //
      float p10, float p01, //
      float p20, float p11, float p02, //
      float p30, float p21, float p12, float p03) {
    this.p00 = p00;
    // ---
    this.p10 = p10;
    this.p01 = p01;
    // ---
    this.p20 = p20;
    this.p11 = p11;
    this.p02 = p02;
    // ---
    this.p30 = p30;
    this.p21 = p21;
    this.p12 = p12;
    this.p03 = p03;
  }

  public float evaluate(float x, float y) {
    float x2 = x * x;
    float y2 = y * y;
    return p00 // constant
        + p10 * x + p01 * y // linear
        + p20 * x2 + p11 * x * y + p02 * y2 // quadratic
        + p30 * x2 * x + p21 * x2 * y + p12 * x * y2 + p03 * y2 * y; // cubic
  }
}
