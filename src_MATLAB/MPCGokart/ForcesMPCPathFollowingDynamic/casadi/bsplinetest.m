points = [0,40,40,5,0;0,0,10,9,10]';
points = [points;points;points;points];
xs = 0:0.01:40;
xxs = zeros(numel(xs),1);
yys = zeros(numel(xs),1);
c = 0;
close all
figure
hold on
for x = xs
    c = c+1;
    [xxs(c),yys(c)]=casadiDynamicBSPLINE(x,points);
    [dirx,diry]=casadiDynamicBSPLINEforward(x,points);
    plot([xxs(c),xxs(c)+dirx*10],[yys(c),yys(c)+diry*10],'r');
    [dirx,diry]=casadiDynamicBSPLINEsidewards(x,points);
    plot([xxs(c),xxs(c)+dirx*10],[yys(c),yys(c)+diry*10],'b');
end
hold off

%plot(xs,xxs);
figure
plot(xxs,yys);


%test symbolic
