// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.io.UserHome;

public enum Urg04lxLiveProvider implements Urg04lxProvider {
  INSTANCE;
  // ---
  public static final String EXECUTABLE = "urg_timedprovider";
  // ---
  private final List<Urg04lxEventListener> listeners = new LinkedList<>();
  private OutputStream outputStream;

  @Override
  public void addListener(Urg04lxEventListener urgListener) {
    listeners.add(urgListener);
  }

  @Override
  public void start() {
    // TODO it seems that urg process is sending only int precision timestamp
    final File dir = UserHome.file("Public");
    ProcessBuilder processBuilder = //
        new ProcessBuilder(new File(dir, EXECUTABLE).toString());
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
                if (line.startsWith(URG_PREFIX)) {
                  Urg04lxEvent urg04lxEvent = Urg04lxEvent.fromString(line);
                  listeners.forEach(urgListener -> urgListener.range(urg04lxEvent));
                }
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
