// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// TODO JPH in the long run, we can start and stop the MPC process from java instead from the console
// TODO JPH the current design below is very bad
/* package */ class MPCNativeProcess {
  private Process process;
  private BufferedReader bufferedReader;
  private boolean test = false;
  private boolean externStart = false;

  public void switchToTest() {
    test = true;
  }

  public void switchToExternalStart() {
    externStart = true;
  }

  void first() {
    if (!externStart) {
      String fullPath;
      if (!test)
        fullPath = MPCNative.lcmBinary().get().getAbsolutePath();
      else
        fullPath = MPCNative.lcmTestBinary().get().getAbsolutePath();
      // start server
      List<String> list = Arrays.asList(fullPath
      // String.valueOf(MPCNative.TCP_SERVER_PORT)
      );
      ProcessBuilder processBuilder = new ProcessBuilder(list);
      try {
        process = processBuilder.start();
        bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // FIXME the reader has to be read in a separate thread! otherwise the process will be blocked at some point!
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          System.out.println(new Date() + " mpc-server: isAlive=" + process.isAlive());
          process.destroy();
        }));
        System.out.println(new Date() + " mpc-server: started");
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  void last() {
    // stop process
    if (Objects.nonNull(process))
      process.destroy();
  }
}
