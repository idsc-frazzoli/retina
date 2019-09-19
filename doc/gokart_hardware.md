# GoKart Hardware
*About:* This is a short overview of the go-kart fleet.

## GoKart 1 - K.I.T.T.
* *About:* First autonomous vehicle, operational since December 2017, named after the fictional car K.I.T.T from the TV series "Knight Rider"
* *Status:* operational
* *Technical data:*
    * Vehicle base: Rimo Sinus Ion
    * Motor: Heinzmann PMS 100
    * Cooling Air-cooled with at least 5[m/s]
    * DC link voltage Udc 48[V] DC
    * Nominal Speed nn 6500[rpm]
    * Rated torque Mn 3.82[Nm]
        * Rated power P 2.6[kW]
        * Rated AC current In 63.9[A]
    * Battery: LiFeMnPo4
        * 16 cells in series
        * Capacity: 100[Ah]
        * Peak load: 10 C
        * Constant load: 3.5 C
        * Nominal voltage: 51.2[V]
        * Maximum voltage: 57.12[V]
        * Minimum voltage: 44.8[V]
    * Modifications:
        * Steering: Steering unit build in, manual steering still possible
            * TKP ColPas C-EPS
            * 12[V], 50[A] rated, 90[A] peak
            * CAN (bare CAN) driven from laptop through MABX (micro-autobox)
            * separated steering battery needed
        * Breaking: breaking motor build in, manual braking still possible
            * LinMot
            * 24[V]
            * CAN open controlled
        * IO Interface: IO board to interact with basic go-kart controls like throttle, buttons, etc.
            * LabJack U3 HV
            * USB powered
            * USB controlled
            * used for: throttle (AIN2), push button "Boost" (FIO4), push button "Reverse" (FIO5), LED "Boost" (FIO6)

## GoKart 2 - BumbleBee
* *About:* Second autonomous vehilce, named after the fictional character bumblebee from the Transformers franchise
* *Status:* in development
* *Technical data:*
    * Vehicle: Rimo Sinus Ion
    * “Difficult to see. Always in motion is the future…” - Yoda

## GoKart 3 - Herbie
* *About:* Manual driven go-kart, named after the fictional car Herbie from the Disney movie series  
* *Status:* operational
* *Technical data:*
    * Vehicle: Rimo Sinus Ion
    * unmodified vehicle, maybe some hardpoints for sensors added later
