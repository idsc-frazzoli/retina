x = 0:0.001:3;
points = [0,0,1,0,0]';
y0=bspline(points, x', 2, 0, 1)
y1=bspline(points, x', 2, 1, 1)
y2=bspline(points, x', 2, 2, 1)
figure
hold on
ylabel('{\boldmath$\alpha$}''Interpreter','latex')
xlabel('$\mathbf{B}(\eta)$')
plot(x,y0);
plot(x,y1);
plot(x,y2);
hold off