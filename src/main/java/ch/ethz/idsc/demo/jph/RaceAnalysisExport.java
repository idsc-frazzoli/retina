// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

/* package */ enum RaceAnalysisExport {
  ;
  public static void main(String[] args) throws IOException {
    Tensor points_xyr = Import.of(HomeDirectory.file("thetrackctrl.csv"));
    BSplineTrack bSplineTrack = BSplineTrack.of(points_xyr, true);
    // ---
    File folder = new File("/media/datahaki/data/gokart/0701mpc");
    for (File file : folder.listFiles()) {
      OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(GokartPoseChannel.INSTANCE);
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
        if (Scalars.lessThan(RealScalar.of(14), prev) && //
            Scalars.lessThan(next, RealScalar.of(1))) {
          offset = offset.add(RealScalar.of(15));
        }
        Scalar prog = next.add(offset);
        if (Scalars.lessThan(curr, prog)) {
          curr = prog;
          monoto.append(table.get(count).append(prog));
        }
      }
      // Tensor result = Transpose.of(Transpose.of(table).append(monoto));
      System.out.println(Dimensions.of(monoto));
      Export.of(HomeDirectory.Documents("racing", file.getName() + "_m.csv"), monoto);
    }
  }
}
