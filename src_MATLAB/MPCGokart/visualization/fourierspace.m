%lidar
x = (0:0.01:1000)';
lidary = ones(numel(x),1);
lidarnoisey = x;

figure
title('lidar pose')
hold on
area(x,[lidary,lidarnoisey])
xlabel('frequency [1/s]')
set (gca, 'Xscale', 'log')
set (gca, 'Yscale', 'log')
legend('signal','noise')
hold off

figure
title('lidar pose')
hold on
area(x,[lidary,lidarnoisey])
xlabel('frequency [1/s]')
set (gca, 'Xscale', 'log')
set (gca, 'Yscale', 'log')
legend('signal','noise')
hold off