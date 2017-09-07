// code by jph
package ch.ethz.idsc.retina.demo.az;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.retina.lcm.LcmLogProcess;
import idsc.BinaryBlob;
import idsc.DavisImu;
import lcm.logging.Log;
import lcm.logging.Log.Event;

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
      extractImagesEtc(file);
      callback();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public abstract void callback();

  private static void extractImagesEtc(File file) {
    try {
      Log log = new Log(file.toString() + ".00", "r"); // TODO
      Set<String> set = new HashSet<>();
      while (true) {
        Event event = log.readNext();
        if (set.add(event.channel))
          System.out.println(event.channel);
        if (event.channel.endsWith(".imu")) {
          DavisImu davisImu = new DavisImu(event.data);
        }
        if (event.channel.endsWith(".sig")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
        }
        if (event.channel.endsWith(".rst")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
        }
        if (event.channel.endsWith(".dvs")) {
          BinaryBlob binaryBlob = new BinaryBlob(event.data);
        }
      }
    } catch (IOException exception) {
      System.out.println("done");
    }
  }
}
