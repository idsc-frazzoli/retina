function alpha = getAlpha(x,d,bw)
%code by mh
%all amounts in SI UNITS
%get measured angle from measured horizontal distance on an axis
%perpendicular to the gokart forward axis
%arguments:
%d: distance of the wheel hub to perpendicular measurement axis
%bw: offset of the projection from the wheel hub
%x: measured offset to projected
%measurement functi
mout = @(alpha)d*tan(alpha)+bw/cos(alpha)-bw;
%find alpha for given x
lsqfun = @(alpha)(mout(alpha)-x)^2;

alpha = fminsearch(lsqfun, 0);
show = 1;
if(show)
    close all
   figure
   %draw line that is projected on
   hold on
   X = [-d,d];
   Y = [-d,-d];
   plot(X,Y,'g');
   %draw crude wheel
   Xw = [-1,1]*sin(alpha)*bw*0.5;
   Yw = [1,-1]*cos(alpha)*bw*0.5;
   plot(Xw,Yw,'r');
   %simple line to projector
   Xl = [cos(alpha)*bw,0];
   Yl = [sin(alpha)*bw,0];
   plot(Xl,Yl,'b');
   %projection line
   Xp = [cos(alpha)*bw,x+bw];
   Yp = [sin(alpha)*bw,-d];
   plot(Xp,Yp,'b');
   %measurement distance
   Xm = [bw,x+bw];
   Ym = [-d+0.05,-d+0.05];
   plot(Xm,Ym,'--')
   text(mean(Xm),mean(Ym)+0.02, num2str(x))
   %projectiondistance
   pd = ((Xp(1)-Xp(2))^2+(Yp(1)-Yp(2))^2)^0.5;
   text(mean(Xp),mean(Yp), num2str(pd));
   %angle
   %text(0,0, num2str(angle));
   daspect([1 1 1])
end
end

