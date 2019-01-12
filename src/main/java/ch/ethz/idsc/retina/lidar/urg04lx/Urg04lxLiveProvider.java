// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** Hint: the sensor requires a warm-up time of half a minute or so.
 * attempts to connect immediately after power up will fail.
 * 
 * receives binary packets via inputstream from urg04lx process and distributes
 * to listeners
 * 
 * requires that the binary "urg_binaryprovider" is located at
 * /home/{username}/Public/urg_binaryprovider
 * 
 * you can download the binary from
 * https://polybox.ethz.ch/index.php/s/IkO51LOeZMOL79u
 * and make it executable using
 * <pre>
 * sudo chmod +x /home/{username}/Public/urg_binaryprovider
 * </pre>
 * 
 * the binary is based on the official urg library located at
 * https://sourceforge.net/projects/urgnetwork/files/urg_library/
 * 
 * Quote from datasheet: The light source of the sensor is infrared laser of
 * wavelength 785nm with laser class 1 safety Max. Distance: 4000[mm]
 * 
 * The sensor is designed for indoor use only. The sensor is not a safety
 * device/tool. The sensor is not for use in military applications.
 * 
 * typically the distances up to 5[m] can be measured correctly. */
public enum Urg04lxLiveProvider implements StartAndStoppable {
  INSTANCE;
  // TODO possibly do not make this a singleton instance?
  // ---
  /* package */ static final File DIRECTORY = HomeDirectory.file("Public");
  /* package */ static final File EXECUTABLE = new File(DIRECTORY, "urg_binaryprovider");
  // ---
  private OutputStream outputStream;
  /** 2 bytes header, 8 bytes timestamp, each point as short */
  private final byte[] array = new byte[2 + 8 + Urg04lxDevice.MAX_POINTS * 2];
  private final List<ByteArrayConsumer> listeners = new CopyOnWriteArrayList<>();
  private boolean isLaunched = false;

  public void addListener(ByteArrayConsumer byteArrayConsumer) {
    listeners.add(byteArrayConsumer);
  }

  public void removeListener(ByteArrayConsumer byteArrayConsumer) {
    boolean removed = listeners.remove(byteArrayConsumer);
    if (!removed)
      new RuntimeException("urg04lx listener not removed").printStackTrace();
  }

  @Override // from StartAndStoppable
  public void start() { // non-blocking
    ProcessBuilder processBuilder = new ProcessBuilder(EXECUTABLE.toString());
    processBuilder.directory(DIRECTORY);
    try {
      Process process = processBuilder.start();
      System.out.println("urg_alive1=" + process.isAlive());
      outputStream = process.getOutputStream();
      InputStream inputStream = process.getInputStream();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          isLaunched = true;
          try {
            System.out.println("urg_alive2=" + process.isAlive());
            while (process.isAlive()) {
              int available = inputStream.available();
              if (array.length <= available) {
                int read = inputStream.read(array);
                GlobalAssert.that(read == array.length);
                ByteBuffer byteBuffer = ByteBuffer.wrap(array);
                byte c1 = byteBuffer.get();
                byte c2 = byteBuffer.get();
                if (c1 == 'U' && c2 == 'B') {
                  long tic = System.currentTimeMillis();
                  listeners.forEach(listener -> listener.accept(array, array.length));
                  long duration = System.currentTimeMillis() - tic;
                  // TODO investigate in order to enhance readout
                  Thread.sleep(Math.max(0, 95 - duration));
                } else
                  throw new RuntimeException("data corrupt");
              } else
                Thread.sleep(2); // magic const
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

  @Override // from StartAndStoppable
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
