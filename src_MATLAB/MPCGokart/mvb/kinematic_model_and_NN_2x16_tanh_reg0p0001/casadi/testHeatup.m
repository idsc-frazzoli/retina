xs = -2:0.01:2.5;
ys = zeros(numel(xs),1);
c = 1;
for x = xs
   ys(c)=heatupfunction(x);
   c = c+1;
end
close all
plot(xs,ys)

xts = 0:0.1:100;
yts = cooldownfunction(xts);
%plot(xts,yts);