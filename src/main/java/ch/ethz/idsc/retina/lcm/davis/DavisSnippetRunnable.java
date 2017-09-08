// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.io.File;

import ch.ethz.idsc.retina.lcm.LcmLogProcess;
import ch.ethz.idsc.retina.util.GlobalAssert;

public abstract class DavisSnippetRunnable implements Runnable {
  private final int milliSeconds;

  public DavisSnippetRunnable(int milliSeconds) {
    this.milliSeconds = milliSeconds;
  }

  @Override
  public void run() {
    try {
      LcmLogProcess lcmLogProcess = LcmLogProcess.createDefault();
      File file = lcmLogProcess.file();
      System.out.println(file);
      Thread.sleep(milliSeconds);
      lcmLogProcess.close();
      GlobalAssert.that(file.isFile());
      callback(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /** function called before exiting run() function
   * 
   * @param file */
  public abstract void callback(File file);
}
