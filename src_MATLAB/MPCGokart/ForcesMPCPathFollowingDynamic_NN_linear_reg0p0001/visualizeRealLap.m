function visualizeRealLap(folder, name, points, mode) 
    addpath('..') 
    addpath('../TireAnalysis') 
    addpath('../SystemAnalysis') 
    addpath('casadi') 
    close all
    userdir = getuserdir;
    folder=strcat(userdir,folder);
    SysID = loadSystIDData(folder);
    % [t,tms,vx,vy,vr,ax,ay,s,pl,pr,pal,par,vwx,px,py,po]
    beta = SysID(:,8);
    lcurr = SysID(:,9);
    rcurr = SysID(:,10);
    l1 = 1;
    l2 = 0.3;
    figure
    hold on
    %plot(ttpos(:,2),ttpos(:,3), 'Color', [0.8 0.8 0.8])
    %plot(history(:,5), history(:,6), 'b')
    %stairs(refEXT(:,1), refEXT(:,3), 'b')
    daspect([1 1 1])
    axis([0 40 0 20])
    xlabel('X [m]')
    ylabel('Y [m]')
    %legend('reference', 'MPC controlled')
    %plot acceleration and deceleration in colors
    
    img = imread(strcat(userdir,'/trackbg.png'));
    scale = 0.0198;
    width = 1920*scale;
    height = 1080*scale;
    image('CData',img,'XData',[0 width],'YData',[height 0])
    
    theta =-0.76; % to rotate 90 counterclockwise
    R = [cos(theta) -sin(theta); sin(theta) cos(theta)];
    offset = [-37.3,6.3]';
    t = SysID(:,1);
    
    if(1)
    %points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
        pos = (R*points(:,1:2)'+offset)';
      [leftline,middleline,rightline] = drawTrack(pos,points(:,3));
      plot(leftline(:,1),leftline(:,2),'b')
      plot(rightline(:,1),rightline(:,2),'b')
    end
    
    if(1)
        po = SysID(:,[14,15]);
        yv = SysID(:,4);
        xv = SysID(:,3);
        po = (R*po'+offset)';
        pd = SysID(:,16)+theta;
        doffset = 0.49*gokartforward(pd);
        forward = gokartforward(pd);
        p = doffset + po;
        spc = 0;
        spacing = numel(pd)/40;
        acc = SysID(:,6);
        yacc = SysID(:,7);
        acc = gaussfilter(acc,100);
        yacc = gaussfilter(yacc,50);
        maxacc = max(abs(acc));
        [nu,~]=size(p);
        for i=1:nu-1
            next = i+1;
            x = [p(i,1),p(next,1)];
            y = [p(i,2),p(next,2)];
            if(mode==1)
                vc = acc(i)/maxacc;
                line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]);
            end
        end
        print(strcat(name,'acc'),'-dpng','-r600')
        hold off
    end
    if(1)
         figure
        hold on
        %plot(ttpos(:,2),ttpos(:,3), 'Color', [0.8 0.8 0.8])
        %plot(history(:,5), history(:,6), 'b')
        %stairs(refEXT(:,1), refEXT(:,3), 'b')
        daspect([1 1 1])
        axis([0 40 0 20])
        xlabel('X [m]')
        ylabel('Y [m]')
        %legend('reference', 'MPC controlled')
        %plot acceleration and deceleration in colors
         pos = (R*points(:,1:2)'+offset)';
        [leftline,middleline,rightline] = drawTrack(pos,points(:,3));
        plot(leftline(:,1),leftline(:,2),'b')
        plot(rightline(:,1),rightline(:,2),'b')
        image('CData',img,'XData',[0 width],'YData',[height 0])
        for i=1:nu-1
            %draw angle
            spc = spc+1;
            if(spc>=spacing)
               spc = 1;
               back = p(i,:) - forward(i,:)*l2;
               front = p(i,:) + forward(i,:)*l1;
               plot([back(1),front(1)],[back(2),front(2)],'-k');
            end
        end
        print(strcat(name,'pos'),'-dpng','-r600')
        hold off
    end
    figure
    hold on
    ax = gca;
ax.XGrid = 'off';
ax.YGrid = 'on';
    axis([0 max(t) -13 13])
    pbaspect([3 1 1])
    plot(t,yacc);
    xlabel('t [s]')
    ylabel('lateral acceleration [m/s^2]')
    print(strcat(name,'yacc'),'-dpng','-r600')
    hold off
    figure
    hold on
        ax = gca;
ax.XGrid = 'off';
ax.YGrid = 'on';
    axis([0 max(t) -10 10])
    pbaspect([3 1 1])
    plot(t,yv);
    plot(t,xv);
    %legend('longitudinal velocity', 'lateral velocity');
    xlabel('t [s]')
    ylabel('velocity [m/s]')
    print(strcat(name,'vel'),'-dpng','-r600')
    hold off
    figure
    hold on
        ax = gca;
ax.XGrid = 'off';
ax.YGrid = 'on';
    axis([0 max(t) -2 2])
    pbaspect([3 1 1])
    %plot(t,beta);
    plot(t,lcurr);
    plot(t,rcurr);
    xlabel('t [s]')
    %ylabel('steering angle [SCE]')
    %yyaxis right
    axis([0 max(t) -2400 2400])
    ylabel('motor current [A]')
    %legend('left motor','right motor')
    print(strcat(name,'steer'),'-dpng','-r600')
    hold off
end