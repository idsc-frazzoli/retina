function vars = estimateVar(dat)
    [m,n] = size(dat);
    if(m<n)
        dat = dat';
        [m,n] = size(dat);
    end
    hpdat = highpass(dat,0.1,1);
    vars = zeros(n,n);
    for i = 1:n
        pd=fitdist(hpdat(:,1),'Normal');
        vars(i,i)=pd.sigma;
    end
end