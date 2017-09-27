function [retArray] = findZeroCrossings(sigIn)



eps  = 0.1; % minimal expected amplitude of the rectangular waveform
zeroTolerance = 0.1; %tolerance to how much the signal is different than zero;
%assumes that time starts from 0

indices = [];
N = length(sigIn);

xPrev = sigIn(1);

for i =2:N

    if (abs (xPrev - sigIn(i)) > eps )
       indices = [indices; i-1; i]; 
    end
    xPrev = sigIn(i);
    
end

retArray = [];

for i=1:length(indices)
    if (abs(sigIn(indices(i))) > zeroTolerance) %magic number
        retArray = [retArray; indices(i)];
    end
end

%retArray = indices;



end