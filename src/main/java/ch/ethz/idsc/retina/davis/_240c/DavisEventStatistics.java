// code by jph
package ch.ethz.idsc.retina.davis._240c;

import ch.ethz.idsc.retina.davis.DavisApsListener;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis.DavisEvent;
import ch.ethz.idsc.retina.davis.DavisImuListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class DavisEventStatistics implements //
    DavisDvsListener, DavisApsListener, DavisImuListener {
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
  private long time_last = 0;
  private long jump = 0;

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    _trackTime(davisDvsEvent);
    ++dvs;
  }

  @Override
  public void davisAps(DavisApsEvent davisApsEvent) {
    _trackTime(davisApsEvent);
    ++aps;
    if (davisApsEvent.x == 0 && davisApsEvent.y == 0)
      ++frames;
  }

  @Override
  public void davisImu(DavisImuEvent davisImuEvent) {
    _trackTime(davisImuEvent);
    ++imu;
    // System.out.println(String.format("%08x", imuDavisEvent.data));
    // System.out.println(imuDavisEvent.time+" "+String.format("%08x %d",
    // imuDavisEvent.data, imuDavisEvent.index));
  }

  private void _trackTime(DavisEvent davisEvent) {
    int time = davisEvent.time();
    time_min = Math.min(time, time_min);
    time_max = Math.max(time, time_max);
    if (time < time_last)
      ++jump;
    time_last = time;
  }

  public Tensor eventCount() {
    return Tensors.vector(dvs, aps, imu);
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
    if (0 < jump)
      System.err.println(String.format("jmp:%10d", jump));
    System.out.println("---");
    System.out.println(String.format("eps:%10d", Math.round(dvs / total)));
    System.out.println(String.format("fps:%13.2f", frames / total));
    System.out.println("---");
    System.out.println(String.format("duration%10.3f [s]", total));
    System.out.println(String.format("processi%10.3f [s]", timer));
    System.out.println(String.format("realtime%10.3f", timer / total));
  }
}
