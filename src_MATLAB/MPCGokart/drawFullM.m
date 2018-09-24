function drawFullM(M)
    %code by mh
    %plot M as from loadinternalstates
    figure;
    
    subplot(1,3,1)
    hold on
    plot(ttpos(:,2),ttpos(:,3), 'r')
    plot(out.STATES_SAMPLED(:,2), out.STATES_SAMPLED(:,3), 'b')
    %stairs(refEXT(:,1), refEXT(:,3), 'b')
    hold off
    daspect([1 1 1])
    title('reference trajectory vs actual');

    subplot(1,3,2)
    hold on
    plot(out.STATES_SAMPLED(:,1), out.STATES_SAMPLED(:,6), 'r')
    stairs(out.CONTROLS(:,1), out.CONTROLS(:,3), 'b')
    hold off
    title('steering input');

    subplot(1,3,3)
    stairs(out.CONTROLS(:,1), out.CONTROLS(:,2), 'r')
    title('Acceleration');


end