function visualize(varargin)
%arguments: (stride, [start, end], [data1 name, data2 name,...], times, data1, data2,...)
%each data item is of the form [t, data]
stride = varargin{1};
limits = varargin{2};%limits are only for the positoin diagram
names = varargin{3};
time = varargin{4};
time = time(1:stride:end);
[m,~]=size(varargin{5});

%state vector [(t) Ux Uy r Ksi x y w2L w2R]'
n = nargin-4;
x = zeros(m,n);
y = zeros(m,n);
ksi = zeros(m,n);
Ux = zeros(m,n);
Uy = zeros(m,n);
r = zeros(m,n);
w2L = zeros(m,n);
w2R = zeros(m,n);
for i = 1:nargin-4
    xhist = varargin{i+4};
    [~,nn] = size(xhist);
    if(nn ~= 9)
       %is in form:  [t x y Ksi dotx_b doty_b dotKsi dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr lp]
        %convert to [Ux Uy r Ksi x y w2L w2R]
        fxhist = xhist;
        xhist = [fxhist(:,1),fxhist(:,5:7),fxhist(:,4),fxhist(:,2:3), fxhist(:,15:16)];
    end
    %interpolate
    xhist = interp1(xhist(:,1),xhist(:,2:end),time,'linear','extrap');
    %xhist = interp1(xhist(:,1),xhist(:,2:end),time,'linear','extrap');
    
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

% front axle distance from pivot point
lF = 1.4;

% rear axle distanc from pivot point
lR = 0.2;

% lateral distance of wheels from pivot point
lw = 1.2/2;

%all x
fullx = x(:);
fully = y(:);
xmid = mean(fullx);
ymid = mean(fully);
adiffx = abs(fullx-xmid);
adiffy = abs(fully-ymid);
far = max([adiffx;adiffy])*1.1;

plot(x,y);

translucency = 0.1;
axis equal
grid on
hold on
if(numel(limits)==2)
    startt = limits(1);
    endt = limits(2);
    first = find(time>startt,1);
    last = find(time>endt,1);
else
    first = 1;
    last = N;
end
xLimDown = xmid-far;
xLimUp = xmid+far;
yLimDown = ymid-far;
yLimUp = ymid+far;
xlim([xLimDown xLimUp])
ylim([yLimDown yLimUp])
for in=1:n
    for i=first:30:last
        draw_car(x(i,in),y(i,in),ksi(i,in),lF, lR, lw,1,translucency); 
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
legend(names);

subplot(4,1,2)
hold on
for in=1:n 
plot(time,Uy)
end
grid on
ylabel('U_y [m/s]')
legend(names);

subplot(4,1,3)
hold on
for in=1:n 
plot(time,r*180/pi)
end
grid on
ylabel('r [deg/s]')
legend(names);

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

