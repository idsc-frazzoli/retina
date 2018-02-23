// code by jph
package ch.ethz.idsc.demo.az;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisSnippetLog;

enum RunDavisSnippetLog {
  ;
  public static void main(String[] args) {
    int period_ms = 1200;
    File lcmDir = new File("/home/ale/Datasets/ourCityscape_lcm");
    File uzhDir = new File("/home/ale/Datasets/ourCityscape_uzh");
    new DavisSnippetLog(period_ms, lcmDir, uzhDir);
  }
}
