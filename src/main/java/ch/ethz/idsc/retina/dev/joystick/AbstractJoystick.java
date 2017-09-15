// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.nio.ByteBuffer;

public abstract class AbstractJoystick {
  private final byte[] _axes;
  private short _buttons;
  private final byte[] _hats;

  public AbstractJoystick() {
    JoystickType joystickType = type();
    _axes = new byte[joystickType.axes];
    _hats = new byte[joystickType.hats];
  }

  protected final boolean isButtonPressed(int index) {
    // System.out.println(String.format("%04X", _buttons));
    return (_buttons & (1 << index)) != 0;
  }

  /** @param index of axis
   * @return value in unit interval [0, 1] */
  protected final double getAxisValue(int index) {
    return _axes[index] / (double) Byte.MAX_VALUE;
  }

  public final void printAxes() {
    for (int index = 0; index < _axes.length; ++index)
      System.out.print(String.format("%7d ", _axes[index]));
    System.out.println();
  }

  public final void decode(ByteBuffer byteBuffer) {
    // <- ordinal is read by JoystickDecoder
    for (int axis = 0; axis < _axes.length; ++axis)
      _axes[axis] = byteBuffer.get();
    _buttons = byteBuffer.getShort();
    byteBuffer.get(_hats);
  }

  public abstract JoystickType type();
}
