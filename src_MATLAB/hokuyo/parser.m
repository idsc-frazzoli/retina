
function numbers = parser(string)
% get the number from the string

N=length(string);
currentNum = 0;
numbers = [];
digits = [];
for i=1:N
    
    if (isdigit(string(i)) >= 0)
        %current character is digit
        digits = [digits; isdigit(string(i))]; 
        continue;
    else
        if (isempty(digits))
            %if we haven't started processing a number
            continue;
        end
    end
    digits = flip(digits);
    for k=length(digits):-1:1   %get a number from digits
        currentNum = currentNum + digits(k)*10^(k-1);
    end
    numbers = [numbers; currentNum];    % add it to the list of numbers in string
    currentNum=0;
    digits = [];
end


end
