//note: not all values are necessarily known for every type of controller
struct State {
	float Ux;
	float Uy;
	float dotPsi;
	float X;
	float Y;
	float Psi;
	float w2L;
	float w2R;
	float s;
};

struct ControlAndState{
	//control: left power, right power, 
	float uL;
	float uR;
	//control: steering
	//send dotS as control input (use state value for actual control)
	float udotS;
	//control: braking
	float uB;
	//also send predicted states
	struct State state;
};
