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

public enum LiveUrg04lxProvider implements Urg04lxProvider {
  INSTANCE;
  // ---
  private final Set<Urg04lxListener> listeners = new LinkedHashSet<>();
  private OutputStream outputStream;

  @Override
  public void addListener(Urg04lxListener urgListener) {
    listeners.add(urgListener);
  }

  @Override
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
                if (line.startsWith(URG_PREFIX))
                  listeners.forEach(urgListener -> urgListener.urg(line));
              } else {
                System.out.println("readLine give up.");
                // never here
                Thread.sleep(1);
              }
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

  @Override
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
