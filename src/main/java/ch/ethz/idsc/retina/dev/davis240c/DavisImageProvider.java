// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public class DavisImageProvider implements DavisEventListener {
  private List<DavisImageListener> davisImageListeners = new LinkedList<>();
  private Tensor image = Array.zeros(240, 180);

  public void addListener(DavisImageListener davisImageListener) {
    davisImageListeners.add(davisImageListener);
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    image.set(RationalScalar.of((1023 - apsDavisEvent.adc) >> 2, 1), apsDavisEvent.x, 179 - apsDavisEvent.y);
    // if (apsDavisEvent.y == 0)
    // System.out.println(apsDavisEvent);
    if (apsDavisEvent.x == 239 && apsDavisEvent.y == 0)
      davisImageListeners.forEach(l -> l.image(apsDavisEvent.time, image));
  }

  @Override
  public void dvs(DvsDavisEvent apsDavisEvent) {
  }

  @Override
  public void imu(ImuDavisEvent apsDavisEvent) {
  }
}
