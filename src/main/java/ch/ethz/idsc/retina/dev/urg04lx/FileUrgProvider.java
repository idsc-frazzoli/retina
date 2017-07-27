// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/** playback urg recordings */
public class FileUrgProvider implements UrgProvider {
  private static final int PERIOD_MILLIS = 100;
  // ---
  private final Set<UrgListener> listeners = new LinkedHashSet<>();
  private final BufferedReader bufferedReader;
  private boolean isLaunched = false;

  public FileUrgProvider(File file) throws IOException {
    bufferedReader = new BufferedReader(new FileReader(file));
  }

  @Override
  public void addListener(UrgListener urgListener) {
    listeners.add(urgListener);
  }

  @Override
  public void start() {
    isLaunched = true;
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          while (isLaunched) {
            String line = bufferedReader.readLine();
            if (line != null && line.startsWith(URG_PREFIX))
              listeners.forEach(urgListener -> urgListener.urg(line));
            Thread.sleep(PERIOD_MILLIS);
          }
          bufferedReader.close();
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  @Override
  public void stop() {
    isLaunched = false;
  }
}
