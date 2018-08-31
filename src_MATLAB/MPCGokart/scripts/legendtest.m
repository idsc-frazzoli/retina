x = linspace(-2*pi,2*pi,100);
y1 = sin(x);
y2 = cos(x);

figure
plot(x,y1,x,y2)
l=['sin(x)';'cos(x)'];
legend(l)