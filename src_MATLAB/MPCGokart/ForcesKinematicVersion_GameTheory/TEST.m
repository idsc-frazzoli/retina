global index
for ii=1:31
K1_v2(ii,1)= outputM(ii,index.ab)-casadiGetSmoothMaxAcc(outputM(ii,index.v));
K2_v8(ii,1)= outputM(ii,index.ab_k2)-casadiGetSmoothMaxAcc(outputM(ii,index.v_k2));
distance_X(ii,1)=(outputM(ii,index.x)-outputM(ii,index.x_k2));
distance_Y(ii,1)=(outputM(ii,index.y)-outputM(ii,index.y_k2));
squared_distance_array(ii,1) = sqrt(distance_X(ii,1).^2+distance_Y(ii,1).^2);
K3_v14(ii,1)=squared_distance_array(ii,1)-dist+outputM(ii,index.slack2);
end

% Path Progress rate Constraint (input)
outputM(:,index.ds)
outputM(:,index.ds_k2)

outputM(:,index.slack)

% Speed Constraint (state)
outputM(:,index.v)
outputM(:,index.v_k2)

% Steering Angle Constraint (input)
outputM(:,index.beta)
outputM(:,index.beta_k2)
% Path Progress Constraint (input)
outputM(:,index.s)
outputM(:,index.s_k2)

% Path Progress rate Constraint (input)
outputM(:,index.ds)
outputM(:,index.ds_k2)

outputM(:,index.slack2)