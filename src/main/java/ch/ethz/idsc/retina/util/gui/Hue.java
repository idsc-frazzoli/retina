// code from Computer Graphics, by Donald Hearn and Pauline Baker
// adapted by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.Color;

@Deprecated
public class Hue {
  public static final double red = 0;
  public static final double green = 1 / 3.;
  public static final double blue = 2 / 3.;
  // ---
  /** in [0,1] */
  public final double h;
  /** in [0,1] */
  public final double s;
  /** in [0,1] */
  public final double v;
  /** in [0,1] */
  public final double a;
  public final Color color;

  /** @param h is periodically mapped to [0, 1)
   * @param s in [0, 1]
   * @param v in [0, 1]
   * @param a in [0, 1] */
  public Hue(double h, double s, double v, double a) {
    this.h = h;
    this.s = s;
    this.v = v;
    this.a = a;
    color = HueColor.of(h, s, v, a);
  }

  private static final double fraction1o255 = 1. / 255.;

  public static Hue fromColor(final Color myColor) {
    int r = myColor.getRed();
    int g = myColor.getGreen();
    int b = myColor.getBlue();
    int min = Math.min(r, Math.min(g, b));
    int max = Math.max(r, Math.max(g, b));
    double del = max - min;
    final double s = max == 0 ? 0 : del / max;
    double h;
    if (s == 0) {
      h = 0;
    } else {
      if (r == max) {
        int dif = g - b;
        h = (dif < 0 ? 6 : 0) + dif / del;
      } else //
      if (g == max)
        h = 2 + (b - r) / del;
      else
        // b == max
        h = 4 + (r - g) / del;
      h /= 6;
    }
    return new Hue(h, s, max * fraction1o255, myColor.getAlpha() * fraction1o255);
  }

  @Override
  public String toString() {
    return "(" + h + "," + s + "," + v + "," + a + ")";
  }

  public String toFriendlyString() {
    return String.format("(%5.3f,%5.3f,%5.3f,%5.3f)", h, s, v, a);
  }
}
