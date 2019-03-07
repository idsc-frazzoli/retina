// code by jph
package ch.ethz.idsc.retina.dvs.core;

import java.awt.Dimension;

import ch.ethz.idsc.retina.dvs.digest.DvsEventBuffer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Partition;

/** accumulate window of dvs points to gray scale image */
public enum DvsAccumulate {
  ;
  public static Tensor of( //
      DvsEventBuffer dvsEventBuffer, Dimension dimension, long time_us) {
    final long window_us = dvsEventBuffer.window_us();
    double[] factor = new double[] { -1.0 / window_us, 1.0 / window_us };
    double[][] image = new double[dimension.width][dimension.height];
    for (DvsEvent dvsEvent : dvsEventBuffer.collection()) {
      long impact = window_us - (time_us - dvsEvent.time_us);
      // LONGTERM investigate corner cases
      // if (age<0)
      // System.out.println("age negative "+age);
      image[dvsEvent.x][dvsEvent.y] += impact * factor[dvsEvent.i];
    }
    for (int x = 0; x < dimension.width; ++x)
      for (int y = 0; y < dimension.height; ++y)
        image[x][y] = 255.0 / (1 + Math.exp(-image[x][y]));
    return Tensors.matrixDouble(image);
  }

  public static Tensor rgb( //
      DvsEventBuffer dvsEventBuffer, Dimension dimension, long time_us) {
    final long window_us = dvsEventBuffer.window_us();
    double factor = 1.0 / window_us;
    double[][][] image = new double[dimension.width][dimension.height][2];
    for (DvsEvent dvsEvent : dvsEventBuffer.collection()) {
      long impact = window_us - (time_us - dvsEvent.time_us);
      if (impact < 0) {
        // ---
      } else
        image[dvsEvent.x][dvsEvent.y][dvsEvent.i] -= impact * factor;
    }
    Tensor rgba = Tensors.empty();
    for (int x = 0; x < dimension.width; ++x)
      for (int y = 0; y < dimension.height; ++y) {
        double dr = 1 - Math.exp(image[x][y][1]);
        double dg = 1 - Math.exp(image[x][y][0]);
        rgba.append(Tensors.vector( //
            255.0 * dr, //
            255.0 * dg, //
            0, //
            255.0
        // * Math.min(dr + dg,1)
        ));
      }
    rgba = Partition.of(rgba, dimension.width);
    return rgba;
  }
}
