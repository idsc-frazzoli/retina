// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.joystick.JoystickType;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** post 201812XY: the gokart is maually controlled using the labjack u3 interface */
public class GenericXboxPadLcmServerModule extends AbstractModule {
  /** refresh period 20[ms] for joystick events resulting in an update rate of 50[Hz] */
  public static final int PERIOD_MS = 20;
  // ---
  private JoystickLcmServer joystickLcmServer;

  @Override
  protected final void first() {
    joystickLcmServer = new JoystickLcmServer( //
        JoystickType.RADICA_GAMESTER, //
        GokartLcmChannel.JOYSTICK, //
        PERIOD_MS);
    joystickLcmServer.start();
  }

  @Override
  protected final void last() {
    if (Objects.nonNull(joystickLcmServer)) {
      joystickLcmServer.stop();
      joystickLcmServer = null;
    }
  }
}
