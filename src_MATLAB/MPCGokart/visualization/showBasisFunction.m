x = 0:0.01:3;
y = arrayfun(@(vv)qsplinebf(vv),x);
close all
plot(x,y)
xticks([0 1 2 3])
yticks([0 0.5 0.75 1])
yticklabels({'0','^{1}/_{2}','^{3}/_{4}','1'})
ylabel('activation');
xlabel('local B-Spline position');
grid on
ylim([0 1])
daspect([1 1 1])
print('bsplinebasisfunction','-dpng','-r600')