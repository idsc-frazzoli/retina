function [xhist,time] = euler(f, xstart, u, dt)
    %m: number of steps
    [m,~] = size(u);
    %n: number of state components
    n = numel(xstart);
    xhist = zeros(m+1,n);
    xhist(1,:)=xstart(:);
    time = zeros(m+1,1);
    for i = 1:m
        xhist(i+1,:)=xhist(i,:)+dt*f(xhist(i,:),u(i,:))';
        time(i+1) = time(i)+dt;
    end
end