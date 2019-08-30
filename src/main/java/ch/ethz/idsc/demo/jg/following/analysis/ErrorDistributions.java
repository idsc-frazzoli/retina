// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.io.IOException;

import org.jfree.chart.ChartUtils;

import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.sophus.util.plot.ListPlot;
import ch.ethz.idsc.sophus.util.plot.VisualRow;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.BinCounts;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum ErrorDistributions {
  ;
  private static final int WIDTH = 900;
  private static final int HEIGHT = 600;

  public static void plot(Tensor[] errors, String[] sources, String[] errorTypes, Scalar[] binSizes) throws IOException {
    GlobalAssert.that(errors.length == sources.length);
    GlobalAssert.that(errors[0].length() == errorTypes.length);
    GlobalAssert.that(errorTypes.length == binSizes.length);
    // ---
    for (int i = 0; i < errorTypes.length; i++) {
      String errorType = errorTypes[i];
      Scalar binSize = binSizes[i];
      VisualSet visualSet = new VisualSet();
      visualSet.setAxesLabelY("distribution [%]");
      visualSet.setAxesLabelX(errorType + (binSize instanceof Quantity ? " [" + ((Quantity) binSize).unit() + "]" : ""));
      for (int k = 0; k < sources.length; k++) {
        String source = sources[k];
        Tensor err = errors[k].get(i);
        Tensor binCounter = BinCounts.of(err, binSize);
        binCounter = binCounter.divide(RealScalar.of(err.length())).multiply(RealScalar.of(100));
        VisualRow visualRow = visualSet.add(Range.of(0, binCounter.length()).multiply(binSize), binCounter);
        visualRow.setLabel(source);
      }
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures(errorType + ".png"), ListPlot.of(visualSet), WIDTH, HEIGHT);
    }
  }
}
