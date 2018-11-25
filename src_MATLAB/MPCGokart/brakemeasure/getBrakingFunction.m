%code by mh
clear all
addpath('..')
addpath('../SystemAnalysis')
folder = strcat(getuserdir,'/Documents/ML_out/');
file = 'brakingMLData.csv';
tireradius = 0.12;
% whole table: [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr lp ltemp dotltemp]
M = csvread(strcat(folder,file));
M = M(20000:end,:);
temp = M(:,20);
dottemp = M(:,21);
acc = tireradius*mean(M(:,17:18),2);
acc = gaussfilter(acc,10);
spd = tireradius*mean(M(:,15:16),2);
bpos = -M(:,19)/100000;
brakestart = 2.5;
movestart = 2.0;
brakeend = 4.0;
bselect = bpos > brakestart & bpos < brakeend & spd>0.2;
bselect = imerode(bselect,ones(100,1));
bexpselect = bpos > movestart & bpos < brakeend & spd>0.2;
bexpselect = imerode(bexpselect,ones(100,1));
nbselect = bpos < 0.6 & bpos > 0.4;
nbselect = imerode(nbselect,ones(100,1));
dottemp(1:1000)=0;
dottemp(end-1000:end)=0;
t = M(:,1);
close all
subplot(2,2,1)
title('brake position and heat')
hold on
xlabel('Time [s]')
yyaxis left
ylabel('position')
plot(t,-M(:,19))
yyaxis right
ylabel('temp °C]')
plot(t,M(:,20))
hold off

pbrake = polyfit(bpos(bselect), acc(bselect),2);

accoffset = polyval(pbrake, brakestart);
pnormbrake = polyfit(bpos(bselect)-brakestart, -acc(bselect)+accoffset,2);
xb = 0:0.01:brakeend-brakestart;
yb = polyval(pnormbrake,xb);
subplot(2,2,2)
title('effect of brake')
hold on
xlabel('Brakingposition [cm]')
ylabel('braking effect [m/s²]')
scatter(bpos(bexpselect)-brakestart, -acc(bexpselect)+accoffset);
%scatter(bpos-brakestart, -acc+accoffset);
plot(xb,yb);
hold off


pcooldown = polyfit(temp(nbselect), dottemp(nbselect),1);
subplot(2,2,3)
xcd = min(temp(nbselect)):0.01:max(temp(nbselect));
ycd = polyval(pcooldown,xcd);
hold on
title('cooldown (no braking) equilibrium at ca. 59° C')
xlabel('temp [°C]')
ylabel('temp change [°C/s]')
scatter(temp(nbselect), dottemp(nbselect));
plot(xcd,ycd);
hold off

hu = polyval(pnormbrake, bpos(bselect)-brakestart);
pnormheatup = polyfit(hu,dottemp(bselect),3);
subplot(2,2,4)
xhu = min(hu):0.01:max(hu);
yhu = polyval(pnormheatup,xhu);
hold on
title('heatup (braking)')
xlabel('brake [m/s²]')
ylabel('temp change [°C/s]')
scatter(hu, dottemp(bselect));
plot(xhu,yhu);
%pnormheatup = polyfit()
hold off