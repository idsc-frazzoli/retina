%u=[BETA,AB,TV]
clear all;
dt = 0.1;
u = zeros(30/dt,3);
u(:,2)=1;
t = (dt:dt:30)';
u(:,1)=sin(t);
tt = dt:dt:30;
%u(:,1)=1;
[x,y,o,vx,vy,vo]=simulationTest(u,dt)
figure
hold on
plot(tt,vy, 'DisplayName','v-y [m/s]')
plot(tt,vx, 'DisplayName','v-x [m/s]')
legend show