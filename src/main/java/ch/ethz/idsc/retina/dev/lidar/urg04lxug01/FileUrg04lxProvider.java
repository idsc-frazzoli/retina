// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lxug01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** playback urg recordings */
public class FileUrg04lxProvider implements Urg04lxProvider {
  private final List<Urg04lxEventListener> listeners = new LinkedList<>();
  private final BufferedReader bufferedReader;

  public FileUrg04lxProvider(File file) throws IOException {
    bufferedReader = new BufferedReader(new FileReader(file));
  }

  @Override
  public void addListener(Urg04lxEventListener urgListener) {
    listeners.add(urgListener);
  }

  @Override
  public void start() {
    try {
      while (true) {
        String line = bufferedReader.readLine();
        if (Objects.isNull(line))
          break;
        if (line.startsWith(URG_PREFIX)) {
          Urg04lxEvent urg04lxEvent = Urg04lxEvent.fromString(line);
          listeners.forEach(urgListener -> urgListener.range(urg04lxEvent));
        } else
          System.err.println("unknown: " + line);
      }
    } catch (Exception exception) {
      // ---
    }
  }

  @Override
  public void stop() {
    try {
      bufferedReader.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
