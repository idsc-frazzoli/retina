// code by jph
package ch.ethz.idsc.gokart.calib.power;

import java.io.IOException;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum MotorFunctionExport {
  ;
  private static final int RES = 200 - 1;

  static void of(MotorFunctionBase motorFunctionBase) throws IOException {
    Clip clip_powers = ManualConfig.GLOBAL.torqueLimitClip();
    Tensor powers = Subdivide.increasing(clip_powers, RES);
    Clip clip_speeds = Clips.absolute(Quantity.of(10, SI.VELOCITY));
    Tensor speeds = Subdivide.increasing(clip_speeds, RES);
    TableBuilder tableBuilder = new TableBuilder();
    for (Tensor _p : powers)
      for (Tensor _v : speeds) {
        Scalar acc = motorFunctionBase.getAccelerationEstimation(_p.Get(), _v.Get());
        tableBuilder.appendRow( //
            _p.map(Magnitude.ARMS).map(Round._2), //
            _v.map(Magnitude.VELOCITY).map(Round._6), //
            acc.map(Magnitude.ACCELERATION).map(Round._6));
      }
    Export.of(HomeDirectory.file(motorFunctionBase.getClass().getSimpleName() + ".csv"), tableBuilder.getTable());
  }

  public static void main(String[] args) throws IOException {
    of(MotorFunctionV1.INSTANCE);
    of(MotorFunctionV2.INSTANCE);
  }
}
