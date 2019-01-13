// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.davis.DavisSnippetLog;

enum RunDavisSnippetLog {
  ;
  public static void main(String[] args) {
    int period_ms = 1700;
    File lcmDir = new File("/media/datahaki/media/ethz/snippet/lcm");
    File uzhDir = new File("/media/datahaki/media/ethz/snippet/uzh");
    new DavisSnippetLog(period_ms, lcmDir, uzhDir);
  }
}
