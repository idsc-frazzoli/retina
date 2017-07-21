function [num] = isdigit(x)

switch (x)
    case '0'
        num = 0;
    case '1'
        num = 1;
    case '2'
        num = 2;
    case '3'
        num = 3;
    case '4'
        num = 4;
    case '5'
        num = 5;
    case '6'
        num = 6;
    case '7'
        num = 7;
    case '8'
        num = 8;
    case '9'
        num = 9;
    otherwise
        num = -1; %nonsense, distance cannot be negative
end

end