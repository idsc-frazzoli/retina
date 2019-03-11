motorlim = [0.5 0.5 0.5];
underlim = [1 1 0.5];
usable = [0.5 1 0.5];

sideaxis = 8;
highaxis = 4.25;
longshift = 0.5;
torquesteerpenalty = 0.7;
maxacc = 1.9;
understeerlimit = 5;
tveffect = 1.5;
close all
%Back wheels only
figure
daspect([1 1 1])
set(gca,'YAxisLocation','right');
hold on
xlabel("lateral acceleration [m/s^2]");
ylabel("longitudonal acceleration [m/s^2]");
t = 0:0.01:2*pi;
X1 = sideaxis*sin(t);
Y1 = highaxis*cos(t)+longshift;
fill(X1,Y1,usable)
txt0 = 'rear axle capacity';
%text(0,0,txt0,'HorizontalAlignment','center');
hold off
print('bw','-dpng','-r600')

%Back wheels only + max acc
figure
daspect([1 1 1])
set(gca,'YAxisLocation','right');
hold on
xlabel("lateral acceleration [m/s^2]");
ylabel("longitudonal acceleration [m/s^2]");
X2 = X1;
Y2 = min(maxacc,Y1);
fill(X1,Y1,motorlim)
fill(X2,Y2,usable)
txt = 'motor limitation';
%text(0,3,txt,'HorizontalAlignment','center');
hold off
print('bwma','-dpng','-r600')


%Back wheels only + max acc + front wheels understeering
figure
daspect([1 1 1])
set(gca,'YAxisLocation','right');
hold on
xlabel("lateral acceleration [m/s^2]");
ylabel("longitudonal acceleration [m/s^2]");
X3 = max(-understeerlimit,min(understeerlimit,X2));
Y3 = Y2;
fill(X1,Y1,motorlim)
fill(X2,Y2,underlim)
fill(X3,Y3,usable)
txt2 = 'understeering \rightarrow';
%text(0,3,txt,'HorizontalAlignment','center');
%text(understeerlimit+0.5,0,txt2,'HorizontalAlignment','right');
hold off
print('bwmaus','-dpng','-r600')


%Back wheels only + max acc + front wheels understeering + torque vectoring
figure
daspect([1 1 1])
set(gca,'YAxisLocation','right');
hold on
xlabel("lateral acceleration [m/s^2]");
ylabel("longitudonal acceleration [m/s^2]");
xi = [-understeerlimit-10,-understeerlimit,understeerlimit,understeerlimit+10]
vi = [-understeerlimit-10*torquesteerpenalty,-understeerlimit,understeerlimit,understeerlimit+10*torquesteerpenalty]
maxfdist = maxacc - Y3;
tvlimitr = understeerlimit+maxfdist*tveffect;
tvlimitl = -tvlimitr;
X4 = max(tvlimitl,min(tvlimitr,interp1(xi,vi,X2)));
Y4 = Y2;
fill(X1,Y1,motorlim)
fill(X2,Y2,underlim)
fill(X4,Y4,usable)
fill(X3,Y3,usable)
txt3 = 'torquevectoring \rightarrow';
%text(0,3,txt,'HorizontalAlignment','center');
%text(understeerlimit+0.5,0,txt3,'HorizontalAlignment','right');
hold off
print('bwmaustv','-dpng','-r600')