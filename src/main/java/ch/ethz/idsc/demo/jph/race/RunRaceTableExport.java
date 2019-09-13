// code by jph
package ch.ethz.idsc.demo.jph.race;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;

/** used in analysis of race on 20190701 between human driver and dynamic mpc
 * 
 * https://github.com/idsc-frazzoli/retina/files/3492127/20190812_autonomous_human_racing.pdf */
/* package */ enum RunRaceTableExport {
  ;
  public static void main(String[] args) throws IOException {
    Tensor points_xyr = ResourceData.of("/dubilab/analysis/track/20190912.csv");
    final int n = points_xyr.length();
    System.out.println("n=" + n);
    File dest = HomeDirectory.Documents("manual");
    dest.mkdir();
    BSplineTrack bSplineTrack = BSplineTrack.of(points_xyr, true);
    // ---
    File folder = new File("/media/datahaki/data/gokart/racing/20190912");
    for (File file : folder.listFiles()) {
      OfflineTableSupplier offlineTableSupplier = new RaceTableExport();
      OfflineLogPlayer.process(new File(file, "log.lcm"), offlineTableSupplier);
      Tensor table = offlineTableSupplier.getTable();
      Tensor tensor = Tensor.of(table.stream() //
          .map(row -> row.extract(1, 3)) //
          .map(xy -> bSplineTrack.getNearestPathProgress(xy)));
      Tensor monoto = Tensors.empty();
      Scalar offset = RealScalar.of(0);
      // monoto.append(tensor.get(0));
      Scalar curr = RealScalar.of(-1000);
      for (int count = 1; count < tensor.length(); ++count) {
        Scalar prev = tensor.Get(count - 1);
        Scalar next = tensor.Get(count - 0);
        if (Scalars.lessThan(RealScalar.of(n - 1), prev) && //
            Scalars.lessThan(next, RealScalar.of(1))) {
          offset = offset.add(RealScalar.of(n));
        }
        Scalar prog = next.add(offset);
        if (Scalars.lessThan(curr, prog)) {
          curr = prog;
          monoto.append(table.get(count).append(prog));
        }
      }
      // Tensor result = Transpose.of(Transpose.of(table).append(monoto));
      System.out.println(Dimensions.of(monoto));
      Export.of(new File(dest, file.getName() + ".csv"), monoto);
    }
  }
}
