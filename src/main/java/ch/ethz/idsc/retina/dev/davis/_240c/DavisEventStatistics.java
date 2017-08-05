// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import ch.ethz.idsc.retina.dev.davis.ApsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis.DvsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis.ImuDavisEventListener;

public class DavisEventStatistics implements //
    DvsDavisEventListener, ApsDavisEventListener, ImuDavisEventListener {
  private static final double SEC_USEC = 1e-6;
  private static final double SEC_NSEC = 1e-9;
  // ---
  private final long tic = System.nanoTime();
  private long time_min = Long.MAX_VALUE;
  private long time_max = 0;
  private long dvs = 0;
  private long aps = 0;
  private long imu = 0;
  private long frames = 0;

  @Override
  public void dvs(DvsDavisEvent dvsDavisEvent) {
    time_min = Math.min(dvsDavisEvent.time, time_min);
    time_max = Math.max(dvsDavisEvent.time, time_max);
    ++dvs;
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    time_min = Math.min(apsDavisEvent.time, time_min);
    time_max = Math.max(apsDavisEvent.time, time_max);
    ++aps;
    if (apsDavisEvent.x == 0 && apsDavisEvent.y == 0)
      ++frames;
  }

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
    time_min = Math.min(imuDavisEvent.time, time_min);
    time_max = Math.max(imuDavisEvent.time, time_max);
    ++imu;
    // System.out.println(String.format("%08x", imuDavisEvent.data));
    // System.out.println(imuDavisEvent.time+" "+String.format("%08x %d", imuDavisEvent.data, imuDavisEvent.index));
  }

  public void print() {
    final double total = (time_max - time_min) * SEC_USEC;
    final double timer = (System.nanoTime() - tic) * SEC_NSEC;
    System.out.println(DavisEventStatistics.class.getName());
    System.out.println(String.format("beg:%10d", time_min));
    System.out.println(String.format("end:%10d", time_max));
    System.out.println(String.format("dvs:%10d", dvs));
    System.out.println(String.format("aps:%10d", aps));
    System.out.println(String.format("imu:%10d", imu));
    System.out.println(String.format("img:%10d", frames));
    System.out.println("---");
    System.out.println(String.format("eps:%10d", Math.round(dvs / total)));
    System.out.println(String.format("fps:%13.2f", frames / total));
    System.out.println("---");
    System.out.println(String.format("duration%10.3f [s]", total));
    System.out.println(String.format("processi%10.3f [s]", timer));
    System.out.println(String.format("realtime%10.3f", timer / total));
  }
}
