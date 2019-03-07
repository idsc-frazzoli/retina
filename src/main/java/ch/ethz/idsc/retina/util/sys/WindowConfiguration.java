// code by jph
package ch.ethz.idsc.retina.util.sys;

import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Objects;

import javax.swing.JFrame;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;

public class WindowConfiguration implements Serializable {
  public Tensor bounds = Tensors.vector(100, 100, 800, 800);

  // ---
  public void setBounds(Rectangle rectangle) {
    bounds = Tensors.vector(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  public Rectangle getBounds() {
    return new Rectangle( //
        bounds.Get(0).number().intValue(), //
        bounds.Get(1).number().intValue(), //
        bounds.Get(2).number().intValue(), //
        bounds.Get(3).number().intValue());
  }

  private final Point shift = new Point();

  public void attach(Class<?> cls, JFrame jFrame) {
    jFrame.setBounds(getBounds());
    WindowConfiguration windowConfig = this;
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        Rectangle rectangle = jFrame.getBounds();
        rectangle.x -= shift.x;
        rectangle.y -= shift.y;
        windowConfig.setBounds(rectangle);
        AppCustomization.save(cls, windowConfig);
      }
    });
    jFrame.addComponentListener(new ComponentAdapter() {
      final Timing timing = Timing.stopped();
      Point shown = null;

      @Override
      public void componentShown(ComponentEvent componentEvent) {
        try {
          shown = jFrame.getLocationOnScreen();
        } catch (IllegalComponentStateException illegalComponentStateException) {
          illegalComponentStateException.printStackTrace();
        }
        timing.start();
      }

      @Override
      public void componentMoved(ComponentEvent componentEvent) {
        long nanos = timing.nanoSeconds(); // 45846090
        // System.out.println("ns=" + nanos);
        if (nanos < 300_000_000 && Objects.nonNull(shown)) {
          Point moved = jFrame.getLocationOnScreen();
          // System.out.println("moved location: " + jFrame.getLocationOnScreen());
          shift.x = moved.x - shown.x;
          shift.y = moved.y - shown.y;
          if (shift.x != 0) {
            System.err.println("shift=" + shift + " -> reset");
            shift.x = 0;
            shift.y = 0;
          }
        } else {
          System.err.println("nanos=" + nanos);
        }
        jFrame.removeComponentListener(this);
      }
    });
  }
}
