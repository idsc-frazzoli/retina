// code from Computer Graphics, by Donald Hearn and Pauline Baker
// adapted by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.Color;

public enum HueColor {
  ;
  /** @param h is periodically mapped to [0, 1)
   * @param s in [0, 1]
   * @param v in [0, 1]
   * @param a in [0, 1] */
  public static Color of(double h, double s, double v, double a) {
    final double r;
    final double g;
    final double b;
    if (s == 0) {
      r = g = b = v;
    } else {
      h %= 1;
      if (h < 0)
        h += 1;
      h *= 6;
      int i = (int) h;
      double f = h - i;
      double aa = v * (1 - s);
      double bb = v * (1 - s * f);
      double cc = v * (1 - s * (1 - f));
      switch (i) {
      case 0:
        r = v;
        g = cc;
        b = aa;
        break;
      case 1:
        r = bb;
        g = v;
        b = aa;
        break;
      case 2:
        r = aa;
        g = v;
        b = cc;
        break;
      case 3:
        r = aa;
        g = bb;
        b = v;
        break;
      case 4:
        r = cc;
        g = aa;
        b = v;
        break;
      case 5:
      default:
        r = v;
        g = aa;
        b = bb;
        break;
      }
    }
    return new Color((float) r, (float) g, (float) b, (float) a);
  }
}
