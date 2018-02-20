// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum OfflineLocalizeResources implements GokartLogInterface {
  TEST(new File("src/test/resources/localization", "vlp16.center.ray_autobox.rimo.get.lcm"), //
      Tensors.vector(56.137, 57.022, -1.09428)), //
  BRAKE6(UserHome.file("gokart/localquick/20171213T162832_brake6/log.lcm"), //
      Tensors.vector(46.58, 48.54, 1.248986)), //
  ;
  // ---
  private final File file;
  private final Tensor xya;

  private OfflineLocalizeResources(File file, Tensor xya) {
    this.file = file;
    this.xya = xya;
  }

  @Override
  public File file() {
    return file;
  }

  @Override
  public Tensor model() {
    return Se2Utils.toSE2Matrix(xya);
  }
}
