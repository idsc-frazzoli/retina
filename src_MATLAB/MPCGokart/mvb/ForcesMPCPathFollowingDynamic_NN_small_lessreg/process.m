points = getPoints('/thesistrackid.csv');
points(:,3)=points(:,3)+0.4;
visualizeRealLap('/retina_out/kinematic.lcm/', 'kinematic', points,1)
visualizeRealLap('/retina_out/dynamic.lcm/', 'dynamic', points,1)
visualizeRealLap('/retina_out/human.lcm/', 'human', points,1)
visualizeRealLap('/retina_out/center.lcm/', 'center', points,1)
