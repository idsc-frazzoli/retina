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
sel = tvmu > 360 & tvmu < 369;
VMU = VMU(sel,:);
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
%plot(tvmu,sxacc)
%plot(tvmu,syacc)
%plot(tvmu,szacc)
plot(tvmu,zacc,'Displayname','Z-axis')
plot(tvmu,xacc,'Displayname', 'X-axis')
plot(tvmu,yacc,'Displayname','Y-axis')
xlabel('time [s]')
ylabel('acceleration [m/s^2]')
legend show
legend('location','east')
print('imuPrint','-dpng','-r600')
%plot(tvmu,ygyr)
hold off

%frequency response
Fs = 1000;
T = 1/Fs;
L = numel(xacc);
t = (0:L-1)*T;

Y = fft(xacc-mean(xacc));
P2 = abs(Y/L);
%P2(P2>0.001)=0.001;
P1 = P2(1:L/2+1);

f = Fs*(0:(L/2))/L;
P1s = gaussfilter(P1, 10);
P1ss = gaussfilter(P1, 200);
P1int = P1ss./f';
P1intint = P1int./f';
figure
hold on
area(f,P1s);
xlabel('frequency (Hz)');
ylabel('Amplitude [m/s^2]');
print('imunoise','-dpng','-r600')
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