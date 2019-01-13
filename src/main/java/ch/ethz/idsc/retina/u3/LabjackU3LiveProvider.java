// code by jph
package ch.ethz.idsc.retina.u3;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import ch.ethz.idsc.retina.util.StartAndStoppable;

/** Labjack U3
 * readout ADC */
public final class LabjackU3LiveProvider implements StartAndStoppable, Runnable {
  private final LabjackU3Config labjackU3Config;
  /** 2 bytes header, 8 bytes timestamp, each point as short */
  private final LabjackAdcListener labjackAdcListener;
  private Process process;

  /* package */ LabjackU3LiveProvider(LabjackU3Config labjackU3Config, LabjackAdcListener labjackAdcListener) {
    this.labjackU3Config = labjackU3Config;
    this.labjackAdcListener = Objects.requireNonNull(labjackAdcListener);
  }

  @Override // from StartAndStoppable
  public void start() { // non-blocking
    File executable = labjackU3Config.getExecutable();
    ProcessBuilder processBuilder = new ProcessBuilder(executable.toString());
    processBuilder.directory(executable.getParentFile()); // <- not required
    try {
      process = processBuilder.start();
      Thread thread = new Thread(this);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override // from StartAndStoppable
  public void stop() {
    process.destroy();
  }

  @Override // from Runnable
  public void run() {
    try {
      InputStream inputStream = process.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      while (process.isAlive()) {
        String line = bufferedReader.readLine();
        float[] array = parse(line);
        labjackAdcListener.labjackAdc(new LabjackAdcFrame(array));
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      stop();
    }
  }

  /* package */ static float[] parse(String line) {
    String[] split = line.split(" ");
    float[] array = new float[split.length];
    for (int index = 0; index < split.length; ++index)
      array[index] = Float.parseFloat(split[index]);
    return array;
  }
}
