package synth;

public class SawSqrTri {

public static double fhz=440; //fhz = frequency hz = pitch of sinusoid
public static double Fs=44100; //Fs = sampling frequency
public static int tlen=44100; //tlen=length in samples for one second of sound
	

//generate a sawtooth using mathematical formula
//the result is returned in the array 'wave'
public static double[] sawtooth(double fhz, double Fs){
	double[] wave =new double[tlen];
	
	for (int n=0;n<tlen;n++){
		wave[n]=2*((n*fhz/Fs)%1)-1;
	}
	return wave;	
}


//generate a sawtooth using additive synthesis
//the result is returned in the array 'wave'
//the input parameters are the pitch (fhz) and the sampling frequency or sampling rate
//this method uses many of the methods following it sinWave, addArray, scale,
public static double[] addSaw(double fhz, double Fs){
double [] wave=new double [tlen];
double Amp;
double[] harmonicScale=new double [tlen];


double pi=Math.PI;  //store the value for PI in variable pi
//we compute a square wave by adding sine waves(sinusoids)
//compute the maximum number of sinusoids possible
//given a fundamental frequency (fhz) and a sampling frequency of Fs
//the highest harmonic/sinusoid before aliasing is fhz/2.
int K=(int) Math.floor(Fs*0.5/fhz);  //K=number of harmonics/sinusoids possible to avoid aliasing
System.out.println("K freq is" + K);
double [] harmonic=new double [tlen];

for (int k=1;k<K;k++){
	//compute the amplitude for each sinusoid based on harmonic number k
	Amp=(double) Math.pow(k, -1); //Amp=(1/(double)k);
	//Amp=1.5*Amp/pi;
	Amp=Amp/pi;
	System.out.println(""+Amp);
	//create a sinewave of frequency k*fhz for 1 second
	harmonic=sinWave(k*fhz, Fs, 1);
	//scale(multiply) the harmonic by the Amplitude
	harmonicScale=scale(harmonic,Amp);

	//build the sawtooth by adding this harmonic to wave
	//at each cycle of the for loop one more harmonic is added to wave
	wave=addArray(wave,harmonicScale);
	//wave=addArray(wave,harmonic);
	}

return wave;	
}

//method to generate a sinewave
public static double[] sinWave(double freqHz, double SampFreq, double timeDurSecs){
	
	int timeSamps=(int) (SampFreq*timeDurSecs);
	double[] wave=new double [timeSamps];
	double pi=Math.PI;
	double SampPeriod=(double) 1.0/SampFreq;
	for (int index=0; index<timeSamps; index++)
		wave[index]=Math.sin((double) 2*pi*index*freqHz*SampPeriod);
	
	return wave;
}

//method to add two arrays together
//the result array is returned in C
public static double[] addArray(double[] A,double[] B){
	int Alen=A.length;
	double [] C=new double [Alen];
	
	for (int index=0;index<Alen;index++) {
		C[index]=A[index]+B[index];	
	}
	return C;
}

//scales the amplitude of one sinusoid/harmonic
//multiplies each array element by Amp
public static double[] scale(double[] harmonic,double Amp){
	int harmonicLen=harmonic.length;
	double[] ScaledHarmonic=new double [harmonicLen];
		
	for (int index=0;index<harmonicLen;index++) {
		ScaledHarmonic[index]=Amp*harmonic[index];	
	}
	return ScaledHarmonic;
}


//this method scales the amplitude of a wave to be between +1 and -1 only
public static double [] wavScale(double[] inwave) {

	double[] output=new double[(int) inwave.length];
	double maxAmp=0;
	
	for(int i=0;i<inwave.length;i++){
		if(Math.abs(inwave[i])>maxAmp)
			maxAmp=Math.abs(inwave[i]);
	}
	for(int i=0;i<inwave.length;i++){
		output[i]=inwave[i]/(maxAmp+0.5);
	}
	
	
//	System.out.println("max is" + maxAmp);
	//System.out.println("len is" + inwave.length);
	return output;
}



public static double[] addTri(double fhz, double Fs){
double [] wave=new double [tlen];
double Amp;
double[] harmonicScale=new double [tlen];
double[] triScale=new double [tlen];


double pi=Math.PI;  //store the value for PI in variable pi
//we compute a square wave by adding sine waves(sinusoids)
//compute the maximum number of sinusoids possible
//given a fundamental frequency (fhz) and a sampling frequency of Fs
//the highest harmonic/sinusoid before aliasing is fhz/2.
int K=(int) Math.floor(Fs*0.5/fhz);  //K=number of harmonics/sinusoids possible to avoid aliasing
System.out.println("K freq is" + K);
double [] harmonic=new double [tlen];

for (int k=1;k<K;k++){
	//compute the amplitude for each sinusoid based on harmonic number k
	Amp=(double) Math.pow(k, -1); //Amp=(1/(double)k);
	//Amp=1.5*Amp/pi;
	Amp=Amp/pi;
	//create a sinewave of frequency k*fhz
	harmonic=sinWave(k*fhz, Fs, 1);
	//scale(multiply) the harmonic by the Amplitude
	harmonicScale=scale(harmonic,Amp);
	

	//build the sawtooth by adding this harmonic to wave
	//at each cycle of the for loop one more harmonic is added to wave
	wave=addArray(wave,harmonicScale);
	//System.out.println("wlen is" + wave.length);
	triScale=wavScale(wave);
	}

for(int t=0;t<wave.length;t++)
	if( triScale[t] >= 0.0 ){
		triScale[t]= 1.0-triScale[t];
	} else triScale[t]=1.0+triScale[t];

		//wave[t] = 1.0 - ( 2.0 * wave[t] );
//	else wave[t] = 1.0 + ( 2.0 * wave[t] );

for(int t=0;t<400;t++)
	System.out.println(""+triScale[t]);
return triScale;	
}

public static double[] tsaw(double freq, double Fs){
	double [] wave=new double [tlen];
	double[] harmonicScale=new double [tlen];
	double pi=Math.PI;
	
	double samp = 0.0,ph=0.0;
    int k;
    int K=(int) Math.floor(Fs*0.5/freq);
    
    for(int i = 0; i < tlen; i++){
        double s, sw = 0.0;
        for(k=1; k<K; k++) {
            samp = Math.pow((double) k, -1.0);
            samp=samp/(pi);
            s = samp * Math.sin((double) k * ph);
            sw = sw+s;
        }
        wave[i] = 0.5*sw;
        ph += 2*pi * freq / Fs;
//        samples[i] = (short) (amp*2.0*((i*freq/samplerate)%1)-1);
//          samples[i]=(short)(amp*2*((((i*freq)/samplerate) %1))-1);
    }
    return wave;
}
public static void main(String args[]){

//Declare double arrays to hold the output of the SawSqrTri class methods
double[] sawWave=new double[(int) Fs];
double[] scaleSaw=new double[(int) Fs];
double[] filtWave=new double[(int) Fs];
double[] scaleWave=new double[(int) Fs];
double[] env=new double[(int) Fs];
double[] envWave=new double[(int) Fs];
//declare an array of weights for IIR filtering

double[] weights={1.9802,-0.9999}; 
//double[] weights={1.99,-0.99}; 
//double[] weights={0.0001,-0.9999};  
//double[] weights={0.9999,-0.0001};




//create a sawtooth wave
sawWave=tsaw(fhz, Fs);

//Filter the unscaled sawtooth
//Set up an object of class Filters using the Filters constructor(method)
//////Filters mywave=new Filters(sawWave);
//use an IIR filter with weights
//here we use the object name (mywave) to call the method filterIIR in the Filters class
/////mywave.filterIIR(weights);
//get the filtered sawtooth result
//again we use the object name (mywave) to call the method filterIIR in the Filters class
/////filtWave=mywave.getFiltered();
//call the method in this class (SawSqrTri) above to scale the output filtered wave to be
//between +1 and -1
///scaleWave=wavScale(filtWave);


scaleWave=wavScale(sawWave);

//place an envelope on the filtered wave to shape the attack, decay, sustain and release sections
//specify where each section of the envelope will end
//the parameters are attack time, decay time, sustainTime, sustainLevel, release time
//note that each parameter is later in time than the next with the release ending at the wave end (1=full duration)
/////EnvAdsr.envGenNew(0.25, 0.6, 0.8, 0.5, 1,Fs);
/////env = EnvAdsr.output;

/////envWave=mywave.multArray(scaleWave, env);


//Create a string name for the output wav file and save it (to the current project folder)
String filename="tomTSaw.wav";
StdAudio.save(filename,scaleWave);
//play the filtered sawtooth
//StdAudio.play(scaleWave);
//StdAudio.play(envWave);

StdAudio.play(scaleWave);
}
	
}

