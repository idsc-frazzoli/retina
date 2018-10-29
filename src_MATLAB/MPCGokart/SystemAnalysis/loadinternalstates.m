function M = loadinternalstates(RTSM,folder)
%code by mheim
%load steering, braking and wheelspeed and match to RTS State matrix

close all

powersteer = csvread(strcat(folder,'powersteer.csv'));
powerrimo = csvread(strcat(folder,'powerrimo.csv'));
rimorate = csvread(strcat(folder,'rimorate.csv'));

linmot = csvread(strcat(folder,'linmot.csv'));
lt = linmot(:,1);
lp = linmot(:,4);
%lp = max(0,-lp-100000);
%lp = lp/100000; %data is scaled and truncated (completely arbitrary)
ltemp = mean(linmot(:,6:7),2);
ldt = (lt(end)-lt(1))/numel(lt);
dotltemp = getDerivation(ltemp,50,ldt);
%plot(lt,lp);

st = powersteer(:,1);
sdota = powersteer(:,2);%steering speed
sa = powersteer(:,9);%steering angle
%sb = table2array(powersteer(:,4));%still don't know what this is
%sb = gaussfilter(sa,1000);
%sa = gaussfilter(sa,10);
sdota = gaussfilter(sdota,10);


%load power
pt = powerrimo(:,1);
pcl = powerrimo(:,16);
pcr = powerrimo(:,17);

plot(pt,pcl);

%load wheelspeeds
wt = rimorate(:,1);
wrl = rimorate(:,4);
wrr = rimorate(:,5);
wdt = (wt(end)-wt(1))/numel(wt);
dotwrl = getDerivation(wrl,5,wdt);
dotwrr = getDerivation(wrr,5,wdt);

wrl = gaussfilter(wrl,10);
wrr = gaussfilter(wrr,10);

cRTSM = convertM(RTSM);

t = cRTSM(:,1);
% whole table: [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr lp  ltemp dotltemp]

M = [cRTSM,...%t x y Ksi dotx_b doty_b dotKsi dotdotx_b dotdoty_b dotdotKsi
    interp1(st,sa,t),...%sa
    interp1(st,sdota,t),...%sdota
    interp1(pt,pcl,t),...%pcl
    interp1(pt,pcr,t),...%pcr
    interp1(wt,wrl,t),...%wrl
    interp1(wt,wrr,t),...%wrr
    interp1(wt,dotwrl,t),...%dotwrl
    interp1(wt,dotwrr,t),...%dotwrr
    interp1(lt,lp,t),...%lp
    interp1(lt,ltemp,t),...%ltemp
    interp1(lt,dotltemp,t)];%dotltemp
show = 1;

    %if(show)
        %figure;
        %subplot(

    %end

end