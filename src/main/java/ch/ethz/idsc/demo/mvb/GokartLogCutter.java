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
    File folder = new File("/home/mvb/0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics_newFormat/RawData/20190822");
    File[] files = folder.listFiles();
    try (Scanner scanner = new Scanner(System.in)) {
      int no_of_files = files.length;
      long total_size = 0;
      for (File file : files) {
        total_size = total_size + file.length();
      }
      System.out.print("total_size" + total_size + "\n");
      int file_count = 1;
      long size_count = 0;
      for (File file : files) {
        // File file = new File("/home/mvb/0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics_newFormat/RawData/20190819/20190819T181527_c21b2aba.lcm.00");
        System.out.print("Processing file " + file_count + "/" + no_of_files + " " + file.getName().toString() + "\n");
        float percentage = 100 * size_count / (float) total_size;
        System.out.print(percentage + "% of contents done\n");
        GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
        new GokartLcmLogCutter( //
            gokartLogFileIndexer, //
            new File("/home/mvb/0_ETH/01_MasterThesis/Logs_GoKart/LogData/dynamics_newFormat/cuts"), //
            file.getName().substring(0, 15));
        System.out.println("Press Enter when ready for next file...");
        scanner.nextLine();
        file_count += 1;
        size_count += file.length();
      }
      System.out.println("FINISHED: End of file list");
    }
  }
}