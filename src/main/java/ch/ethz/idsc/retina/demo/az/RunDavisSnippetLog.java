// code by jph
package ch.ethz.idsc.retina.demo.az;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisSnippetLog;

enum RunDavisSnippetLog {
  ;
  public static void main(String[] args) {
    // TODO change target directory
    int period_ms = 5000;
    File lcmDir = new File("/home/ale/Datasets/ourCityscape_lcm");
    File uzhDir = new File("/home/ale/Datasets/ourCityscape_uzh");
    new DavisSnippetLog(period_ms, lcmDir, uzhDir);
  }
}
