// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Dimension;
import java.io.File;

import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** produces a high resolution image with lidar obstacles */
/* package */ enum VideoBackground {
  ;
  public static final Dimension DIMENSION = new Dimension(1920, 1080);
  // public static final Tensor MODEL2PIXEL = Tensors.fromString("{{50,0,-1000},{0,-50,3000},{0,0,1}}");
  // public static final Tensor MODEL2PIXEL = Tensors.fromString(
  // "{{21.57529078604976, 20.84482735590282, -1091.4861896725226}, {20.84482735590282, -21.57529078604976, 364.92043391882794}, {0.0, 0.0, 1.0}}");
  /** large */
  public static final Tensor _20190311 = Se2Utils.toSE2Translation(Tensors.vector(0, +200)).dot(DiagonalMatrix.of(0.9, 0.9, 1).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}")));
  /** dustproof wall */
  public static final Tensor _20190401 = Se2Utils.toSE2Translation(Tensors.vector(0, +120)).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}"));
  // ---
  public static final File IMAGE_FILE = HomeDirectory.Pictures("20190401T101109_00.png");
}
