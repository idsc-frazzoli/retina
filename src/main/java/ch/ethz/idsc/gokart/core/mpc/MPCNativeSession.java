// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MPCNativeSession {
  private Process process;
  private HashMap<Integer, Integer> messageCounter;

  void first() {
    String fullPath = MPCNative.binary().get().getAbsolutePath();
    // start server
    List<String> list = Arrays.asList(fullPath
    // String.valueOf(MPCNative.TCP_SERVER_PORT)
    );
    ProcessBuilder processBuilder = new ProcessBuilder(list);
    try {
      process = processBuilder.start();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println(new Date() + " mpc-server: isAlive=" + process.isAlive());
        process.destroy();
      }));
      System.out.println(new Date() + " mpc-server: started");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /** gets a unique ID for any object that inherits MPCNative */
  int getMessageId(MPCNativeMessage message) {
    int prefix = message.getMessagePrefix();
    Integer current = messageCounter.get(prefix);
    if (current == null) {
      current = 0;
    }
    messageCounter.put(prefix, current + 1);
    return current;
  }

  void last() {
    // stop process
    process.destroy();
  }
}
