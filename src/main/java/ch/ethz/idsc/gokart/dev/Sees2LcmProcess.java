// code by jph
package ch.ethz.idsc.gokart.dev;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Sees2LcmProcess implements AutoCloseable {
  // TODO MG confirm path
  public final static String BINARY = "/home/gokart/Projects/sees_sdk/lcmprovider/build/sees2lcm";
  // ---
  private final Process process;

  public Sees2LcmProcess() throws IOException {
    List<String> list = Arrays.asList(BINARY);
    ProcessBuilder processBuilder = new ProcessBuilder(list);
    process = processBuilder.start();
    System.out.println(new Date() + " sees2lcm: started");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // the print out will not always show up
      // even if the shutdown hook is called !
      System.out.println(new Date() + " sees2lcm: isAlive=" + process.isAlive());
      process.destroy();
    }));
  }

  @Override
  public void close() throws Exception {
    if (Objects.nonNull(process))
      process.destroy();
  }
}
