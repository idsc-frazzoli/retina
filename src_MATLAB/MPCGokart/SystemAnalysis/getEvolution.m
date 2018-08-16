function F = getEvolution(x)
    %get state matrix
    %states (everything is in global frame)
    %[x,y,theta,dotx, doty, dottheta, dotdotx, dotdoty]
    F = zeros(8);
    F(1,4) = 1;
    F(2,5) = 1;
    F(3,6) = 1;
    F(4,7) = 1;
    F(5,8) = 1;
end