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
  OVAL(UserHome.file("temp/20180108T162528_5f742add.lcm.00.extract"), //
      Tensors.vector(40.32, 51.02, 0.818226)), //
  // BRAKE1(UserHome.file("temp/20171213T162832_brake1.lcm"), //
  // Tensors.vector(41.99, 49.20, 0.4424784)), //
  // BRAKE2(UserHome.file("temp/20171213T162832_brake2.lcm"), //
  // Tensors.vector(48.77, 52.24, -1.9115421)), //
  // // BRAKE3(UserHome.file("temp/20171213T162832_brake3.lcm"), //
  // // Tensors.vector(44.44, 51.09, 0.17353867)), //
  // BRAKE4(UserHome.file("temp/20171213T162832_brake4.lcm"), //
  // Tensors.vector(41.55, 53.21, -2.229745)), //
  BRAKE5(UserHome.file("temp/20171213T162832_brake5.lcm"), //
      Tensors.vector(43.33, 51.18, -2.398181)), //
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
