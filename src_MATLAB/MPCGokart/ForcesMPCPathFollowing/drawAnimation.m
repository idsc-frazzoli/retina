frames = 200;
vidfile = VideoWriter('kinematic','Motion JPEG AVI');
open(vidfile);
tracelength = 500;
for iff = 1:frames
    figure(1)
    clf
    daspect([1 1 1])
    hold on
    set(gca,'visible','off')
    if(1)
    %points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
    points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
       [leftline,middleline,rightline] = drawTrack(points(:,1:2),points(:,3));
       plot(leftline(:,1),leftline(:,2),'k')
       plot(rightline(:,1),rightline(:,2),'k')
    end
    endind = iff*eulersteps*planintervall;
    for i=max(1,endind-tracelength):endind
        next = i+1;
        x = [p(i,1),p(next,1)];
       y = [p(i,2),p(next,2)];
       vc = acc(i)/maxacc;
       line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]);
    end
    iff
    plot(plansx(iff+1,:),plansy(iff+1,:),'-b');
    
    drawnow
    F = getframe(gcf); 
    writeVideo(vidfile,F);
end
close(vidfile)