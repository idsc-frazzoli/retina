// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public class AutoboxGetServer implements StartAndStoppable, //
    RimoGetListener, LinmotGetListener, SteerGetListener, MiscGetListener {
  // ---
  private final BinaryBlobPublisher publisher = new BinaryBlobPublisher("autobox.gokart.get");
  private final byte[] data = //
      new byte[2 * RimoGetEvent.LENGTH + LinmotGetEvent.LENGTH + SteerGetEvent.LENGTH + MiscGetEvent.LENGTH];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private Timer timer;
  // ---
  private RimoGetEvent rimoGetEventL;
  private RimoGetEvent rimoGetEventR;
  private LinmotGetEvent linmotGetEvent;
  private SteerGetEvent steerGetEvent;
  private MiscGetEvent miscGetEvent;

  public AutoboxGetServer() {
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override
  public void rimoGet(RimoGetEvent rimoGetEventL, RimoGetEvent rimoGetEventR) {
    this.rimoGetEventL = rimoGetEventL;
    this.rimoGetEventR = rimoGetEventR;
  }

  @Override
  public void linmotGet(LinmotGetEvent linmotGetEvent) {
    this.linmotGetEvent = linmotGetEvent;
  }

  @Override
  public void steerGet(SteerGetEvent steerGetEvent) {
    this.steerGetEvent = steerGetEvent;
  }

  @Override
  public void miscGet(MiscGetEvent miscGetEvent) {
    this.miscGetEvent = miscGetEvent;
  }

  @Override
  public void start() {
    stop();
    // ---
    timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        if (Objects.nonNull(rimoGetEventL) //
            && Objects.nonNull(rimoGetEventR) //
            && Objects.nonNull(linmotGetEvent) //
            && Objects.nonNull(steerGetEvent) //
            && Objects.nonNull(miscGetEvent)) {
          rimoGetEventL.encode(byteBuffer);
          rimoGetEventR.encode(byteBuffer);
          linmotGetEvent.encode(byteBuffer);
          steerGetEvent.encode(byteBuffer);
          miscGetEvent.encode(byteBuffer);
          publisher.accept(data, data.length);
        }
      }
    };
    timer.schedule(timerTask, 50, 50); // TODO magic const
  }

  @Override
  public void stop() {
    if (Objects.nonNull(timer)) {
      timer.cancel();
      timer = null;
    }
  }
}
