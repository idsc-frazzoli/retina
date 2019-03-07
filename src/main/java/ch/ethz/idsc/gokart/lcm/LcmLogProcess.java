// code by jph
// developed together with swisstrolley+
package ch.ethz.idsc.gokart.lcm;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.util.sys.GitRevHead;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;

/** process to log lcm traffic */
public class LcmLogProcess implements AutoCloseable {
  /** standard path on linux */
  public final static String BINARY = "/usr/local/bin/lcm-logger";
  /** split file every 50 MB */
  public final static String SPLIT_MB = "50";

  public static LcmLogProcess createDefault(File directory) throws Exception {
    return new LcmLogProcess(new File(directory, defaultFilename()));
  }

  // function is not for use from the outside
  private static String defaultFilename() {
    String gitHash = GitRevHead.getHash();
    gitHash = gitHash.substring(0, Math.min(gitHash.length(), 8));
    return String.join("_", SystemTimestamp.asString(), gitHash) + ".lcm";
  }

  // ---
  private final File file;
  private final Process process;

  /** @param file
   * reference to absolute path of log file
   * @throws Exception if file already exists or log process cannot be started */
  private LcmLogProcess(final File file) throws Exception {
    if (file.exists())
      throw new RuntimeException();
    this.file = file;
    // ---
    List<String> list = Arrays.asList( //
        BINARY, "--quiet", "--increment", "--split-mb=" + SPLIT_MB, file.toString());
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

  /** the lcm-logger binary starts logging into a file with name terminating with ".00"
   * once the size limit is reached, the extension will increase to ".01" etc.
   * 
   * @return first file created by lcm-logger */
  public File file() {
    String string = file.toString() + ".00";
    return new File(string);
  }
}
