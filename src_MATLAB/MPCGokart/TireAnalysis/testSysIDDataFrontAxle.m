addpath('..')  
clear
close all
userdir = getuserdir

folders = {};
targetfiles = {};
if(0)
    folders{end+1} = '/retina_out/20190125T105720/';
end
if(0)
    folders{end+1} = '/retina_out/20190125T134537/';
end
if(0)
    folders{end+1} = '/retina_out/20190128T141006/';
end
if(1)
    folders{end+1} = '/retina_out/sysidlog/';
end
if(1)
    folders{end+1} = '/retina_out/understeerTest/';
end
N = numel(folders);
tic;
for i = 1:N
    folders{i}=strcat(userdir,folders{i});
    SysID = loadSystIDData(folders{i});
end
toc;

% [t,tms,vx,vy,vr,ax,ay,s,pl,pr,pal,par,vwx]

l = 1.19;
l1 = 0.73;
l2 = l-l1;

SysID=SysID(20000:end,:);
t = SysID(:,1);
dt = (t(1001)-t(1))/1000;
tms = SysID(:,2);
vx = SysID(:,3);
vy = SysID(:,4);
vr = SysID(:,5);
ar = getDerivation(vr, 60, dt);
ax = SysID(:,6);
ay = SysID(:,7);
sax = gaussfilter(ax,100);
say = gaussfilter(ay,100);
sayf = say+l*ar;
scay = say+l2*ar;
cvy = vy+l2*vr;
s = SysID(:,8);
beta = -0.63.*s.*s.*s+0.94*s;
kinrot = vx/l.*tan(beta);
pl = SysID(:,9);
pr = SysID(:,10);
pal = SysID(:,11);
par = SysID(:,12);
ptv = (par-pal)/2;
vwx = SysID(:,13);
m = numel(t);
fsa = zeros(m,1);
fsc = cos(beta);
rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
for i = 1:m
    vel1 = rotmat(beta(i))*[vx(i);vy(i)+l*vr(i)]; 
    fsa(i) = vel1(2)/vel1(1);
end
plot(t,sayf)

figure
title('ay')
hold on
plot(t,ay)
plot(t,say)
hold off

figure
title('acc comparison')
hold on
plot(t,sax,'DisplayName', 'a-X')
plot(t,mean([pal,par],2),'DisplayName', 'power a-X')
legend show
hold off

figure
title('torque vectoring')
hold on
plot(t,vr,'DisplayName', 'rot')
plot(t,ptv,'DisplayName', 'tv')
%plot(t,-beta*10,'DisplayName', 'beta')
plot(t,kinrot,'DisplayName', 'kin rot')
legend show
hold off

figure
title('velocity')
hold on
plot(t,vy,'DisplayName', 'v-Y')
plot(t,vx,'DisplayName', 'v-X')
plot(t,vwx,'DisplayName', 'wheelspeed-X')
legend show
hold off

figure
title('rotationalAcceleration')
hold on
plot(t,vr,'DisplayName', 'rot')
plot(t,ar,'DisplayName', 'rotacc')
legend show
hold off


%%
%test 

%%
%look at back axle grip
figure
hold on
minAx = -0.4;
maxAx = 0.4;
sela = sax>minAx & sax<maxAx & vx > 3;
minAx = -3.6;
maxAx = -3.4;
selb = sax>minAx & sax<maxAx & vx > 3;
sel = vx>3;
magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
scatter(fsa(sel),-sayf(sel)./(fsc(sel)),'b');
scatter(-fsa(sel),sayf(sel)./(fsc(sel)),'b');
%scatter(-vy(selb)./vx(selb),say(selb),'r');
%scatter(vy(selb)./vx(selb),-say(selb),'r');
xlabel('front axle slip angle [1]');
ylabel('specific lateral force [N/kg]');

B = 5;
D = 1*9.81;
Ic = 1;

B1 = 12;
C1 = 1.1;
D1 = 9.8;

capfactor = @(taccx)(1-satfun((taccx/D)^2))^(1/2);
simpleslip = @(VELY,VELX,taccx)-(1/capfactor(taccx))*VELY/(VELX+0.001);
simplediraccy = @(VELY,VELX,taccx)magic(simpleslip(VELY,VELX,taccx),B,C,D);
simpleaccy = @(VELY,VELX,taccx)capfactor(taccx)*simplediraccy(VELY,VELX,taccx);
acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;
simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+0.001),B,C,D);

isa = -1:0.01:1;
mm = numel(isa);
pfy = zeros(mm,1);
for i = 1:mm
    pfy(i) = magic(isa(i),B1,C1,D1);
end

plot(isa,pfy, '--k')
print('frontaxlemodel','-dpng','-r600')
hold off