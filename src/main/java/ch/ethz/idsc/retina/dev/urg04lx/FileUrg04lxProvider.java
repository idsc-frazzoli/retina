// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/** playback urg recordings */
public class FileUrg04lxProvider implements Urg04lxProvider {
  private static final int PERIOD_MILLIS = 10; // TODO probably should ...
  // ---
  private final Set<Urg04lxListener> listeners = new LinkedHashSet<>();
  private final BufferedReader bufferedReader;
  private boolean isLaunched = false;
  private boolean isTerminated = false;

  public FileUrg04lxProvider(File file) throws IOException {
    bufferedReader = new BufferedReader(new FileReader(file));
  }

  @Override
  public void addListener(Urg04lxListener urgListener) {
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
          Thread.sleep(100);
          bufferedReader.close();
        } catch (Exception exception) {
          exception.printStackTrace();
        }
        isTerminated = true;
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  @Override
  public void stop() {
    isLaunched = false;
  }

  public boolean isTerminated() {
    return isTerminated;
  }
}
