%code by mheim
function [p,steps,speed, ttpos] = getTrajectory(points, order, maxacc, timestep)
    [np,~]=size(points);
    step = 0.02;
    u = 0:step:np;
    u = u(:,1:end-1)';
    [nu,~]=size(u);
    s = 0.01;
    show = 1;
    p = cartbspline(points, u, order, 0);
    v = cartbspline(points, u, order, 1);
    a = cartbspline(points, u, order, 2);
    %normalized acceleration
    vnn = vecnorm(v')';
    vn = v./vnn;
    an = a./(vnn);
    dd = dot(an',vn')';
    an = an-([dd,dd].*vn);
    ann = vecnorm(an')';
    
    %curvature pass
    vmax = maxacc*ones(nu,1)./ann;
    %vmax(1)=0.1;
    %backwards pass
    dist = 0;
    i = nu;
    while dist<np*2
        %local forward acceleration
        next = i+1;
        if(next>nu)
            next = 1;
        end
        la = sqrt(maxacc.^2-(ann(next)*vmax(next)).^2);
        %speed gained in step
        d = step*vnn(next);
        sg = la*d/vmax(next);
        vmax(i)=min(vmax(i),vmax(next)+sg);
        i = i-1;
        if(i == 0)
            i = nu;
        end
        dist = dist + step;
    end
    


    %forward pass
    dist = 0;
    i = 1;
    while dist<np*2
        %local forward acceleration
        last = i-1;
        if(last==0)
            last = nu;
        end
        la = sqrt(maxacc.^2-(ann(last)*vmax(last)).^2);
        %speed gained in step
        d = step*vnn(last);
        sg = la*d/vmax(last);
        vmax(i)=min(vmax(i),vmax(last)+sg);
        i = i+1;
        if(i > nu)
            i = 1;
        end
        dist = dist + step;
    end

    totalMaxSpeed = max(vmax);
    steps = step*vnn;
    speed = vn.*vmax;

    if(show)
        figure
        %plot(p(:,1),p(:,2))
        daspect([1 1 1])
        hold on
        for i=1:nu
           x = [p(i,1),p(i,1)+an(i,1)*s];
           y = [p(i,2),p(i,2)+an(i,2)*s];
           line(x,y,'Color','blue');
        end

        for i=1:nu
            next = i+1;
            if(next>nu)
                next = 1;
            end
            x = [p(i,1),p(next,1)];
           y = [p(i,2),p(next,2)];
           vc = vmax(i)/totalMaxSpeed;
           line(x,y,'Color',[1-vc,vc,0]);
        end
        scatter(points(:,1),points(:,2));
        hold off
    end
    
    ttpos = [];
    currentt = 0;
    currentu = 0;
    %slow method but i don't know the size beforehand
    while currentu<np
        ttpos = [ttpos;[currentt,cartbspline(points, currentu, order, 0)]];
        %localStep = step*norm(cartbspline(points, currentu, order, 1));
        %localSpeed = interp1([vmax(end);vmax],0:nu,currentu);
        localStep = norm(cartbspline(points, currentu, order, 1));
        localSpeed = vmax(max(1,ceil(currentu/step)));
        currentu = currentu + timestep*localSpeed/localStep;
        currentt = currentt+timestep;
    end
end