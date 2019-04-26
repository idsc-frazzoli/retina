// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.opt.Pi;

/*package*/ enum RnUnitCircle {
  ;
  public static Scalar convert(Scalar angleOut) {
    while (!(Scalars.lessEquals(angleOut, Pi.VALUE) && Scalars.lessEquals(Pi.VALUE.negate(), angleOut))) {
      if (Scalars.lessEquals(Pi.VALUE, angleOut)) {
        angleOut = angleOut.add(Pi.TWO.negate());        
      }
      else if (Scalars.lessEquals(angleOut, Pi.VALUE.negate())) {
        angleOut = angleOut.add(Pi.TWO);
      }
    }
    return angleOut;
  }
}
