// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ enum Dubilab {
  ;
  public static final RenderInterface GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
}
