% compare matlab and code
addpath('..')
addpath('../misc')
clear rollout %!!!!clear the persisitent variables, without this it ain't gonna work

%modification by mheim: edited for use without simulink
x0 = [params.Ux0 params.Uy0 params.r0 params.ksi0 params.x0 params.y0 params.w1L0 params.w1R0 params.w2L0 params.w2R0]';
timesteps = 0:0.01:10;
time = timesteps;
[~,tn] = size(timesteps);
delta = 0.3*sin(timesteps);
lt = ones(1,tn)*0.01;
rt = lt;
u = [ delta', lt', rt']'; %these are vectors of inputs saved to workspace from simulink (can also be defined here)
out = fullmodelsimulator(x0, u, timesteps, params);

%plot all the stuff
% plots
%%
close all
clc

translucency = 0.1;
axis equal
grid on
hold on
xLimDown = -50;
xLimUp = 80;
yLimDown = xLimDown;
yLimUp = xLimUp;
N = max(size(out));
for i=1:100:N;
    %draw_car(x(i),y(i),ksi(i),params.frontL, params.rearL, params.width,1,translucency);
    out(5,i)
    out(6,i)
    draw_car(out(5,i),out(6,i),out(4,i),params.frontL, params.rearL, params.width,2,translucency);

    xlim([xLimDown xLimUp])
    ylim([yLimDown yLimUp])

end
%h1 = draw_car( x(end),y(end),ksi(end),params.frontL, params.rearL, params.width,1,translucency);
h2 = draw_car(out(5,end),out(6,end),out(4,end),params.frontL, params.rearL, params.width,2,translucency);

%legend ([h1 h2], 'simulink', 'code')



%%
figure
subplot(4,1,1)
%plot(time,Ux)
hold on
grid on
plot(time, out(1,:),'r')
ylabel('U_x [m/s]')
%legend('simulink', 'code')


subplot(4,1,2)
%plot(time,Uy)
hold on
grid on
plot(time, out(2,:),'r')
ylabel('U_y [m/s]')

subplot(4,1,3)
%plot(time,r*180/pi)
hold on
grid on
plot(time, out(3,:)*180/pi,'r')
ylabel('r [deg/s]')

subplot(4,1,4)
%plot(time,ksi*180/pi)
hold on
grid on
plot(time, out(4,:)*180/pi,'r')
ylabel('heading [deg]')
%plot(time, y, 'g');

%%
figure
subplot(4,1,1)
%plot(time, w1L)
hold on
plot(time, out(7,:),'r')
grid on
ylabel('w1L [rad/s]')
legend('simulink', 'code')

subplot(4,1,2)
%plot(time, w1R)
hold on
grid on
plot(time, out(8,:),'r')
ylabel('w1R [rad/s]')

subplot(4,1,3)
%plot(time, w2L)
hold on
grid on
plot(time, out(9,:),'r')
ylabel('w2L [rad/s]')

subplot(4,1,4)
%plot(time, w2R)
hold on
grid on
plot(time, out(10,:),'r')
ylabel('w2R [rad/s]')



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
