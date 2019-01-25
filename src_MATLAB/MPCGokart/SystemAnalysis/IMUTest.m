addpath('..')  
clear
userdir = getuserdir

folders = {};
targetfiles = {};
if(1)
    folders{end+1} = '/retina_out/20190125T105720/';
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

sxacc = gaussfilter(xacc,100);
syacc = gaussfilter(yacc,100);
szacc = gaussfilter(zacc,100);
dsxacc = gaussfilter(dxacc,100);
dsyacc = gaussfilter(dyacc,100);
dszacc = gaussfilter(dzacc,100);

figure
hold on
plot(tvmu,sxacc)
plot(tvmu,syacc)
plot(tvmu,szacc)
%plot(tvmu,ygyr)
hold off

figure
hold on
plot(tdavis,dsxacc)
plot(tdavis,dsyacc)
plot(tdavis,dszacc)
%plot(tvmu,ygyr)
hold off