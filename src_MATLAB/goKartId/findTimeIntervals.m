function ids = findTimeIntervals(sigIn, timeBounds,time)
sigIn = sigIn - sigIn(1);
N = length(timeBounds);
ids = [];
absSig = abs(sigIn);
for i =1:2:N-1
   
    idStart = find(time > timeBounds(i),1, 'first');
    
    k = idStart;
    maxIndex = k;
    while(time(k) < timeBounds(i+1))
        if (absSig(k) > absSig(maxIndex))
            maxIndex = k;
        end
        k = k+1;
    end
    ids = [ids; idStart; maxIndex];

end




end