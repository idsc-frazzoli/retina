// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum Dubilab {
  ;
  public static final RenderInterface GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
  // ---
  private static final Tensor _20190401 = Tensors.of( //
      Tensors.vector(3.6677994336284594, 3.5436206505034793, -190.05265224432887), //
      Tensors.vector(3.5436206505034793, -3.6677994336284594, 74.03647376620074), //
      Tensors.vector(0.0, 0.0, 1.0)).unmodifiable();

  public static BackgroundImage backgroundImage20190408() {
    return new BackgroundImage(ResourceData.bufferedImage("/dubilab/obstacles/20190408.png"), _20190401);
  }
}
