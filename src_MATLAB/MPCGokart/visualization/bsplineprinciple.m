close all
clear all

points = [36.2,52,58.2,52,51,46,41.8;44.933,58.2,54.8,46,41,40,38.33;1.7,1.2,2.3,0.5,0.6,0.5,1.8]';
points = [points(3:end,:);points(1:2,:)]
mpoints = mean(points(:,1:2));
[m,n]=size(points);
startpoints = [(0:m+1)',zeros(m+2,1)];
points = [points(:,1:2)-ones(m,1)*mpoints,points(:,3)];
points = points(m:-1:1,:);

mps = 0.1;
mpsx = 3;
mpsXX = -mpsx:0.01:mpsx;

vidfile = VideoWriter('testmovie','Motion JPEG AVI');
open(vidfile);
frames = 1000;
speed = 0.025;
set(gcf,'position',[100,100,1000,800])
for ii = 1:frames
    %show track spline
    figure(1)
    clf
    hold on
    set(gca,'visible','off')
    daspect([1 1 1])
    
        %plot circles
    x = ii*speed
    
    %precompute mini plots
    fullMP = [];
    for iimp=-mpsx:0.01:mpsx
        fullMP(:,end+1)=casadiDynamicBasis(iimp+x,points);
    end    
    
    b = casadiDynamicBasis(x,points);
    pos = b'*points;
    
    
    [leftline,middleline,rightline, wl, llc,rlc] = drawTrack(points(:,1:2),points(:,3),1,startpoints);
    %plot(leftline(:,1),leftline(:,2),'b')
    %plot(rightline(:,1),rightline(:,2),'b')
    plot(middleline(:,1),middleline(:,2),'--r','LineWidth',3)
    plot(points(:,1),points(:,2)+b, 'ks','MarkerSize',8);
    plot([points(:,1),points(:,1)]',[points(:,2),points(:,2)+b]', '-b','LineWidth',2);
    plot([points(:,1);points(1,1)],[points(:,2);points(1,2)], '-k')
    %plot([wl(:,1)'; wl(:,3)'], [wl(:,2)'; wl(:,4)'],'-ks') 
    %plot(llc(:,1),llc(:,2),'-k');
    %plot(rlc(:,1),rlc(:,2),'-k');




    for i=1:m
        r = b(i);
        x = points(i,1);
        y = points(i,2);
        d = r*2;
        px = x-r;
        py = y-r;
        %h = rectangle('Position',[px py d d],'Curvature',[1,1],'FaceColor',[0 0 0]);
        X = [pos(1),x];
        Y= [pos(2),y];
        if(r>0)
            plot(X,Y,'k','LineWidth',r*10);
            YY = fullMP(i,:);
            mask = YY>0;
            YY = YY(mask);
            XX = mpsXX(mask);
            plot(x+XX,y+YY, 'b','LineWidth',2)
        end
        

        
        
    end
    r = 0.3;
    x = pos(1);
    y = pos(2);
    d = r*2;
    px = x-r;
    py = y-r;
    h = rectangle('Position',[px py d d],'Curvature',[1,1],'FaceColor',[1 1 1]);

    hold off
       drawnow
    F = getframe(gcf); 
    writeVideo(vidfile,F);
end
close(vidfile)