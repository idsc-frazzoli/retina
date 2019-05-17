// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class QueuedManualControl implements ManualControlInterface {
  private final BoundedLinkedList<ManualControlInterface> boundedLinkedList;
  private ManualControlInterface last;

  public QueuedManualControl(int maxSize) {
    this.boundedLinkedList = new BoundedLinkedList<>(maxSize);
  }

  public void add(ManualControlInterface manualControlInterface) {
    last = manualControlInterface;
    boundedLinkedList.add(manualControlInterface);
  }

  @Override
  public Scalar getSteerLeft() {
    return last.getSteerLeft();
  }

  @Override
  public Scalar getBreakStrength() {
    return last.getBreakStrength();
  }

  @Override
  public Scalar getAheadAverage() {
    return last.getAheadAverage();
  }

  @Override
  public Tensor getAheadPair_Unit() {
    return last.getAheadPair_Unit();
  }

  @Override
  public boolean isAutonomousPressed() {
    return boundedLinkedList.stream() //
        .allMatch(ManualControlInterface::isAutonomousPressed);
  }

  @Override
  public boolean isResetPressed() {
    return boundedLinkedList.stream() //
        .allMatch(ManualControlInterface::isResetPressed);
  }
}
