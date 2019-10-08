// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;

/* package */ enum Vmu931Bias {
  ;
  public static void main(String[] args) throws IOException {
    File directory = new File("/media/datahaki/data/gokart/acc/20190921T124329_04");
    {
      Tensor acc = Import.of(new File(directory, "csv/vmu931.ag.vehicle.csv.gz"));
      Tensor vmu = Tensor.of(acc.stream().map(row -> row.extract(2, 5)));
      Tensor fil = CenterFilter.of(BiinvariantMeanCenter.of(RnBiinvariantMean.INSTANCE, GaussianWindow.FUNCTION), 24).apply(vmu);
      Export.of(HomeDirectory.Documents("vmu931acc", "imuav.csv.gz"), fil);
      System.out.println(Dimensions.of(vmu));
    }
    {
      Tensor pos = Import.of(new File(directory, "csv/gokart.pose.lidar.csv.gz"));
      System.out.println(Dimensions.of(pos));
      Tensor se2 = Tensor.of(pos.stream().map(row -> row.extract(1, 4)));
      Tensor se2d0 = CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION), 12).apply(se2);
      System.out.println(Dimensions.of(se2d0));
      Tensor se2d1 = Se2Differences.INSTANCE.apply(se2d0);
      System.out.println(Dimensions.of(se2d1));
      Tensor se2d2 = Differences.of(se2d1);
      System.out.println(Dimensions.of(se2d2));
      Export.of(HomeDirectory.Documents("vmu931acc", "se2d0.csv.gz"), se2d0);
      Export.of(HomeDirectory.Documents("vmu931acc", "se2d1.csv.gz"), se2d1);
      Export.of(HomeDirectory.Documents("vmu931acc", "se2d2.csv.gz"), se2d2);
    }
  }
}
