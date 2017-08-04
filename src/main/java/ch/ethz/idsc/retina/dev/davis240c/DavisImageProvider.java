// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.DoubleScalar;
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
    // TODO some aps conversion fails because of IMU?!
    if (apsDavisEvent.x < 240 && apsDavisEvent.y < 180) {
      image.set(DoubleScalar.of(apsDavisEvent.adc * 0.2490234375), apsDavisEvent.x, 179 - apsDavisEvent.y);
      if (apsDavisEvent.x == 239 && apsDavisEvent.y == 0)
        davisImageListeners.forEach(l -> l.image(apsDavisEvent.time, image));
    }
  }

  @Override
  public void dvs(DvsDavisEvent apsDavisEvent) {
  }

  @Override
  public void imu(ImuDavisEvent apsDavisEvent) {
  }
}
