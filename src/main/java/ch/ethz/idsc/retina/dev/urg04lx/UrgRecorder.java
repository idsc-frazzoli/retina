// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UrgRecorder implements UrgListener, AutoCloseable {
  private final BufferedWriter bufferedWriter;

  public UrgRecorder(File file) throws IOException {
    bufferedWriter = new BufferedWriter(new FileWriter(file));
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }

  @Override
  public void urg(String line) {
    try {
      bufferedWriter.write(line);
      bufferedWriter.newLine();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
