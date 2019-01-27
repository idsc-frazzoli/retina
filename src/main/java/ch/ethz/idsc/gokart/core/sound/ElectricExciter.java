// code by mh
package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.retina.util.math.AngleVectorLookupFloat;

public class ElectricExciter implements SoundExciter {
  private static final int SIZE = 1 << 12;
  private static final int MASK = SIZE - 1;
  private static final AngleVectorLookupFloat ANGLE_VECTOR_LOOKUP_FLOAT = //
      new AngleVectorLookupFloat(SIZE, false, 0);
  // ---
  private final float baseAmplitude;
  private final float relAmpFrequency;
  private final float baseAmpFrequency;
  private final float baseFrequency;
  private final float relFrequency;
  private final float powerFactor;
  private final float baseAmpFactor;
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
    baseAmpFactor = baseAmplitude * (amplitudeFactor - 1);
  }

  @Override
  public float getNextValue(GokartSoundState gokartSoundState, float dt) {
    dt *= SIZE;
    int dSinePosition = (int) (dt * (gokartSoundState.speed * relFrequency + baseFrequency));
    int dAmpSinePosition = (int) (dt * (gokartSoundState.speed * relAmpFrequency + baseAmpFrequency));
    sinePosition += dSinePosition;
    sinePosition &= MASK;
    float sineVal = ANGLE_VECTOR_LOOKUP_FLOAT.dy(sinePosition);
    ampSinePosition += dAmpSinePosition;
    ampSinePosition &= MASK;
    float ampSineVal = ANGLE_VECTOR_LOOKUP_FLOAT.dy(ampSinePosition);
    float ampFac = powerFactor * gokartSoundState.power + (1 - powerFactor);
    float toAdd = (ampSineVal + 1) * 0.5f * ampFac * baseAmpFactor;
    float amplitude = baseAmplitude + toAdd;
    return amplitude * sineVal;
  }
}
