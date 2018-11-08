function f = objectiveN(z,points,vmax)
    f = 10*objective(z,points,vmax)-1*z(9);
end