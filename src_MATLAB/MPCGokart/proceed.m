%code by mheim
function nt = proceed(t,prog,p,steps)
    %t: current position (depends on step length)
    %prog: wanted progress
    %p: approximated spline points
    %steps: step lenghts between approx. points
    %speed: wanted speed at points
    %----------------------------
    %lp: local point
    %nt: new position
    %ls: local speed
    [n,~]=size(p);
    lstep = interp1([steps(end);steps],0:n,t);
    nt = t + prog/lstep;
    %lp = interp1([p(end);p],0:n,nt);
    %ls = interp1([speed(end);speed],0:n,nt);
end

