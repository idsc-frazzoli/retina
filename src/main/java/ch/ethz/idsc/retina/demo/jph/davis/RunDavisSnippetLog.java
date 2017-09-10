// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisSnippetLog;

enum RunDavisSnippetLog {
  ;
  public static void main(String[] args) {
    // TODO change target directory
    int period_ms = 1200;
    File lcmDir = new File("/media/datahaki/media/ethz/snippet/lcm");
    File uzhDir = new File("/media/datahaki/media/ethz/snippet/uzh");
    new DavisSnippetLog(period_ms, lcmDir, uzhDir);
  }
}
