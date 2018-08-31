function visualize(varargin)
global param

stride = varargin{1};
names = varargin({2});
time = varargin{3};
time = time(1:stride:end);
[m,~]=size(varargin{4});

%state vector [Ux Uy r Ksi x y w2L w2R]'
n = nargin-3;
x = zeros(m,n);
y = zeros(m,n);
ksi = zeros(m,n);
Ux = zeros(m,n);
Uy = zeros(m,n);
r = zeros(m,n);
w2L = zeros(m,n);
w2R = zeros(m,n);
for i = 4:nargin
    xhist = varargin{i};
    xhist = xhist(1:stride:end,:);
    Ux(:,i)=xhist(:,1);
    Uy(:,i)=xhist(:,2);
    r(:,i)=xhist(:,3);
    ksi(:,i)=xhist(:,4);
    x(:,i)=xhist(:,5);
    y(:,i)=xhist(:,6);
    w2L(:,i)=xhist(:,7);
    w2R(:,i)=xhist(:,8);
end
[N,~]=size(x);

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
for in=1:n
    for i=1:100:N
        draw_car(x(i,in),y(i,in),ksi(i,in),params.frontL, params.rearL, params.width,1,translucency);
        xlim([xLimDown xLimUp])
        ylim([yLimDown yLimUp])   
    end
end
%h1 = draw_car( x(end),y(end),ksi(end),params.frontL, params.rearL, params.width,1,translucency);


%%

figure

subplot(2,1,1)
hold on
for in=1:n 
plot(time, w2L(:,in))
end
ylabel('w2L [rad/s]')

subplot(2,1,2)
hold on
for in=1:n 
plot(time, w2R(:,in))
end
grid on
ylabel('w2R [rad/s]')
legend(names);




%%
figure
subplot(4,1,1)
hold on
for in=1:n 
plot(time,Ux)
end
grid on
ylabel('U_x [m/s]')

subplot(4,1,2)
hold on
for in=1:n 
plot(time,Uy)
end
grid on
ylabel('U_y [m/s]')

subplot(4,1,3)
hold on
for in=1:n 
plot(time,r*180/pi)
end
grid on
ylabel('r [deg/s]')

subplot(4,1,4)
hold on
for in=1:n
plot(time,ksi*180/pi)
end
grid on
ylabel('heading [deg]')
legend(names);
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

end

