% plots

close all
clc

translucency = 0.1;
axis equal
grid on
hold on
xLimDown = -20;
xLimUp = 50;
yLimDown = xLimDown;
yLimUp = xLimUp;
N = max(size(x));
for i=1:100:N;
    draw_car(x(i),y(i),ksi(i),params.frontL, params.rearL, params.width,1,translucency);
    xlim([xLimDown xLimUp])
    ylim([yLimDown yLimUp])   
end
h1 = draw_car( x(end),y(end),ksi(end),params.frontL, params.rearL, params.width,1,translucency);


%%

figure
subplot(4,1,1)
plot(time, w1L)
hold on
grid on
ylabel('w1L [rad/s]')

subplot(4,1,2)
plot(time, w1R)
hold on
grid on
ylabel('w1R [rad/s]')

subplot(4,1,3)
plot(time, w2L)
hold on
grid on
ylabel('w2L [rad/s]')

subplot(4,1,4)
plot(time, w2R)
hold on
grid on
ylabel('w2R [rad/s]')




%%
figure
subplot(4,1,1)
plot(time,Ux)
hold on
grid on
ylabel('U_x [m/s]')

subplot(4,1,2)
plot(time,Uy)
hold on
grid on
ylabel('U_y [m/s]')

subplot(4,1,3)
plot(time,r*180/pi)
hold on
grid on
ylabel('r [deg/s]')

subplot(4,1,4)
plot(time,ksi*180/pi)
hold on
grid on
ylabel('heading [deg]')
%plot(time, y, 'g');



%%

% plot forces (tire forces)

% figure
% subplot(4,1,1)
% plot(time, fy1L)
% hold on
% grid on
% ylabel('f1L [N]')
% 
% subplot(4,1,2)
% plot(time, fy1R)
% hold on
% grid on
% ylabel('f1R [N]')
% 
% subplot(4,1,3)
% plot(time, fy2L)
% hold on
% grid on
% ylabel('f2L [N]')
% 
% subplot(4,1,4)
% plot(time, fy2R)
% grid on
% ylabel('f2R [N]')
