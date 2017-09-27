%read the data
clear all
get = csvread('sysid_get.csv',1,0);
put = csvread('sysid_put.csv',1,0);

torque = put(:,2);
timePut = put(:,1) / 1e6; %convert us to s
vel = get(:,2);
timeGet = get(:,1) / 1e6; %convert us to s

hold on
idsTorque = findZeroCrossings(torque);
plot(timePut,torque);
hold on
grid on
plot(timePut(idsTorque),torque(idsTorque), 'mo');

idsVel = findTimeIntervals(vel, timePut(idsTorque), timeGet);

%debug output
figure
plot(timeGet, vel);
hold on
grid on
plot(timeGet(idsVel),vel(idsVel), 'go');

Jest = estimateJ(vel,idsVel, torque, idsTorque, timeGet)

%%
J = Jest;
s = tf('s');

G = (1/J * 1 / s^2);

pidTuner(G);
