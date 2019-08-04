// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.retina.app.map.ErodableMap;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** free space module always runs in the background
 * 
 * other modules that require free space information subscribe
 * to the instance of {@link OccupancyMappingModule} to obtain
 * an eroded snapshot of the current obstacles. */
public class OccupancyMappingModule extends AbstractModule {
  private final ErodableMap erodableMap;
  private final List<AbstractModule> abstractModules = new CopyOnWriteArrayList<>();

  public OccupancyMappingModule() {
    // TODO JPH this is dubilab specific an will be moved to config area
    BufferedImage bufferedImage = new BufferedImage(160, 80, BufferedImage.TYPE_BYTE_GRAY);
    Tensor model2pixel = Dot.of( //
        Se2Matrix.of(Tensors.vector(32, 20, Math.PI / 4)), //
        DiagonalMatrix.of( //
            38.4 / bufferedImage.getWidth(), //
            19.2 / bufferedImage.getHeight(), 1), //
        Se2Matrix.flipY(bufferedImage.getHeight()));
    erodableMap = new ErodableMap(bufferedImage, model2pixel);
  }

  @Override
  protected void first() {
    // TODO Auto-generated method stub
  }

  @Override
  protected void last() {
    // TODO Auto-generated method stub
  }

  public void subscribe(AbstractModule abstractModule) {
    abstractModules.add(abstractModule);
  }

  public void unsubscribe(AbstractModule abstractModule) {
    abstractModules.remove(abstractModule);
  }

  public BufferedImageRegion erodedMap(int radius) {
    return erodableMap.erodedRegion(radius);
  }
}
