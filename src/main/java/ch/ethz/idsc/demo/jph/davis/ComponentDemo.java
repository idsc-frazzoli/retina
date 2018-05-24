// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.io.DavisTxtFileSupplier;
import ch.ethz.idsc.retina.dev.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.dev.dvs.digest.DvsEventComponents;
import ch.ethz.idsc.retina.dev.dvs.digest.DvsEventLast;
import ch.ethz.idsc.retina.dev.dvs.digest.DvsEventStatistics;
import ch.ethz.idsc.retina.util.gui.ShapeHelper;
import ch.ethz.idsc.retina.util.math.Constant;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.opt.ConvexHull;

/** visualization of log files from Robotics and Perception Group
 * http://rpg.ifi.uzh.ch/ */
enum ComponentDemo {
  ;
  public static void main(String[] args) throws Exception {
    final Dimension dimension = ImageDimensions.UZ;
    DvsEventStatistics stats = new DvsEventStatistics();
    DvsEventLast del = new DvsEventLast();
    DvsEventComponents dec = new DvsEventComponents(dimension, del);
    final int maxsize = dimension.height * dimension.width;
    Tensor palette = //
        Tensors.vector(i -> Constant.GOLDEN_ANGLE.value.multiply(DoubleScalar.of(i / (2 * Math.PI))), maxsize);
    BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    File file = new File("/media/datahaki/media/ethz/davis/shapes_6dof", //
        "events.txt");
    final int rate_us = 30_000;
    try (AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("components2.gif"), rate_us / 1000)) {
      try (DavisTxtFileSupplier sup = new DavisTxtFileSupplier(file, dimension)) {
        // File file = new
        // File("/media/datahaki/media/ethz/dvs/wp.doc.ic.ac.uk_pb2114_datasets", //
        // "jumping.dat");
        // DvsEventSupplier sup = new DatFileSupplier(file, dimension);
        // DvsEventBuffer buf = new DvsEventBuffer(10000);
        // DatFileDigest dfd = new DatFileDigest(UserHome.file("test.dat"));
        int count = 0;
        long next = rate_us;
        while (count < 1000) {
          DvsEvent dvsEvent = sup.next();
          stats.digest(dvsEvent);
          del.digest(dvsEvent);
          dec.digest(dvsEvent);
          long time = dvsEvent.time_us;
          if (next <= time) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, dimension.width, dimension.height);
            Tensor group = Tensors.vector(i -> Tensors.empty(), maxsize);
            graphics.setColor(Color.GRAY);
            for (int x = 0; x < dimension.width; ++x)
              for (int y = 0; y < dimension.height; ++y) {
                int rep = dec.at(x, y);
                if (0 <= rep) {
                  Tensor pix = Tensors.vector(x, y);
                  group.set(s -> s.append(pix), rep);
                  Shape rect = new Rectangle(x, y, 2, 2);
                  graphics.fill(rect);
                }
              }
            int index = 0;
            for (Tensor ph : group)
              if (2 <= ph.length()) {
                Tensor hull = ConvexHull.of(ph);
                ++index;
                double h = palette.Get(index).number().doubleValue();
                graphics.setColor(Hue.of(h, 1, 1, .5));
                graphics.fill(ShapeHelper.path(hull));
              }
            gsw.append(bufferedImage);
            del = new DvsEventLast();
            dec = new DvsEventComponents(dimension, del);
            ++count;
            next += rate_us;
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
    stats.printSummary();
  }
}
