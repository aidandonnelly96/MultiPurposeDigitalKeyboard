package synth;


//"Differentiated Parabolic Wave" technique?
public class DPW {
	private double sqPrev; //for sawtooth wave
	private double sawPhase; //for sawtooth wave
	private double sqPrev1, sqPrev2; //for square/triangle wave
	private double sawPhase1,sawPhase2;
	private double IntegInputprev; //for triangle wave
	
	public void init() {
	sqPrev=1; //for sawtooth wave
	sawPhase=0; //for sawtooth wave
	sqPrev1=1; sqPrev2=0; //for square/triangle wave
	sawPhase1=0;sawPhase2=0.5;
	IntegInputprev=0; //for triangle wave
	}
	
	private void update(double sqP, double swP, double sqP1,double sqP2,double sP1, double sP2,double IIP) {
		 sqPrev=sqP; //for sawtooth wave
		 sawPhase=swP; //for sawtooth wave
		 sqPrev1=sqP1;
		 sqPrev2=sqP2; //for square/triangle wave
		 sawPhase1=sP1;
		 sawPhase2=sP2;
		 IntegInputprev=IIP; //for triangle wave
		}
	
	public double GenOsc(double pitch, int sampleIndex, double SR, String type ) {
			
    double output = 0;
	double RAD=2*Math.PI;
	double conv;
	

	//sawtooth algorithm

	double delta=0;
	double z1;
	double c;
	double bphase;
	double sq;
	double dsq;

	//square/triangle algorithm
	double z2;
	double bphase1,bphase2;
	double sq1,sq2;
	double dsq1,dsq2;
	double IntegInput=0;
	double ampCorrection;
	double TriAdj;

	


	c=SR/(4*pitch*(1-pitch/SR));

	if (type.equals("sine")) {
	output=Math.sin(RAD*sampleIndex*pitch/SR);
	}
	else if (type.equals("sawtooth")) {

	delta=pitch/SR;
	sawPhase=(sawPhase+delta)%1;
	bphase=2*sawPhase-1;
	//sq=Math.pow(bphase, 2);
	sq=bphase*bphase;
	z1=sqPrev;
	dsq=sq-z1;
	sqPrev=sq;
	output=c*dsq;
	update(sqPrev, sawPhase,0,0,0,0,0);
	}
	else if (type.equals("square") || type.equals("triangle")) {
		delta=pitch/SR;
		sawPhase1=(sawPhase1+delta)%1; 	sawPhase2=(sawPhase2+delta)%1;
		bphase1=2*sawPhase1-1; 	bphase2=2*sawPhase2-1;
		sq1=bphase1*bphase1;; sq2=bphase2*bphase2;
		z1=sqPrev1;z2=sqPrev2; 
		dsq1=sq1-z1; dsq2=sq2-z2;
		sqPrev1=sq1;sqPrev2=sq2;
		output=c*(dsq1-dsq2);
		output=output*(1/(1+(1.25*(pitch/SR)))); //this is a cheaper solution
		update(0, 0,sqPrev1,sqPrev2,sawPhase1,sawPhase2,0);
		if (type.equals("triangle")) {
		IntegInput=output;
		IntegInput=IntegInput+IntegInputprev;
		IntegInputprev=IntegInput;
		
		TriAdj=0.25*SR/pitch;
		output=(IntegInput+TriAdj)/TriAdj;
		update(0, 0,sqPrev1,sqPrev2,sawPhase1,sawPhase2,IntegInputprev);
		
		}
		}
	else output=0;

		return output;
	}
}
