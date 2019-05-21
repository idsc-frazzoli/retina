// code by jph
package ch.ethz.idsc.gokart.calib.power;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
    Clip CLIP_VEL = PowerLookupTable.CLIP_VEL;
    {
      Clip clipARMS = Clips.absolute(Quantity.of(+2316, "ARMS"));
      Tensor si = Subdivide.increasing(clipARMS, 900 / div);
      Tensor sj = Subdivide.increasing(CLIP_VEL, 800 / div);
      Tensor matrix = //
          Tensors.matrix((i, j) -> PowerLookupTable.getInstance().getAcceleration(si.Get(i), sj.Get(j)), si.length(), sj.length()) //
              .map(Magnitude.ACCELERATION).map(Round._6);
      MatrixQ.require(matrix);
      File folder = HomeDirectory.file("powerlookup", "forward");
      folder.mkdirs();
      Export.of(new File(folder, "cur_vel_to_acc.csv"), matrix);
      Export.of(new File(folder, "cur.csv"), si.map(Magnitude.ARMS));
      Export.of(new File(folder, "vel.csv"), sj.map(Magnitude.VELOCITY));
    }
    {
      Clip CLIP_ACC = PowerLookupTable.CLIP_ACC;
      Tensor si = Subdivide.increasing(CLIP_ACC, 950 / div);
      Tensor sj = Subdivide.increasing(CLIP_VEL, 850 / div);
      Tensor matrix = //
          Tensors.matrix((i, j) -> PowerLookupTable.getInstance().getNeededCurrent(si.Get(i), sj.Get(j)), si.length(), sj.length()) //
              .map(Magnitude.ARMS).map(Round._6);
      MatrixQ.require(matrix);
      File folder = HomeDirectory.file("powerlookup", "inverse");
      folder.mkdirs();
      Export.of(new File(folder, "acc_vel_to_cur.csv"), matrix);
      Export.of(new File(folder, "acc.csv"), si.map(Magnitude.ACCELERATION));
      Export.of(new File(folder, "vel.csv"), sj.map(Magnitude.VELOCITY));
    }
  }
}
