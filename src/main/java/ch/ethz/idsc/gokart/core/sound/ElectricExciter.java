// code by mh
package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.sca.Increment;
import ch.ethz.idsc.tensor.sca.Sin;

public class ElectricExciter implements SoundExciter {
  /** size is required to be a power of 2 */
  private static final int SIZE = 1 << 12;
  private static final int MASK = SIZE - 1;
  private static final float[] SINE = Primitives.toFloatArray( //
      Sin.of(Range.of(0, SIZE).multiply(DoubleScalar.of(2 * Math.PI / SIZE))));
  // ---
  private final float baseAmplitude;
  private final float relAmpFrequency;
  private final float baseAmpFrequency;
  private final float baseFrequency;
  private final float relFrequency;
  private final float powerFactor;
  private final float[] lookupTable1D; //
  // ---
  private int sinePosition = 0;
  private int ampSinePosition = 0;

  public ElectricExciter( //
      float relFrequency, float baseFrequency, float relAmpFrequency, //
      float baseAmpFrequency, float baseAmplitude, float amplitudeFactor, //
      float powerFactor) {
    this.relFrequency = relFrequency;
    this.relAmpFrequency = relAmpFrequency;
    this.powerFactor = powerFactor;
    this.baseAmplitude = baseAmplitude;
    this.baseFrequency = baseFrequency;
    this.baseAmpFrequency = baseAmpFrequency;
    float baseAmpFactor = baseAmplitude * (amplitudeFactor - 1);
    Tensor tensor = Sin.of(Range.of(0, SIZE).multiply(DoubleScalar.of(2 * Math.PI / SIZE)));
    lookupTable1D = Primitives.toFloatArray(tensor.map(Increment.ONE).multiply(DoubleScalar.of(baseAmpFactor * 0.5)));
  }

  @Override
  public float getNextValue(GokartSoundState gokartSoundState, float dt) {
    dt *= SIZE;
    int dSinePosition = (int) (dt * (gokartSoundState.speed * relFrequency + baseFrequency));
    int dAmpSinePosition = (int) (dt * (gokartSoundState.speed * relAmpFrequency + baseAmpFrequency));
    float sineVal = SINE[sinePosition];
    sinePosition += dSinePosition;
    sinePosition &= MASK;
    float ampSineVal = lookupTable1D[ampSinePosition];
    ampSinePosition += dAmpSinePosition;
    ampSinePosition &= MASK;
    float ampFac = powerFactor * gokartSoundState.power + (1 - powerFactor);
    float amplitude = baseAmplitude + ampSineVal * ampFac;
    return amplitude * sineVal;
  }
}
