close all
clear all

points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
points = [points(3:end,:);points(1:2,:)]
mpoints = mean(points(:,1:2));
[m,n]=size(points);
startpoints = [(1:m+2)',zeros(m+2,1)]*10;
points = [points(:,1:2)-ones(m,1)*mpoints,points(:,3)];
points = points(m:-1:1,:)



frames = 100;
vidfile = VideoWriter('testmovie','Motion JPEG AVI');
open(vidfile);

for i = 0:frames
    figure(1)
    clf
    hold on
    set(gca,'visible','off')
    daspect([1 1 1])
    blendfactor = i/frames;
   [leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),blendfactor,startpoints);
   plot(leftline(:,1),leftline(:,2),'b')
   plot(rightline(:,1),rightline(:,2),'b')
   plot(middleline(:,1),middleline(:,2),'--r')
   %plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-ks')
   plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
   plot(llc(:,1),llc(:,2),'-k');
      plot(rlc(:,1),rlc(:,2),'-k');
   hold off
   drawnow
    F = getframe(gcf); 
    writeVideo(vidfile,F);
end
close(vidfile)