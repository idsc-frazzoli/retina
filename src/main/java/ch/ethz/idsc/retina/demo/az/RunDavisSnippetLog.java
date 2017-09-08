// code by jph
package ch.ethz.idsc.retina.demo.az;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisSnippetLog;

enum RunDavisSnippetLog {
  ;
  public static void main(String[] args) {
    // TODO change target directory
    int period_ms = 1000;
    new DavisSnippetLog(period_ms, new File("/media/datahaki/media/ethz/export"));
  }
}
