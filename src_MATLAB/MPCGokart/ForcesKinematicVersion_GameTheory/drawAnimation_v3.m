
frames = tend;
vidfile = VideoWriter('PotGame','Motion JPEG AVI');
vidfile.FrameRate = 10;
open(vidfile);
set(gcf,'position',[100,100,1000,800])
tracelength = 500;
for iff = 1:frames-1
    figure(1)
    clf
    daspect([1 1 1])
    hold on
    set(gca,'visible','off')
    if(1)
    %points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
    %points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
       [leftline,middleline,rightline] = drawTrack(points(:,1:2),points(:,3)+0.5);
       [rleftline,rmiddleline,rrightline] = drawTrack(points(:,1:2),points(:,3));
       plot(leftline(:,1),leftline(:,2),'b')
       plot(rightline(:,1),rightline(:,2),'b')
       plot(rleftline(:,1),rleftline(:,2),'--b')
       plot(rrightline(:,1),rrightline(:,2),'--b')
       [leftline2,middleline2,rightline2] = drawTrack(points2(:,1:2),points2(:,3)+0.5);
       [rleftline2,rmiddleline2,rrightline2] = drawTrack(points2(:,1:2),points2(:,3));
       plot(leftline2(:,1),leftline2(:,2),'b')
       plot(rightline2(:,1),rightline2(:,2),'b')
       plot(rleftline2(:,1),rleftline2(:,2),'--b')
       plot(rrightline2(:,1),rrightline2(:,2),'--b')
%        [leftline3,middleline3,rightline3] = drawTrack(points3(:,1:2),points3(:,3)+0.5);
%        [rleftline3,rmiddleline3,rrightline3] = drawTrack(points3(:,1:2),points3(:,3));
%        plot(leftline3(:,1),leftline3(:,2),'b')
%        plot(rightline3(:,1),rightline3(:,2),'b')
%        plot(rleftline3(:,1),rleftline3(:,2),'--b')
%        plot(rrightline3(:,1),rrightline3(:,2),'--b')
    end
    if(1)
       %plot control points
       sph = splinepointhist(iff*planintervall,:);
       sphx = sph(2:1+pointsN)';
       sphy = sph(2+pointsN:1+2*pointsN)';
       sphr = sph(2+2*pointsN:1+3*pointsN)';
       [leftline,middleline,rightline]=drawTrack([sphx,sphy],sphr);
       plot(leftline(1:800,1),leftline(1:800,2),'--b')
       plot(rightline(1:800,1),rightline(1:800,2),'--b')
       plot(sphx,sphy,'--s','color',[.7 .2 .2]);
    end
    endind = iff*eulersteps*planintervall;
    for i=max(1,endind-tracelength):endind-1
        
       next = i+1;
       x = [p(i,1),p(next,1)];
       y = [p(i,2),p(next,2)];
       x2 = [p2(i,1),p2(next,1)];
       y2 = [p2(i,2),p2(next,2)];
       x3 = [p3(i,1),p3(next,1)];
       y3 = [p3(i,2),p3(next,2)];
       
       vc = acc(i)/maxacc;
       vc2 = acc2(i)/maxacc2;
       vc3 = acc3(i)/maxacc3;
       line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0],'LineWidth',2);
       line(x2,y2,'Color',[0.5-0.5*vc2,0.5+0.5*vc2,0],'LineWidth',2);
       line(x3,y3,'Color',[0.5-0.5*vc3,0.5+0.5*vc3,0],'LineWidth',2);
       
    end
    iff
    plot(plansx(iff+1,:),plansy(iff+1,:),'-k','LineWidth',6);
    plot(plansx2(iff+1,:),plansy2(iff+1,:),'-k','LineWidth',6);
    plot(plansx3(iff+1,:),plansy3(iff+1,:),'-k','LineWidth',6);
    
    gklx = [-0.4,1.4,1.4,-0.4,-0.4];
    gkly = [-0.8,-0.8,0.8,0.8,-0.8];
    gklp = [gklx;gkly];
    
    theta = atan2(plansy(iff+1,2)-plansy(iff+1,1),plansx(iff+1,2)-plansx(iff+1,1)); % to rotate 90 counterclockwise
    R = [cos(theta) -sin(theta); sin(theta) cos(theta)];
    rgklp = [plansx(iff+1,1);plansy(iff+1,1)]+R*gklp;
    fill(rgklp(1,:),rgklp(2,:),'b');
    
    theta2 = atan2(plansy2(iff+1,2)-plansy2(iff+1,1),plansx2(iff+1,2)-plansx2(iff+1,1)); % to rotate 90 counterclockwise
    R2 = [cos(theta2) -sin(theta2); sin(theta2) cos(theta2)];
    rgklp2 = [plansx2(iff+1,1);plansy2(iff+1,1)]+R2*gklp;
    fill(rgklp2(1,:),rgklp2(2,:),'m');
    
    theta3 = atan2(plansy3(iff+1,2)-plansy3(iff+1,1),plansx3(iff+1,2)-plansx3(iff+1,1)); % to rotate 90 counterclockwise
    R3 = [cos(theta3) -sin(theta3); sin(theta3) cos(theta3)];
    rgklp3 = [plansx3(iff+1,1);plansy3(iff+1,1)]+R3*gklp;
    fill(rgklp3(1,:),rgklp3(2,:),'g');
    drawnow
    F = getframe(gcf); 
    writeVideo(vidfile,F);
end
close(vidfile)