%helper code/only needed if MATLAB is older than 2017
% https://github.com/lemonzi/matlab/blob/master/custom/vecnorm.m
function n = vecnorm(x, dim)
% VECNORM(x, dim)
% Norm of a vector
% If x is matrix, gets norm of every row or column

    if nargin < 2
        dim = 1;
    end

    n = sqrt(sum(x.^2, dim));

end
