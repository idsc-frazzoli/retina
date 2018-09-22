function nD = deleteJumpData(D, dist, threshold)
    [n,~]=size(D);
    nD = zeros(n,4);
    ncount = 1;
    for i = dist+1:n-dist
        if(abs(D(i-dist,3)-D(i+dist,3))<threshold)
            nD(ncount,:) = D(i,:);
            ncount = ncount + 1;
        end
    end
    nD = nD(1:ncount,:);
end

