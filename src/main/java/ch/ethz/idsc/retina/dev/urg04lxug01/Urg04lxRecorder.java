// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.ethz.idsc.retina.util.io.UserHome;

public class Urg04lxRecorder implements Urg04lxListener, AutoCloseable {
  public static Urg04lxListener createDefault() throws IOException {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    return new Urg04lxRecorder(UserHome.file("urg" + dateFormat.format(new Date()) + ".txt"));
  }

  private final BufferedWriter bufferedWriter;

  public Urg04lxRecorder(File file) throws IOException {
    bufferedWriter = new BufferedWriter(new FileWriter(file));
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

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
    System.out.println("urg recorder close");
  }
}
