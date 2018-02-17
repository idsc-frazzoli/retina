// code by jph
package ch.ethz.idsc.retina.offline.slam;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum OfflineLocalizeResources implements OfflineLocalizeResource {
  TEST(new File("src/test/resources/localization", "vlp16.center.ray_autobox.rimo.get.lcm"), //
      Tensors.vector(56.137, 57.022, -1.09428)), //
  OVAL(UserHome.file("temp/20180108T162528_5f742add.lcm.00.extract"), //
      Tensors.vector(40.32, 51.02, 0.818226)), //
  BRAKE6(UserHome.file("temp/20171213T162832_brake6.lcm"), //
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
