// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* package */ class MPCNativeSession {
  private final Map<Integer, Integer> messageCounter = new HashMap<>();
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
      // TODO design, talk to jan
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

  public String getNativeOutput() {
    // doesn't seem to work
    String res = "";
    try {
      while (bufferedReader.ready())
        res = res + bufferedReader.readLine() + "\n";
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return res;
  }

  /** gets a unique ID for any object that inherits MPCNative */
  int getMessageId(MPCNativeMessage mpcNativeMessage) {
    int prefix = mpcNativeMessage.getMessageType().ordinal();
    Integer current = messageCounter.get(prefix);
    if (Objects.isNull(current))
      current = 0;
    messageCounter.put(prefix, current + 1);
    return current;
  }

  void last() {
    // stop process
    if (Objects.nonNull(process))
      process.destroy();
  }
}
