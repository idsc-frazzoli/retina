%code by mheim
function nx = gaussfilter(dx, sigma)
sz = sigma*30;    % length of gaussFilter vector
x = linspace(-sz / 2, sz / 2, sz);
gf = exp(-x .^ 2 / (2 * sigma ^ 2));
gf = gf / sum (gf); % normalize
nx = conv (dx, gf, 'same');
end

