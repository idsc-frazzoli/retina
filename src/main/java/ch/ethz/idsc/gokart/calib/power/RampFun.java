// code by mh, jph
package ch.ethz.idsc.gokart.calib.power;

// TODO JPH rename to AffineFun
/* package */ enum RampFun {
  ;
  static final float p0 = -0.3223f;
  static final float ppower = 0.001855f;
  static final float pvel = -0.0107f;

  static float of(float fpow, float fvel) {
    return p0 + ppower * fpow + pvel * fvel;
  }
}
