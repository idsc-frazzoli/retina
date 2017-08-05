// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

public class DavisEventStatistics implements //
    DvsDavisEventListener, ApsDavisEventListener, ImuDavisEventListener {
  private static final double SEC_USEC = 1e-6;
  // ---
  private long time_min = Long.MAX_VALUE;
  private long time_max = 0;
  private long dvs = 0;
  private long aps = 0;
  private long imu = 0;

  @Override
  public void dvs(DvsDavisEvent dvsDavisEvent) {
    time_min = Math.min(dvsDavisEvent.time, time_min);
    time_max = Math.max(dvsDavisEvent.time, time_max);
    ++dvs;
    // System.out.println(dvsDavisEvent);
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    time_min = Math.min(apsDavisEvent.time, time_min);
    time_max = Math.max(apsDavisEvent.time, time_max);
    ++aps;
    // if (apsDavisEvent.x == 239)
    // System.out.println(apsDavisEvent);
  }

  private int lastimutime = -1;

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
    // TODO
    // time_min = Math.min(imuDavisEvent.time, time_min);
    ++imu;
    if (lastimutime != imuDavisEvent.time) {
      System.out.println("---");
      lastimutime = imuDavisEvent.time;
    }
    // System.out.println(String.format("%08x", imuDavisEvent.data));
    // System.out.println(imuDavisEvent.time+" "+String.format("%08x %d", imuDavisEvent.data, imuDavisEvent.index));
  }

  public void print() {
    System.out.println(String.format("beg:%10d", time_min));
    System.out.println(String.format("end:%10d", time_max));
    System.out.println(String.format("dvs:%10d", dvs));
    System.out.println(String.format("aps:%10d", aps));
    System.out.println(String.format("imu:%10d", imu));
    System.out.println("---");
    System.out.println(String.format("duration %.3f [s]", (time_max - time_min) * SEC_USEC));
    // TODO show processing time
  }
}
