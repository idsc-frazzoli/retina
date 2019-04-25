// code by swisstrolley+ and jph
package ch.ethz.idsc.gokart.lcm;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** invokes lcm logger binary as Process that records all lcm-messages
 * into binary files for later playback */
public final class LoggerModule extends AbstractModule {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
  // ---
  private LcmLogProcess lcmLogProcess;
  private Date date;

  @Override // from AbstractModule
  protected void first() {
    try {
      date = new Date();
      File directory = HomeDirectory.file("gokartlogs", DATE_FORMAT.format(date));
      directory.mkdirs();
      lcmLogProcess = LcmLogProcess.createDefault(directory, date);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  @Override // from AbstractModule
  protected void last() {
    if (Objects.nonNull(lcmLogProcess))
      try {
        System.out.println(new Date() + " lcm-logger: destruction");
        lcmLogProcess.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public Date uptime() {
    return date;
  }
}