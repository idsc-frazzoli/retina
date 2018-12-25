x = 0:0.01:3;
y = arrayfun(@(vv)qsplinebf(vv),x);
close all
plot(x,y)
ylim([0 1]
daspect([1 1 1])