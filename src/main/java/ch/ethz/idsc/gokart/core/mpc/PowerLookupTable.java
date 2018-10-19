package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import ch.ethz.idsc.gokart.core.mpc.LookUpTable2D.LookupFunction;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PowerLookupTable {
  final String lookupTableLocation = "powerlookuptable.csv";
  final String inverseLookupTableLocation = "inverselookup.csv";
  /** maps from (current, speed)->(acceleration) */
  final LookUpTable2D powerLookupTable;
  /** maps from (acceleration, speed)->(current) */
  final LookUpTable2D inverseLookupTable;
  // min and max values for lookup tables
  final Scalar vMin = Quantity.of(-10, SI.VELOCITY);
  final Scalar vMax = Quantity.of(10, SI.VELOCITY);
  final Scalar cMin = Quantity.of(-2300, NonSI.ARMS);
  final Scalar cMax = Quantity.of(2300, NonSI.ARMS);
  final Scalar aMin = Quantity.of(-2, SI.ACCELERATION);
  final Scalar aMax = Quantity.of(2, SI.ACCELERATION);
  final int DimN = 100;

  public PowerLookupTable() throws Exception {
    // TODO: save this in ephemeral
    File lookupfile = new File(lookupTableLocation);
    File invlookupfile = new File(inverseLookupTableLocation);
    if (!lookupfile.exists() || !invlookupfile.exists()) {
      // create lookup tables
      LookupFunction function = new LookupFunction() {
        @Override
        public Scalar getValue(Scalar firstValue, Scalar secondValue) {
          // power, Speed
          return PowerHelpers.getAccelerationEstimation(firstValue, secondValue);
        }
      };
      // maps from (current, speed)->(acceleration)
      powerLookupTable = new LookUpTable2D(//
          function, //
          DimN, //
          DimN, //
          cMin, //
          cMax, //
          vMin, //
          vMax, //
          NonSI.ARMS, SI.VELOCITY, SI.ACCELERATION);
      // save
      FileWriter lookupFileWriter = new FileWriter(lookupfile);
      BufferedWriter lookupBufferedWriter = new BufferedWriter(lookupFileWriter);
      powerLookupTable.saveTable(lookupBufferedWriter);
      lookupBufferedWriter.close();
      // maps from (acceleration, speed)->(acceleration)
      inverseLookupTable = powerLookupTable.getInverseLookupTableBinarySearch(//
          0, //
          DimN, //
          DimN, //
          aMin, //
          aMax);
      // Save
      FileWriter inverseLookupFileWriter = new FileWriter(lookupfile);
      BufferedWriter inverseLookupBufferedWriter = new BufferedWriter(inverseLookupFileWriter);
      inverseLookupTable.saveTable(inverseLookupBufferedWriter);
      inverseLookupBufferedWriter.close();
    } else {
      // load lookup table
      FileReader lookupFileReader = new FileReader(lookupTableLocation);
      BufferedReader lookupBufferedReader = new BufferedReader(lookupFileReader);
      powerLookupTable = new LookUpTable2D(lookupBufferedReader);
      lookupBufferedReader.close();
      // load inverse table
      FileReader inverseLookupFileReader = new FileReader(inverseLookupTableLocation);
      BufferedReader inverseLookupBufferedReader = new BufferedReader(inverseLookupFileReader);
      inverseLookupTable = new LookUpTable2D(inverseLookupBufferedReader);
      inverseLookupBufferedReader.close();
    }
  }

  /** get min and max possible acceleration
   * @param velocity current velocity [m/s]
   * @return a tensor of the maximal and minimal acceleration [m/s^2] */
  public Tensor getMinMaxAcceleration(Scalar velocity) {
    return powerLookupTable.getExtremalValues(0, velocity);
  }
  
  
 /** get acceleration for a given current and velocity
  * @param current the applied motor current [Arms]
  * @param velocity the velocity [m/s]
  * @return the resulting acceleration [m/s^2]
  */
  public Scalar getAcceleration(Scalar current, Scalar velocity) {
    return powerLookupTable.lookup(current, velocity);
  }
  
  /** get the need current for a wanted acceleration
   * If the acceleration is not achievable
   * the motor current corresponding to the nearest possible acceleration value is returned
   * @param wantedAcceleration the wanted acceleration [m/s^2]
   * @param velocity the velocity [m/s]
   * @return the needed motor current [Arms]
   */
  public Scalar getNeededCurrent(Scalar wantedAcceleration, Scalar velocity) {
    return inverseLookupTable.lookup(wantedAcceleration, velocity);
  }
}
