%code by mh
%points = [-1,1,2,0,0,0,1,0,0,0]';
%points = ones(5,1)
points = [1,2,2,4,2,2,1;0,0,4.7,5,5.3,10,10]';
[np,~]=size(points);
step = 0.02;
u = 0:step:np;
u = u(:,1:end-1)';
[nu,~]=size(u);
o = 2
s = 0.01;
p = cartbspline(points, u, o, 0);
v = cartbspline(points, u, o, 1);
a = cartbspline(points, u, o, 2);
%normalized acceleration
vn = vecnorm(v')';
vnn = v./vn;
an = a./(vn);
dd = dot(an',vnn')';
an = an-([dd,dd].*vnn);
ann = vecnorm(an')';
%
figure
%plot(p(:,1),p(:,2))
daspect([1 1 1])
hold on
[m,n]=size(an);

for i=1:m
   x = [p(i,1),p(i,1)+an(i,1)*s];
   y = [p(i,2),p(i,2)+an(i,2)*s];
   line(x,y,'Color','blue');
end
maxacc = 1;
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
    d = step*vn(next);
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
    d = step*vn(last);
    sg = la*d/vmax(last);
    vmax(i)=min(vmax(i),vmax(last)+sg);
    i = i+1;
    if(i > nu)
        i = 1;
    end
    dist = dist + step;
end

totalMaxSpeed = max(vmax);

for i=1:m
    next = i+1;
    if(next>m)
        next = 1;
    end
    x = [p(i,1),p(next,1)];
   y = [p(i,2),p(next,2)];
   vc = vmax(i)/totalMaxSpeed;
   line(x,y,'Color',[1-vc,vc,0]);
end
hold off

