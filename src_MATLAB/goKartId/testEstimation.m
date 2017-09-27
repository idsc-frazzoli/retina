clear all

Jtarget = 2.345; %kgm^2



open('testModel');
sim('testModel');
close_system('testModel');


ids = findZeroCrossings(torque);
plot(time,torque);
hold on
grid on
plot(time(ids),torque(ids), 'go');


ids = findTimeIntervals(vel, time(ids), time);

%debug output
figure
plot(time, vel);
hold on
grid on
plot(time(ids),vel(ids), 'go');

%debug output
figure
plot(time, pos);
hold on
grid on
plot(time(ids),pos(ids), 'go');


Jest = estimateJ(vel,ids, torque, ids, time)
Jest = estimateJ_pos(pos,ids, torque, ids, time)

return

%% analysis

J = 3.4;
Jhat = 4.5;
s = tf('s');

G = (1/J * 1 / s^2);
Ghat = (1/Jhat * 1 / s^2);

pidTuner(G);
Gc = 0.5355 + 10.2*s;
step(feedback(Gc*G,1))
grid on
hold on
step(feedback(Gc*Ghat,1))

