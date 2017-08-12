// code by jph
package ch.ethz.idsc.retina.davis.io.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;
import java.util.StringTokenizer;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;

public class DavisDirectorySupplier implements StartAndStoppable {
  private final BufferedReader br_events;
  private final BufferedReader br_images;

  public DavisDirectorySupplier(File directory) throws Exception {
    GlobalAssert.that(directory.isDirectory());
    br_events = new BufferedReader(new FileReader(new File(directory, "events.txt")));
    br_images = new BufferedReader(new FileReader(new File(directory, "images.txt")));
  }

  private long next_image = -2;
  private long last_event = -1;

  @SuppressWarnings("unused")
  @Override
  public void start() {
    try {
      long events = 0;
      while (true) {
        if (next_image < last_event) {
          String line = br_images.readLine();
          if (Objects.isNull(line))
            break;
          // System.out.println(line);
          StringTokenizer stringTokenizer = new StringTokenizer(line);
          int time = (int) (Double.parseDouble(stringTokenizer.nextToken()) * 1e6);
          String imagelocation = stringTokenizer.nextToken();
          // FIXME
        }
        String line = br_events.readLine();
        if (Objects.isNull(line)) {
          System.out.println("exit");
          break;
        }
        DavisDvsEvent dde = asdf(line);
        if (events % 10000 == 0)
          System.out.println(dde);
        ++events;
      }
    } catch (Exception e) {
      // ---
      e.printStackTrace();
    }
  }

  private static DavisDvsEvent asdf(String line) {
    StringTokenizer stringTokenizer = new StringTokenizer(line);
    int time = (int) (Double.parseDouble(stringTokenizer.nextToken()) * 1e6);
    int x = Integer.parseInt(stringTokenizer.nextToken()); // x
    int y = Integer.parseInt(stringTokenizer.nextToken()); // y
    int i = Integer.parseInt(stringTokenizer.nextToken()); // i
    return new DavisDvsEvent(time, x, y, i);
  }

  @Override
  public void stop() {
    try {
      if (Objects.nonNull(br_events))
        br_events.close();
      if (Objects.nonNull(br_images))
        br_images.close();
    } catch (Exception exception) {
      // ---
    }
  }

  public static void main(String[] args) throws Exception {
    DavisDirectorySupplier ds = new DavisDirectorySupplier( //
        new File("/media/datahaki/media/ethz/davis/shapes_6dof"));
    ds.start();
    System.out.println("finish");
  }
}
