// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* package */ class MPCNativeSession {
  private Process process;
  private final Map<Integer, Integer> messageCounter = new HashMap<>();
  public BufferedReader is;
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
        is = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
      while (is.ready())
        res = res + is.readLine() + "\n";
    } catch (IOException e) {
      e.printStackTrace();
    }
    return res;
  }

  /** gets a unique ID for any object that inherits MPCNative */
  int getMessageId(MPCNativeMessage message) {
    int prefix = message.getMessagePrefix();
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
