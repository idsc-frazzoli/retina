addpath('..')  
clear
userdir = getuserdir

folders = {};
targetfiles = {};
if(0)
    folders{end+1} = '/retina_out/20190125T105720/';
end
if(0)
    folders{end+1} = '/retina_out/20190125T134537/';
end
if(1)
    folders{end+1} = '/retina_out/20190128T141006/';
end
N = numel(folders);
tic;
for i = 1:N
    folders{i}=strcat(userdir,folders{i});
    VMU = loadVMUIMUData(folders{i});
    Davis = loadDavisIMUData(folders{i});
end
toc;

tdavis = Davis(:,1);
tvmu = VMU(:,1);
tvmu=tvmu/1000;
tvmu=tvmu-tvmu(1);
yacc = VMU(:,3);
xacc = VMU(:,2);
zacc = VMU(:,4);
zgyr = VMU(:,7);
dxacc = Davis(:,3);
dyacc = Davis(:,4);
dzacc = Davis(:,5);

sxacc = gaussfilter(xacc,500);
syacc = gaussfilter(yacc,500);
szacc = gaussfilter(zacc,500);
dsxacc = gaussfilter(dxacc,500);
dsyacc = gaussfilter(dyacc,500);
dszacc = gaussfilter(dzacc,500);

figure
title('VMU');
hold on
plot(tvmu,sxacc)
plot(tvmu,syacc)
plot(tvmu,szacc)
%plot(tvmu,ygyr)
hold off

figure
title('davis');
hold on
plot(tdavis,dsxacc)
plot(tdavis,dsyacc)
plot(tdavis,dszacc)
%plot(tvmu,ygyr)
hold off

figure
title('frequency response')
fx = fft(xacc);
n = length(xacc);          % number of samples
fs = 100;
f = (0:n-1)*(fs/n);     % frequency range
power = abs(fx).^2/n;  
plot(f,power)
xlabel('Frequency')
ylabel('Power')