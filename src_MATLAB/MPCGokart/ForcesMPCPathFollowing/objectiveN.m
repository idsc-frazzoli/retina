function f = objectiveN(z,points,vmax)
    global index
    f = 10*objective(z,points,vmax)-1*z(index.s);
end