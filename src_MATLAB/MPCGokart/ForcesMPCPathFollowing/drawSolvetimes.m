figure
xlabel('[ms]')
title('Execution times')

csolv = solvetimes*0.7*1000;
csolv = csolv(csolv<30);
csvwrite('extimes.csv',csolv);
histogram(csolv)
mean(csolv)
max(csolv)
min(csolv)
set(gca, 'YTick', [])
xlabel('execution time[ms]')
print('solvetimes','-dpng','-r600')