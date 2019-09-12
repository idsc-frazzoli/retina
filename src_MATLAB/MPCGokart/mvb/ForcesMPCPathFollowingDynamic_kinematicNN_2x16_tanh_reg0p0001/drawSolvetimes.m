figure
xlabel('[ms]')
title('Execution times')

csolv = solvetimes*0.6*1000;
csolv = csolv(csolv<100);
csvwrite('extimes.csv',csolv);
histogram(csolv)
mean(csolv)
max(csolv)
min(csolv)
set(gca, 'YTick', [])
xlabel('execution time[ms]')
print('solvetimes','-dpng','-r600')