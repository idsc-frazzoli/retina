// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.ethz.idsc.retina.util.io.UserHome;

public final class LoggerModule extends AbstractModule {
  Process process;

  @Override
  protected void first() throws Exception {
    // Store the log file with <date time githash>
    String gitHash = GitRevHead.getHash();
    gitHash = gitHash.substring(0, Math.min(gitHash.length(), 8));
    File logfilepath = UserHome.file(".");
    File logfile = new File(logfilepath, //
        String.join("_", //
            "machine", //
            "" + System.currentTimeMillis(), // TODO
            gitHash) //
            + ".log");
    System.out.println(new Date() + "log: " + logfile);
    File loggerbinary = new File("/usr/local/bin/lcm-logger"); // FIXME
    List<String> list = Arrays.asList(//
        loggerbinary.toString(), //
        "--quiet", //
        "--increment", //
        "--split-mb=50", //
        logfile.toString());
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
  protected void last() {
    // TODO try to send signal to exit gracefully
    // ... something like "Ctrl+C" via the process.inputstream
    System.out.println(new Date() + " lcm-logger: graceful destruction");
    process.destroy();
  }
}