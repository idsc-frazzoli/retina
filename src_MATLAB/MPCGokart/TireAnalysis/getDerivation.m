function dery = getDerivation(y, sigma, dt)
    sz = sigma*30;    % length of gaussFilter vector
    ls = linspace(-sz / 2, sz / 2, sz);
    gauss = exp(-ls .^ 2 / (2 * sigma ^ 2));
    gauss = gauss / sum (gauss); % normalize
    strobel = 0.5*[1 0 -1]/dt;
    derfilter = conv(strobel, gauss);
    dery = conv(y,derfilter,'same');
    n = numel(dery);
    X = 1:n;
    %figure
    %hold on
    %plot(X,y)
    %plot(X,dery)
    %legend('x','dotx')
    %hold off
end