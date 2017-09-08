// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.UserHome;

/** receives binary packets via inputstream from urg04lx process
 * and distributes to listeners */
public enum Urg04lxLiveProvider implements StartAndStoppable {
  INSTANCE;
  public static final String EXECUTABLE = "urg_binaryprovider";
  // ---
  private OutputStream outputStream;
  private final byte[] array = new byte[2 + 8 + Urg04lxDevice.POINTS * 2];
  private final List<ByteArrayConsumer> listeners = new LinkedList<>();

  public void addListener(ByteArrayConsumer byteArrayConsumer) {
    listeners.add(byteArrayConsumer);
  }

  private boolean isLaunched = false;

  @Override
  public void start() { // non-blocking
    final File dir = UserHome.file("Public");
    ProcessBuilder processBuilder = //
        new ProcessBuilder(new File(dir, EXECUTABLE).toString());
    processBuilder.directory(dir);
    try {
      Process process = processBuilder.start();
      outputStream = process.getOutputStream();
      InputStream inputStream = process.getInputStream();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          isLaunched = true;
          try {
            while (process.isAlive()) {
              int available = inputStream.available();
              if (array.length <= available) {
                int read = inputStream.read(array);
                GlobalAssert.that(read == array.length);
                ByteBuffer byteBuffer = ByteBuffer.wrap(array);
                byte c1 = byteBuffer.get();
                byte c2 = byteBuffer.get();
                if (c1 == 'U' && c2 == 'B')
                  listeners.forEach(listener -> listener.accept(array, array.length));
                else
                  throw new RuntimeException("data corrupt");
              } else
                Thread.sleep(2);
            }
          } catch (Exception exception) {
            exception.printStackTrace();
            if (isLaunched)
              stop();
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
    isLaunched = false;
    if (Objects.nonNull(outputStream))
      try {
        outputStream.write("EXIT\n".getBytes());
        outputStream.flush();
        System.out.println("sent EXIT");
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    outputStream = null;
  }
}
