%code by mh
clear all
addpath('..')
addpath('../SystemAnalysis')
folder = strcat(getuserdir,'/Documents/ML_out/');
file = 'brakingMLData.csv';
tireradius = 0.12;
% whole table: [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr lp ltemp dotltemp]
M = csvread(strcat(folder,file));
temp = M(:,20);
dottemp = M(:,21);
acc = tireradius*mean(M(:,17:18),2);
acc = gaussfilter(acc,10);
spd = tireradius*mean(M(:,15:16),2);
bpos = -M(:,19);
bselect = bpos > 250000 & bpos < 390000 & spd>0.2;
bselect = imerode(bselect,ones(100,1));
nbselect = bpos < 60000 & bpos > 40000;
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
subplot(2,2,2)
title('effect of brake')
hold on
xlabel('Brakingposition [1]')
ylabel('Acceleration [m/s²]')
scatter(bpos(bselect), acc(bselect));
hold off
subplot(2,2,3)
hold on
title('cooldown (no braking)')
xlabel('temp [°C]')
ylabel('temp change [°C/s]')
scatter(temp(nbselect), dottemp(nbselect));
hold off
subplot(2,2,4)
hold on
title('heatup (braking)')
xlabel('temp [°C]')
ylabel('temp change [°C/s]')
scatter(temp(bselect), dottemp(bselect));
hold off