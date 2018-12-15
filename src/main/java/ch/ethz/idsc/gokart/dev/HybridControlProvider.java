// code by jph
package ch.ethz.idsc.gokart.dev;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class HybridControlProvider implements ManualControlProvider {
  private final LabjackAdcLcmClient labjackAdcLcmClient = new LabjackAdcLcmClient(GokartLcmChannel.LABJACK_U3_ADC, 0.2);
  private final JoystickLcmProvider joystickLcmProvider = new JoystickLcmProvider(GokartLcmChannel.JOYSTICK, 0.2);

  @Override
  public void start() {
    labjackAdcLcmClient.start();
    joystickLcmProvider.start();
  }

  @Override
  public void stop() {
    joystickLcmProvider.stop();
    labjackAdcLcmClient.stop();
  }

  @Override
  public Optional<GokartJoystickInterface> getJoystick() {
    System.out.println("here");
    Optional<GokartJoystickInterface> optional = joystickLcmProvider.getJoystick();
    if (optional.isPresent()) {
      System.out.println("  has joystick");
      GokartJoystickInterface joystick = optional.get();
      Optional<GokartJoystickInterface> optional_labjack = labjackAdcLcmClient.getJoystick();
      if (optional_labjack.isPresent()) {
        System.out.println("  has labjack adc");
        GokartJoystickInterface labjack = optional.get();
        Scalar ahead = labjack.getAheadAverage();
        System.out.println("   -> " + ahead);
        return Optional.of(new GokartJoystickAdapter( //
            joystick.getSteerLeft(), //
            joystick.getBreakStrength(), //
            ahead, //
            Tensors.of(ahead.zero(), ahead), //
            joystick.isAutonomousPressed(), //
            joystick.isResetPressed()));
      }
    }
    return Optional.empty();
  }
}
