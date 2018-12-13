// code by jph
package ch.ethz.idsc.retina.dev.u3;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** Labjack U3
 * readout ADC */
public class LabjackU3LiveProvider implements StartAndStoppable, Runnable {
  /* package */ static final File DIRECTORY = UserHome.file("Public");
  /* package */ static final File EXECUTABLE = new File(DIRECTORY, "");
  /** 2 bytes header, 8 bytes timestamp, each point as short */
  private Process process;

  @Override // from StartAndStoppable
  public void start() { // non-blocking
    ProcessBuilder processBuilder = new ProcessBuilder(EXECUTABLE.toString());
    processBuilder.directory(DIRECTORY);
    try {
      process = processBuilder.start();
      System.out.println("urg_alive1=" + process.isAlive());
      Thread thread = new Thread(this);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      InputStream inputStream = process.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      System.out.println("urg_alive2=" + process.isAlive());
      while (process.isAlive()) {
        String line = bufferedReader.readLine();
        System.out.println(line);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      stop();
    }
    System.out.println("thread stop.");
  }

  @Override // from StartAndStoppable
  public void stop() {
    process.destroy();
  }
}
