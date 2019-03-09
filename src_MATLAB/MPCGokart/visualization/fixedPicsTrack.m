close all
clear all

points = [36.2,52,58.2,52,51,46,41.8;44.933,58.2,54.8,46,41,40,38.33;1.7,1.2,2.3,0.5,0.6,0.5,1.8]';
points = [points(3:end,:);points(1:2,:)]
mpoints = mean(points(:,1:2));
[m,n]=size(points);
startpoints = [(0:m+1)',zeros(m+2,1)];
points = [points(:,1:2)-ones(m,1)*mpoints,points(:,3)];
points = points(m:-1:1,:)

%show track spline
figure
hold on
set(gca,'visible','off')
daspect([1 1 1])
[leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),1,startpoints);
%plot(leftline(:,1),leftline(:,2),'b')
%plot(rightline(:,1),rightline(:,2),'b')
plot(middleline(:,1),middleline(:,2),'--r','LineWidth',2)
plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-ks')
%plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
%plot(llc(:,1),llc(:,2),'-k');
%plot(rlc(:,1),rlc(:,2),'-k');
hold off
print('centerline','-dpng','-r600')
xl = xlim;
yl = ylim;


%show track width spline
figure
hold on
xlabel('control point')
ylabel('track width [m]')
[leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),0,startpoints);
plot(leftline(:,1),leftline(:,2),'b')
plot(rightline(:,1),rightline(:,2),'b')
plot(middleline(:,1),middleline(:,2),'--r','LineWidth',2)
%plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-ks')
plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'ks') 
plot(llc(:,1),llc(:,2),'-k');
plot(rlc(:,1),rlc(:,2),'-k');
hold off
print('widthcontrol','-dpng','-r600')


%show track width spline
figure
xlim(xl);
ylim(yl);
hold on
set(gca,'visible','off')
daspect([1 1 1])
[leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),1,startpoints);
plot(leftline(:,1),leftline(:,2),'b')
plot(rightline(:,1),rightline(:,2),'b')
plot(middleline(:,1),middleline(:,2),'--r','LineWidth',2)
%plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-ks')
plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
plot(llc(:,1),llc(:,2),'-k');
plot(rlc(:,1),rlc(:,2),'-k');
hold off
print('combined','-dpng','-r600')


%show partial track width spline
figure
xlim(xl);
ylim(yl);
hold on
set(gca,'visible','off')
daspect([1 1 1])
[leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),1,startpoints);
plot(leftline(:,1),leftline(:,2),'b')
plot(rightline(:,1),rightline(:,2),'b')
plot(middleline(:,1),middleline(:,2),'--r','LineWidth',2)
plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-ks')
%plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
%plot(llc(:,1),llc(:,2),'-k');
%plot(rlc(:,1),rlc(:,2),'-k');
hold off
print('preextracted','-dpng','-r600')

figure
xlim(xl);
ylim(yl);
pointsel = 2:7
hold on
set(gca,'visible','off')
daspect([1 1 1])
[leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),1,startpoints);
[trackm,~]=size(leftline);
tracksel=1*trackm/m:1:5*trackm/m;
plot(leftline(tracksel,1),leftline(tracksel,2),'b')
plot(rightline(tracksel,1),rightline(tracksel,2),'b')
plot(middleline(tracksel,1),middleline(tracksel,2),'--r','LineWidth',2)
plot(points(pointsel,1),points(pointsel,2), '-ks')
%plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
%plot(llc(:,1),llc(:,2),'-k');
%plot(rlc(:,1),rlc(:,2),'-k');
hold off
print('extracted','-dpng','-r600')

%end result
figure
xlim(xl);
ylim(yl);
hold on
set(gca,'visible','off')
daspect([1 1 1])
[leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),1,startpoints);
plot(leftline(:,1),leftline(:,2),'b')
plot(rightline(:,1),rightline(:,2),'b')
plot(middleline(:,1),middleline(:,2),'--r','LineWidth',2)
%plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-ks')
%plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
%plot(llc(:,1),llc(:,2),'-k');
%plot(rlc(:,1),rlc(:,2),'-k');
hold off
print('finished','-dpng','-r600')