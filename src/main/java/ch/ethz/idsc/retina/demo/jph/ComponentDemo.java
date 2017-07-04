// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.awt.Dimension;
import java.io.File;

import ch.ethz.idsc.retina.core.DvsEvent;
import ch.ethz.idsc.retina.digest.DvsEventComponents;
import ch.ethz.idsc.retina.digest.DvsEventLast;
import ch.ethz.idsc.retina.digest.DvsEventStatistics;
import ch.ethz.idsc.retina.io.txt.TxtFileSupplier;
import ch.ethz.idsc.retina.supply.DvsEventSupplier;
import ch.ethz.idsc.retina.util.gui.Hue;
import ch.ethz.idsc.retina.util.io.ImageDimensions;
import ch.ethz.idsc.retina.util.io.UserHome;
import ch.ethz.idsc.retina.util.math.Constant;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.GifSequenceWriter;

enum ComponentDemo {
  ;
  public static void main(String[] args) throws Exception {
    final Dimension dimension = ImageDimensions.UZ;
    DvsEventStatistics stats = new DvsEventStatistics();
    DvsEventLast del = new DvsEventLast();
    DvsEventComponents dec = new DvsEventComponents(dimension, del);
    Tensor palette = //
        Tensors.vector(i -> Constant.GoldenAngle.value.multiply(DoubleScalar.of(i / (2 * Math.PI))), dimension.height * dimension.width);
    GifSequenceWriter gsw = null;
    try {
      // File file = new File("/media/datahaki/media/ethz/dvs/wp.doc.ic.ac.uk_pb2114_datasets", //
      // "jumping.dat");
      File file = new File("/media/datahaki/media/ethz/davis/shapes_6dof", //
          "events.txt");
      // DvsEventSupplier sup = new DatFileSupplier(file, dimension);
      DvsEventSupplier sup = new TxtFileSupplier(file, dimension);
      // DvsEventBuffer buf = new DvsEventBuffer(10000);
      // DatFileDigest dfd = new DatFileDigest(UserHome.file("test.dat"));
      int count = 0;
      final int rate_us = 30_000;
      long next = rate_us;
      gsw = GifSequenceWriter.of(UserHome.Pictures("components.gif"), rate_us / 1000);
      while (count < 5000) {
        DvsEvent dvsEvent = sup.next();
        stats.digest(dvsEvent);
        // dfd.digest(dvsEvent);
        // buf.digest(dvsEvent);
        del.digest(dvsEvent);
        dec.digest(dvsEvent);
        long time = dvsEvent.time_us;
        if (next <= time) {
          Tensor image = Array.zeros(dimension.width, dimension.height, 4);
          for (int x = 0; x < dimension.width; ++x)
            for (int y = 0; y < dimension.height; ++y) {
              int rep = dec.at(x, y);
              if (0 <= rep) {
                double h = palette.Get(rep).number().doubleValue();
                Hue hue = new Hue(h, 1, 1, 1);
                image.set(ColorFormat.toVector(hue.color), x, y);
              }
              // System.out.println(dvsEventComponents.reps().size());
              // System.out.println("mapsize=" + dvsEventLast.keys().size());
              image.set(RealScalar.of(255), x, y, 3);
            }
          // Export.of(UserHome.Pictures("demo" + count + ".png"), image);
          gsw.append(image);
          del = new DvsEventLast();
          dec = new DvsEventComponents(dimension, del);
          ++count;
          next += rate_us;
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    gsw.close();
    stats.printSummary();
  }
}
