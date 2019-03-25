mat = zeros(12,12);
for i = 1:10
    b = simple2bas(i,12);
   toadd = b'*b;
    mat = mat+toadd;
end