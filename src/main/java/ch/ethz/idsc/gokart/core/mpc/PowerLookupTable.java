// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.gokart.core.mpc.LookupTable2D.LookupFunction;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

public class PowerLookupTable {
  // TODO class design: directly assign INSTANCE = new PowerLookupTable();
  private static PowerLookupTable INSTANCE;
  // to ensure that the maximum motor torque is actually applied

  /** returns global instance of Power Lookup Table
   * @return instance of PowerLookupTable */
  public static PowerLookupTable getInstance() {
    try {
      if (INSTANCE == null)
        INSTANCE = new PowerLookupTable();
    } catch (IOException e) {
      System.err.println("Power lookup table not available");
      e.printStackTrace();
    }
    return INSTANCE;
  }

  private final String lookupTableLocation = "powerlookuptable.csv";
  private final String inverseLookupTableLocation = "inversepowerlookuptable.csv";
  /** maps from (current, speed)->(acceleration) */
  private final LookupTable2D powerLookupTable;
  /** maps from (acceleration, speed)->(current) */
  private final LookupTable2D inverseLookupTable;
  // min and max values for lookup tables
  // TODO magic const in config class
  private final Scalar vMin = Quantity.of(-10, SI.VELOCITY);
  private final Scalar vMax = Quantity.of(+10, SI.VELOCITY);
  private final Scalar cMin = ManualConfig.GLOBAL.torqueLimit.negate();
  private final Scalar cMax = ManualConfig.GLOBAL.torqueLimit;
  private final Scalar aMin = Quantity.of(-2, SI.ACCELERATION);
  private final Scalar aMax = Quantity.of(2, SI.ACCELERATION);
  private final int DimN = 1000;

  /** create or load Power Lookup Table */
  private PowerLookupTable() throws IOException {
    // TODO: save this in ephemeral
    File lookupfile = new File(lookupTableLocation);
    File invlookupfile = new File(inverseLookupTableLocation);
    if (!lookupfile.exists() || !invlookupfile.exists()) {
      // create lookup tables
      LookupFunction function = new LookupFunction() {
        @Override
        public Scalar getValue(Scalar firstValue, Scalar secondValue) {
          // power, Speed
          return MotorFunction.getAccelerationEstimation(firstValue, secondValue);
        }
      };
      // maps from (current, speed)->(acceleration)
      powerLookupTable = new LookupTable2D(//
          function, //
          DimN, //
          DimN, //
          cMin, //
          cMax, //
          vMin, //
          vMax, //
          SI.ACCELERATION);
      // save
      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lookupfile))) {
        powerLookupTable.saveTable(bufferedWriter);
      }
      // maps from (acceleration, speed)->(acceleration)
      inverseLookupTable = powerLookupTable.getInverseLookupTableBinarySearch(//
          0, //
          DimN, //
          DimN, //
          aMin, //
          aMax);
      // Save
      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(invlookupfile))) {
        inverseLookupTable.saveTable(bufferedWriter);
      }
    } else {
      // load lookup table
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(lookupfile))) {
        powerLookupTable = LookupTable2D.from(bufferedReader);
      }
      // load inverse table
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(invlookupfile))) {
        inverseLookupTable = LookupTable2D.from(bufferedReader);
      }
    }
  }

  /** get min and max possible acceleration
   * @param velocity current velocity [m/s]
   * @return a tensor of the maximal and minimal acceleration [m/s^2] */
  public Tensor getMinMaxAcceleration(Scalar velocity) {
    // the min and max values are multiplied by 1.02
    // in order to ensure that the maximum value can be outputted
    return powerLookupTable.getExtremalValues(0, velocity).multiply(Quantity.of(1.02, SI.ONE));
  }

  /** get acceleration for a given current and velocity
   * @param current the applied motor current [ARMS]
   * @param velocity the velocity [m/s]
   * @return the resulting acceleration [m/s^2] */
  public Scalar getAcceleration(Scalar current, Scalar velocity) {
    return powerLookupTable.lookup(current, velocity);
  }

  /** get the need current for a wanted acceleration
   * If the acceleration is not achievable
   * the motor current corresponding to the nearest possible acceleration value is returned
   * @param wantedAcceleration the wanted acceleration [m/s^2]
   * @param velocity the velocity [m/s]
   * @return the needed motor current [ARMS] */
  public Scalar getNeededCurrent(Scalar wantedAcceleration, Scalar velocity) {
    return inverseLookupTable.lookup(wantedAcceleration, velocity);
  }

  /** get the acceleration characterized by the relative power value
   * @param power value scaled from [-1,1] characterizing the requested power value [ONE]
   * -1: minimal acceleration (full deceleration)
   * 0: no acceleration
   * 1: maximal acceleration
   * @param velocity [m/s]
   * @return the resulting acceleration [m/s^2] */
  public Scalar getNormalizedAcceleration(Scalar power, Scalar velocity) {
    Tensor minMaxAcc = getMinMaxAcceleration(velocity);
    Scalar clippedPower = Clip.absoluteOne().apply(power);
    Tensor keypoints = Tensors.of(minMaxAcc.Get(0), RealScalar.ZERO, minMaxAcc.Get(1));
    Interpolation powerInterpolation = LinearInterpolation.of(keypoints);
    return powerInterpolation.At(clippedPower.add(RealScalar.ONE));
  }

  /** get the acceleration characterized by the relative power value
   * @param power value scaled from [-1,1] characterizing the requested power value [ONE]
   * -1: minimal acceleration (full deceleration)
   * 0: no motor current
   * 1: maximal acceleration
   * @param velocity [m/s]
   * @return the resulting acceleration [m/s^2] */
  public Scalar getNormalizedAccelerationTorqueCentered(Scalar power, Scalar velocity) {
    Tensor minMaxAcc = getMinMaxAcceleration(velocity);
    Scalar torqueFreeAcc = getAcceleration(Quantity.of(0, NonSI.ARMS), velocity);
    Scalar clippedPower = Clip.absoluteOne().apply(power);
    Tensor keypoints = Tensors.of(minMaxAcc.Get(0), torqueFreeAcc, minMaxAcc.Get(1));
    Interpolation powerInterpolation = LinearInterpolation.of(keypoints);
    return powerInterpolation.At(clippedPower.add(RealScalar.ONE));
  }
}
