// code by jph
// developed together with swisstrolley+
package ch.ethz.idsc.retina.lcm;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.sys.GitRevHead;
import ch.ethz.idsc.retina.sys.SystemTimestamp;
import ch.ethz.idsc.retina.util.io.UserHome;

public class LcmLogProcess implements AutoCloseable {
  /** standard on linux, non-final so that configurable at runtime */
  public static String BINARY = "/usr/local/bin/lcm-logger";

  public static LcmLogProcess createDefault() throws Exception {
    return new LcmLogProcess(defaultFile());
  }

  // this function is not used for use from the outside
  private static File defaultFile() {
    String gitHash = GitRevHead.getHash();
    gitHash = gitHash.substring(0, Math.min(gitHash.length(), 8));
    return UserHome.file(String.join("_", "lcm", SystemTimestamp.file(), gitHash) + ".log");
  }

  // ---
  private final File file;
  private final Process process;

  /** @param file reference to absolute path of log file
   * @throws Exception if file already exists or log process cannot be started */
  public LcmLogProcess(final File file) throws Exception {
    if (file.exists())
      throw new RuntimeException();
    this.file = file;
    // ---
    List<String> list = Arrays.asList( //
        BINARY, "--quiet", "--increment", "--split-mb=50", file.toString());
    ProcessBuilder processBuilder = new ProcessBuilder(list);
    process = processBuilder.start();
    System.out.println(new Date() + " lcm-logger: started");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // the print out will not always show up
      // even if the shutdown hook is called !
      System.out.println(new Date() + " lcm-logger: isAlive=" + process.isAlive());
      process.destroy();
    }));
  }

  @Override
  public void close() throws Exception {
    if (Objects.nonNull(process))
      process.destroy();
  }

  public File file() {
    return file;
  }
}
