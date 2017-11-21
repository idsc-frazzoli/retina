%read the data
clear all
close all
clc
get = csvread('/home/jelavice/Downloads/sysid_get.csv',1,0);
put = csvread('/home/jelavice/Downloads/sysid_put.csv',1,0);

torque = put(:,2);
timePut = put(:,1) / 1e6; %convert us to s
vel = get(:,2);
timeGet = get(:,1) / 1e6; %convert us to s
pos = get(:, 3);


% some debug outputs
% close all
% plot(timePut, torque);
% grid on
% figure
% plot(timeGet, vel)
% grid on
% figure
% plot(timeGet, pos);
% hold on
% grid on
% plot(timeGet,cumtrapz(timeGet, vel))
%%
close all
idsTorque = findZeroCrossings(torque);

figure
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

idsPos = findTimeIntervals(pos, timePut(idsTorque), timeGet);

%debug output
figure
plot(timeGet, pos);
hold on
grid on
plot(timeGet(idsPos),pos(idsPos), 'go');

%units are all messed up
Jest = estimateJ(vel,idsVel, torque, idsTorque, timeGet) %use this estimate for velocity control
Jest = estimateJ_pos(pos,idsPos, torque, idsTorque, timeGet)

return;

%%
J = 0.0166; % 0.0166 when the kart is in the air
s = tf('s');

G = (1/J * 1 / s^2);

pidTuner(G);


margin(G * (10 + 0.6*s))
grid on

C = 10 + s*0.6;


margin(G*C)

