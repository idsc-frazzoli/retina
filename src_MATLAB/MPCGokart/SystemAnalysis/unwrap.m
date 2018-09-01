%code by mheim
% TODO is this related or consistent with
% https://ch.mathworks.com/help/matlab/ref/unwrap.html
function nx = unwrap(x, d)
%unwrap data (for example angles that wrap aroun +/-180°
%d is the unwrapping distance eg.: 360°
offset = 0;
n = numel(x)
nx = zeros(n,1);
for i = 2:n
    if x(i)-x(i-1)>d/2
        offset = offset -d;
    elseif x(i)-x(i-1)<-d/2
        offset = offset +d;
    end
    nx(i)=x(i)+offset;
end

