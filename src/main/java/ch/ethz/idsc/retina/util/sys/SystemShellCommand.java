// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.util.sys;

import java.io.InputStream;
import java.util.List;

/* package */ enum SystemShellCommand {
  ;
  public static String exec(List<String> list) {
    Process process;
    ProcessBuilder processBuilder = new ProcessBuilder(list);
    try {
      process = processBuilder.start();
      while (process.isAlive())
        Thread.sleep(1);
      InputStream inputStream = process.getInputStream();
      int len = inputStream.available();
      byte[] bytes = new byte[len]; // byte are '0'-'f'
      inputStream.read(bytes);
      final String string = new String(bytes).trim();
      process.destroy();
      return string;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
