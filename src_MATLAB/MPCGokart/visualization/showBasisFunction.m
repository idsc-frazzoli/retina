x = 0:0.01:3;
y = arrayfun(@(vv)qsplinebf(vv),x);
close all
plot(x,y)
xticks([0 1 2 3])
yticks([0 0.5 1])
yticklabels({'0','^{1}/_{2}','1'})
grid on
grid minor
ylim([0 1])
daspect([1 1 1])
print('bsplinebasisfunction','-dpng','-r600')