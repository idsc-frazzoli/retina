function xr = rotate(x,o)
    [m,~] = size(x);
    if(m ~= 2)
        x = x';
    end
    Rot = @(theta)[cos(theta),sin(theta);-sin(theta),cos(theta)];
    n = numel(o);
    xr = zeros(2,n);
    for i=1:n
        xr(:,i)=Rot(o(i))*x(:,i);
    end
    if(m ~= 2)
        xr = xr';
    end
end

