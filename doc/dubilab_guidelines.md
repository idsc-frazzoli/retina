# Dubilab Guidelines

These guidelines are indented to ensure a safe operation of the gokart for human and machine. Please keep in mind, that several student projects besides your own depend on the operability of the gokart. Preserving the hardware is vital to most of these projects.

* only operate the gokart when at least two adult persons are present
* the driver always wears a helmet
* do not delete log files from the gokart pc (even after they have been uploaded to the NAS)
* when in doubt, ask a more experienced person for instructions

## Begin of Day

* communicate experiments to be conducted during the day
* agree on order of experiments for instance due number of people required, or obstacle layout
* write schedule on whiteboard

## Setup of gokart PC

* run `./setup_lcm` in the home directory
* connect to ETH wifi
* activate the Davis 240C using `JAERViewer.java`

## Operations

### Before

* ensure that no loose parts are on the gokart
* ensure that no cables are sticking out
* clear rock debris from the track. Loose rocks may impact against the IMU, which deteriorates the state estimation.
* after plugging in the USB cable to the gokart PC wait ~20 seconds before starting `QuickStartGui.java`. This delay is required for the system to connect to the IMU.

### After

* make sure that software is fully terminated after `QuickStartGui` is closed: Press STOP icon in Eclipse if necessary
* switch off the electronics on the gokart: the switch on the box, the emergency stop button, and the main power switch of the gokart
* disconnect the steering battery and bring the battery to the charger on the table
* charge the steering battery and laptop after each use
* ensure that the steering battery charges displays `CHARGING` while charging

## End of Day

Before leaving the hangar

* ensure that all log files have been uploaded to NAS in the folder `gokartlogs/YYYYMMDD`. The upload rate is about 4 MB/s.
* ensure that all batteries (gokart, laptop, steering) are fully charged
* unplug the gokart battery charger from the power supply
* lock container, cabinets, and hangar doors

