// code by jph
package ch.ethz.idsc.demo.jg;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import lcm.logging.LogPlayer;
import lcm.logging.LogPlayerConfig;

/* package */ enum GokartLcmLogPlayer {
  ;
  public static void main(String[] args) throws IOException {
    LogPlayerConfig logPlayerConfig = new LogPlayerConfig();
    File file;
    // file = new File("C:/Users/joelg/Downloads/20181128T112109_35b19382.lcm.00");
    // file = new File("C:/Users/joelg/Downloads/20181128T130954_35b19382.lcm.00");
    // file = new File("C:/Users/joelg/Downloads/20190321T144129_140b9727.lcm.00");
    // file = new File("C:/Users/joelg/Downloads/20190418T161148_b6a70baf.lcm.00");
    // file = new File("C:/Users/joelg/Downloads/20190424/20190424T155214_3262e93a.lcm.00");
    file = new File("C:/Users/joelg/Downloads/20190425/20190425T133500_7cf20bb2.lcm.00");
    logPlayerConfig.logFile = file.toString();
    logPlayerConfig.speed_numerator = 1;
    logPlayerConfig.speed_denominator = 1;
    LogPlayer logPlayer = LogPlayer.create(logPlayerConfig);
    WindowConfiguration windowConfiguration = //
        AppCustomization.load(GokartLcmLogPlayer.class, new WindowConfiguration());
    windowConfiguration.attach(GokartLcmLogPlayer.class, logPlayer.jFrame);
    logPlayer.jFrame.setLocation(100, 100);
    logPlayer.standalone();
  }
}
