// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Objects;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Channel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Listener;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931_DPS;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931_G;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public class Vmu931LcmServerModule extends AbstractModule implements Vmu931Listener {
  private static final String PORT = "/dev/ttyACM0";
  // ---
  private Vmu931 vmu931;

  @Override
  protected void first() throws Exception {
    vmu931 = new Vmu931(PORT, //
        EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
        Vmu931_DPS._250, Vmu931_G._2, this);
  }

  @Override
  protected void last() {
    if (Objects.nonNull(vmu931))
      vmu931.close();
  }

  @Override
  public void accelerometer(ByteBuffer byteBuffer) {
    System.out.println("acc");
  }

  @Override
  public void gyroscope(ByteBuffer byteBuffer) {
    System.out.println("gryo");
  }
}
