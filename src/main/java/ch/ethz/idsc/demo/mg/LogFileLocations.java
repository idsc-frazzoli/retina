// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.subare.util.UserHome;

enum LogFileLocations {
  /** extracted part of DUBI4 log file */
  DUBI4a("logs/20180307T154859_0cd18c6bExtracted.lcm"), //
  /** 22sec, only noise */
  DUBI7("logs/20180308T162751_45b9eed8.lcm.00"), //
  /** manual driving with tons of cones */
  DUBI8("logs/20180412T114245_7e5b46c2.lcm.00"), //
  /** extracted slow round of DUBI8 */
  DUBI8a("logs/20180412T114245_7e5b46c2Extracted1.lcm"), //
  /** extracted double round of DUBI8 */
  DUBI8b("logs/20180412T114245_7e5b46c2Extracted2.lcm"), //
  /** extracted andy's double round of DUBI8 */
  DUBI8c("logs/20180412T114245_7e5b46c2Extracted3.lcm"), //
  /** extracted andy's fast double round of DUBI8 */
  DUBI8d("logs/20180412T114245_7e5b46c2Extracted4.lcm"), //
  /** extracted ale's fast double round of DUBI8 */
  DUBI8e("logs/20180412T114245_7e5b46c2Extracted5.lcm"), //
  /** manual driving with cones, zoomed out camera --> helmet blocking corner of image
   * extracted slow round of DUBI9 */
  DUBI9a("logs/20180412T143634_7e5b46c2Extracted1.lcm"), //
  /** extracted another slow round of DUBI9 */
  DUBI9b("logs/20180412T143634_7e5b46c2Extracted2.lcm"), //
  /** extracted very slow round of DUBI9 */
  DUBI9c("logs/20180412T143634_7e5b46c2Extracted3.lcm"), //
  /** extracted slow round of DUBI9, tight track */
  DUBI9d("logs/20180412T143634_7e5b46c2Extracted4.lcm"), //
  /** extracted fast round of DUBI9, tight track */
  DUBI9e("logs/20180412T143634_7e5b46c2Extracted5.lcm"), //
  /** manual driving, second half with cones and stripes on floor. no helmet blocking sight */
  DUBI10("logs/20180412T152900_7e5b46c2.lcm.00"), //
  /** extracted fast round of DUBI10 */
  DUBI10a("logs/20180412T152900_7e5b46c2Extracted1.lcm"), //
  /** extracted another fast round of DUBI10 */
  DUBI10b("logs/20180412T152900_7e5b46c2Extracted2.lcm"), //
  /** extracted round of DUBI10 with cones and stripes */
  DUBI10c("logs/20180412T152900_7e5b46c2Extracted3.lcm"), //
  /** extracted round of DUBI10 with cones and stripes */
  DUBI10d("logs/20180412T152900_7e5b46c2Extracted4.lcm"), //
  /** extracted fast round of DUBI10 with cones and stripes */
  DUBI10e("logs/20180412T152900_7e5b46c2Extracted5.lcm"), //
  /** manual driving with only stripes on floor */
  DUBI11("logs/20180412T164740_7e5b46c2.lcm.00"), //
  /** extracted round of DUBI11, Valentina driving */
  DUBI11a("logs/20180412T164740_7e5b46c2Extracted1.lcm"), //
  /** extracted round of DUBI11, Mario driving, helmet slightly blocking sight */
  DUBI11b("logs/20180412T164740_7e5b46c2Extracted2.lcm"), //
  /** extracted slow round of DUBI11, Mario driving */
  DUBI11c("logs/20180412T164740_7e5b46c2Extracted3.lcm"), //
  /** extracted fast round of DUBI11, Jan driving */
  DUBI11d("logs/20180412T164740_7e5b46c2Extracted4.lcm"), //
  /** new camera pose, calibration images under "Dubi first try". camera calibration available */
  DUBI12("logs/20180430T164704_0e959fc6.lcm.00"), //
  /** extracted slow round of DUBI12, camera zoomed and looking down. */
  DUBI12a("logs/20180430T164704_0e959fc6Extracted1.lcm"), //
  /** extracted slightly faster round of DUBI12, camera zoomed and looking down. */
  DUBI12b("logs/20180430T164704_0e959fc6Extracted2.lcm"), //
  /** extracted faster & chaotic round of DUBI12, camera zoomed and looking down. */
  DUBI12c("logs/20180430T164704_0e959fc6Extracted3.lcm"), //
  /** extracted slow round of DUBI12, camera zoomed and looking down. */
  DUBI12d("logs/20180430T164704_0e959fc6Extracted4.lcm"), //
  /** extracted fast & chaotic round of DUBI12, camera zoomed and looking down. */
  DUBI12e("logs/20180430T164704_0e959fc6Extracted5.lcm"), //
  /** log with new optimized camera pose */
  DUBI13("logs/20180507T143002_588d4c6c.lcm.00"), //
  /** driving in 8 shape, cones and duct tape markings */
  DUBI13a("logs/20180507T143002_588d4c6Extracted1.lcm"), //
  /** driving in 8 shape, cones and duct tape markings */
  DUBI13b("logs/20180507T143002_588d4c6Extracted2.lcm"), //
  /** slow driving in 8 shape, cones and duct tape markings */
  DUBI13c("logs/20180507T143002_588d4c6Extracted3.lcm"), //
  /** only ducttape markings, calibration available */
  DUBI14("logs/20180514T153139_767e5417.lcm.00"), //
  // ** extracted single round */
  DUBI14a("logs/20180514T153139_767e5417Extracted1.lcm"),
  // ** extracted double round */
  DUBI14b("logs/20180514T153139_767e5417Extracted2.lcm"),
  /** ducttape markings and cones on outer side, calibration available */
  DUBI15("logs/20180514T155248_767e5417.lcm.00"),
  /** extracted double round */
  DUBI15a("logs/20180514T155248_767e5417Extracted1.lcm"),
  /** extracted double round */
  DUBI15b("logs/20180514T155248_767e5417Extracted2.lcm"),
  /** slow double round, additional cones */
  DUBI15c("logs/20180514T155248_767e5417Extracted3.lcm"),
  /** extracted double round, additional cones */
  DUBI15d("logs/20180514T155248_767e5417Extracted4.lcm"),
  /** short file, duckie thrown in front of gokart */
  DUBI15e("logs/20180514T155248_767e5417Extracted5.lcm"),;
  // ---
  private final File file;

  private LogFileLocations(String filename) {
    file = UserHome.file(filename);
  }

  public File getFile() {
    if (!file.isFile())
      System.err.println("file does not exist:\n" + file);
    return file;
  }
}
