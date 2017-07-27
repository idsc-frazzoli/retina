// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import ch.ethz.idsc.retina.util.io.UserHome;

public enum LiveUrgProvider {
  INSTANCE;
  // ---
  public final Set<UrgListener> listeners = new LinkedHashSet<>();
  private OutputStream outputStream;

  /** call once */
  public void start() {
    final File dir = UserHome.file("Public");
    ProcessBuilder processBuilder = //
        new ProcessBuilder(new File(dir, "urg_provider").toString());
    processBuilder.directory(dir);
    try {
      Process process = processBuilder.start();
      outputStream = process.getOutputStream();
      InputStream inputStream = process.getInputStream();
      BufferedReader bufferedReader = //
          new BufferedReader(new InputStreamReader(inputStream));
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          try {
            while (process.isAlive()) {
              String line = bufferedReader.readLine();
              if (line != null) {
                if (line.startsWith("URG{"))
                  listeners.forEach(urgListener -> urgListener.urg(line));
              } else
                Thread.sleep(1);
            }
          } catch (Exception exception) {
            exception.printStackTrace();
          }
          System.out.println("thread stop.");
        }
      };
      Thread thread = new Thread(runnable);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    try {
      outputStream.write("EXIT\n".getBytes());
      outputStream.flush();
      System.out.println("sent EXIT");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
