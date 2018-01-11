// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.io.File;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.lcm.LcmLogProcess;

public abstract class DavisSnippetRunnable implements Runnable {
  private final int milliSeconds;
  private final File directory;

  public DavisSnippetRunnable(int milliSeconds, File directory) {
    this.milliSeconds = milliSeconds;
    this.directory = directory;
  }

  @Override
  public void run() {
    try (LcmLogProcess lcmLogProcess = LcmLogProcess.createDefault(directory)) {
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
