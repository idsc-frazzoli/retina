clear all;
close all;
x = 0:0.01:2;
y = [];
for xx = 0:0.01:2
    y = [y,satfun(xx)];
end

plot(x,y);