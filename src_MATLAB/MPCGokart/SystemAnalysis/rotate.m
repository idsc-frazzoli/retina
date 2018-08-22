function xr = rotate(x,o)
    Rot = @(theta)[cos(theta),sin(theta);-sin(theta),cos(theta)];
    n = numel(o);
    xr = zeros(2,n);
    for i=1:n
        xr(:,i)=Rot(o(i))*xr(:,i);
    end
end

