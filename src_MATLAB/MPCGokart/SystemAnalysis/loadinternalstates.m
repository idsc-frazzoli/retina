%code by mheim
%load steering, braking and wheelspeed
%steering
%powersteer = csvread

startt = 500;
endt = startt+70;

close all
st = table2array(powersteer(:,1));
sdota = table2array(powersteer(:,2));%steering speed
sa = table2array(powersteer(:,9));%steering angle
%sb = table2array(powersteer(:,4));%still don't know what this is
%sb = gaussfilter(sa,1000);
%sa = gaussfilter(sa,10);
sdota = gaussfilter(sdota,100);

sfirst = find(st>startt,1);
slast = find(st>endt,1);

st = st(sfirst:slast);
sdota = sdota(sfirst:slast);
sa = sa(sfirst:slast);

figure
hold on
plot(st,sa);
plot(st,sdota*0.1);
hold off

%load power
pt = table2array(powerrimo(:,1));
pcl = table2array(powerrimo(:,3));
pcr = table2array(powerrimo(:,10));

pfirst = find(pt>startt,1);
plast = find(pt>endt,1);

pt = pt(pfirst:plast);
pcl = pcl(pfirst:plast);
pcr = pcr(pfirst:plast);

figure
hold on
plot(pt,pcl);
plot(pt,pcr);
hold off

%load wheelspeeds
wt = table2array(rimorate(:,1));
wrl = table2array(rimorate(:,4));
wrr = table2array(rimorate(:,5));

wrl = gaussfilter(wrl,10);
wrr = gaussfilter(wrr,10);

wfirst = find(wt>startt,1);
wlast = find(wt>endt,1);

wt = wt(wfirst:wlast);
wrl = wrl(wfirst:wlast);
wrr = wrr(wfirst:wlast);

figure
hold on
plot(wt,wrl);
plot(wt,wrr);
plot(wt,(wrr-wrl));

hold off

