// code by mvb
package ch.ethz.idsc.demo.mvb;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/home/mvb/0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics_newFormat/RawData/20190507");
    File[] files = folder.listFiles();

    Scanner scanner = new Scanner(System.in);
    for (File file : files) {
      System.out.print("Processing file " + file.getName().toString() + "\n");

      GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
      new GokartLcmLogCutter( //
          gokartLogFileIndexer, //
          new File("/home/mvb/0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics_newFormat/cuts"), //
          file.getName().substring(0,15));
      
      System.out.println("Press Enter when ready for next file...");
      scanner.nextLine();
    }
    System.out.println("FINISHED: End of file list");
    scanner.close();
  }
}