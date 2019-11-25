function [ACCX,ACCY,ACCROTZ] = nomodel_1x32_tanh_reg0p0001(VELX,VELY,VELROTZ,BETA,AB,TV, param)    
    w1 = [-1.0171351 0.6019033 0.4375875 0.17629819 -0.097009644 0.069957025 -0.13545303 -0.6931611 0.44041663 0.68099564 -0.48499346 -0.27381003 1.0544215 -0.25299907 -0.40963656 0.48499465 -0.29994255 1.0419956 -0.2812264 0.5053379 -0.6485829 0.6599527 -0.19759986 0.022598162 0.27110985 0.8632353 -0.37629443 0.53265524 -0.21794137 -0.60051084 -0.93406624 0.728588;0.48017645 -0.17962407 0.09016776 0.033272296 0.28271914 0.66030985 -0.30380479 0.068692856 0.37241197 0.092334904 -0.9380832 -0.16931528 0.20854996 -0.61596704 -0.1155915 -0.10904598 -0.041482486 -0.033775564 -0.16381724 0.07151455 0.08718866 -0.31425056 0.074807 -0.45875493 0.095491044 0.20030005 0.9137466 0.23189862 0.11059167 -0.55414045 0.032693036 -0.28867432;0.43023604 0.038768817 0.20189726 -0.52567285 -1.0036012 -0.099873185 -0.30004048 0.18856883 0.47650525 0.011535568 0.013998328 1.3298454 -0.09940554 0.8157974 0.6078032 0.48880127 -0.17731477 0.23311052 -0.51191604 -0.4328627 -0.4082346 -0.73981464 -0.65810734 -0.4594389 0.11537144 0.52653205 -0.4666126 -0.018512657 0.3106346 0.14591892 0.20103905 -0.03684361;-0.7442862 0.82081074 0.2572271 -0.020434251 0.23146605 0.1805428 0.2155695 0.31191492 -1.6763831 0.66749585 0.65026015 -0.9694873 -0.11791239 -0.107332595 -0.60835826 0.15241346 0.24579561 -0.21953814 0.1424998 -0.12837966 -0.13064478 0.18848914 -0.527109 1.646078 0.27245757 -0.2414958 0.27997765 -0.57237375 -0.1651025 -0.092629924 -0.5986562 -0.42266595;0.073363684 -0.007625596 0.18086304 -0.25364375 -0.24436137 0.2852567 0.34210485 -0.055720914 -0.037064478 0.23851894 0.2915963 -0.124247834 -0.08795455 0.22124478 -0.14555116 -0.11773783 -0.7292317 -0.055135828 -0.15578003 0.112731956 -0.3309182 -0.1640085 -0.33118722 0.10145584 0.15317677 -0.33404773 -0.12206488 0.0025834886 -0.121527106 -0.36149913 0.056131415 0.015580775;0.041048594 -0.0072012465 0.17476523 -0.17406587 0.06300439 0.4894521 -0.01301303 0.025387304 0.08008958 -0.15376128 -0.12839152 0.06865863 -0.02646579 -0.050678108 -0.28988075 0.11294811 -0.23777677 0.15417136 0.009533572 -0.028608803 -0.05918721 0.09429261 -0.16632974 0.040367514 0.39068225 -0.15043475 0.14821029 0.006000652 0.25591657 0.099503204 -0.10074276 -0.2146499];
b1 = [0.85870713 0.68199414 0.83551556 -2.0627618 1.4816774 -0.0072659254 0.9785732 -0.9920529 -0.7926641 -1.3787036 0.48117083 0.49120262 -0.6650865 1.2433566 -0.79928625 -2.9539618 0.9248872 -0.65767896 1.1077513 -2.5901027 -0.51092446 -0.324367 0.36182365 -0.43290207 -1.4779382 -0.6780713 1.1981341 0.68987566 0.7552394 -0.012770118 -0.5818085 -1.72342];
w2 = [-0.070650935 -0.782271 -1.0725356;0.7554093 0.33774513 -1.3264282;-1.2003027 0.5297246 -0.5281397;-0.41639388 1.4436909 -1.5646359;0.099368565 -1.6992168 0.3785094;-0.00027209253 -0.3909056 0.73277193;0.7230429 -0.3075464 -1.1579726;1.3368548 0.27163762 -1.5371847;0.21775623 0.0438867 -1.2749815;-0.09658581 -0.06991209 1.1391115;-0.07090901 -0.5034973 0.5569753;-0.02350463 0.22139002 -1.5326483;-0.11774586 -1.3378584 -0.2921491;0.22590816 1.3244505 -0.93190455;-0.80184805 -0.24583498 1.0752597;-0.92637104 -3.051688 1.1926208;-0.57791984 0.19020721 0.5355189;0.15945335 0.62197 -0.5228313;-0.8114881 1.2549464 -0.37054887;-0.8362424 2.6348493 -0.36291724;-1.0933386 0.40248427 -0.7726283;-0.35991 0.36575165 1.2672485;-0.24905962 -0.4920592 0.7670607;-0.12926157 -0.10737515 1.4485288;-0.046184327 0.043573305 1.3891191;-0.5352894 -0.42510545 -0.72319084;0.11171982 0.10809734 1.0323185;1.3632399 0.23389329 1.2735952;-1.4709865 -0.302107 0.5668009;0.01957915 -0.35856077 0.47086757;1.033982 0.48705342 0.8489272;-0.35255054 -0.2682011 -1.2369807];
b2 = [-1.5725158 0.2655948 0.5249549];
means = [2.6855843572110367 0.0006739826858158322 -0.04802685383934488 -0.027302772888852182 0.06560439433747117 -0.0741416072412400];
stds = [1.9181710264256149 0.3073995994274385 0.5524821241332127 0.20117397363021836 0.837672127818587 0.634702899104761];

    input = [VELX,VELY,VELROTZ,BETA,AB,TV];
    normed_input = (input - means) ./ stds;

    h1 = tanh(normed_input * w1 + b1);
    disturbance = h1 * w2 + b2;
    
    ACCX = disturbance(1);
    ACCY = disturbance(2);
    ACCROTZ = disturbance(3);
end
