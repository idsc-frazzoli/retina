// code by mh
package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.Exciter;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;

public class ElectricExciter extends Exciter {
  private final float baseAmplitude;
  private final float amplitudeFactor;
  private final float relAmpFrequency;
  private final float baseAmpFrequency;
  private final float baseFrequency;
  private final float relFrequency;
  private final float powerFactor;
  // ---
  private float sinePosition = 0;
  private float ampSinePosition = 0;
  private float dSinePosition;
  private float dAmpSinePosition;

  public ElectricExciter( //
      float relFrequency, float baseFrequency, float relAmpFrequency, //
      float baseAmpFrequency, float baseAmplitude, float amplitudeFactor, //
      float powerFactor) {
    this.relFrequency = relFrequency;
    this.relAmpFrequency = relAmpFrequency;
    this.powerFactor = powerFactor;
    this.amplitudeFactor = amplitudeFactor;
    this.baseAmplitude = baseAmplitude;
    this.baseFrequency = baseFrequency;
    this.baseAmpFrequency = baseAmpFrequency;
  }

  @Override
  public float getNextValue(MotorState state, float dt) {
    dSinePosition = dt * (state.speed * relFrequency + baseFrequency);
    dAmpSinePosition = dt * (state.speed * relAmpFrequency + baseAmpFrequency);
    sinePosition += dSinePosition * Math.PI * 2;
    ampSinePosition += dAmpSinePosition * Math.PI * 2;
    if (sinePosition > Math.PI * 2)
      sinePosition -= Math.PI * 2;
    if (ampSinePosition > Math.PI * 2)
      ampSinePosition -= Math.PI * 2;
    float sineVal = (float) Math.sin(sinePosition);
    float ampSineVal = (float) Math.sin(ampSinePosition);
    float ampFac = powerFactor * state.power + (1 - powerFactor);
    float toAdd = (ampSineVal + 1) / 2.0f * baseAmplitude * ampFac * (amplitudeFactor - 1);
    float amplitude = baseAmplitude + toAdd;
    return amplitude * sineVal;
  }
}
