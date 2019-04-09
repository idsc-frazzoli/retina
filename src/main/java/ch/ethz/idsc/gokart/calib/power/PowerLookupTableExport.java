// code by jph
package ch.ethz.idsc.gokart.calib.power;

import java.io.IOException;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum PowerLookupTableExport {
  ;
  public static void main(String[] args) throws IOException {
    int div = 1;
    Clip CLIP_VEL = Clips.interval( //
        Quantity.of(-10, SI.VELOCITY), //
        Quantity.of(+10, SI.VELOCITY));
    {
      Clip clipARMS = Clips.interval(Quantity.of(-2316, "ARMS"), Quantity.of(+2316, "ARMS"));
      Tensor si = Subdivide.increasing(clipARMS, 900 / div);
      Tensor sj = Subdivide.increasing(CLIP_VEL, 800 / div);
      Tensor matrix = //
          Tensors.matrix((i, j) -> PowerLookupTable.getInstance().getAcceleration(si.Get(i), sj.Get(j)), si.length(), sj.length()) //
              .map(Magnitude.ACCELERATION).map(Round._6);
      System.out.println(Dimensions.of(matrix));
      MatrixQ.require(matrix);
      Export.of(HomeDirectory.file("powerlookup", "forward", "cur_vel_to_acc.csv"), matrix);
      Export.of(HomeDirectory.file("powerlookup", "forward", "cur.csv"), si.map(Magnitude.ARMS));
      Export.of(HomeDirectory.file("powerlookup", "forward", "vel.csv"), sj.map(Magnitude.VELOCITY));
    }
    {
      Clip CLIP_ACC = Clips.interval( //
          Quantity.of(-2, SI.ACCELERATION), //
          Quantity.of(+2, SI.ACCELERATION));
      Tensor si = Subdivide.increasing(CLIP_ACC, 950 / div);
      Tensor sj = Subdivide.increasing(CLIP_VEL, 850 / div);
      // PowerLookupTable.getInstance().getNeededCurrent(wantedAcceleration, velocity);
      Tensor matrix = //
          Tensors.matrix((i, j) -> PowerLookupTable.getInstance().getNeededCurrent(si.Get(i), sj.Get(j)), si.length(), sj.length()) //
              .map(Magnitude.ARMS).map(Round._6);
      System.out.println(Dimensions.of(matrix));
      MatrixQ.require(matrix);
      Export.of(HomeDirectory.file("powerlookup", "inverse", "acc_vel_to_cur.csv"), matrix);
      Export.of(HomeDirectory.file("powerlookup", "inverse", "acc.csv"), si.map(Magnitude.ACCELERATION));
      Export.of(HomeDirectory.file("powerlookup", "inverse", "vel.csv"), sj.map(Magnitude.VELOCITY));
    }
  }
}
