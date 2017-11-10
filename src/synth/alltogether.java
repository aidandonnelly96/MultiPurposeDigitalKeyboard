package synth;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;


public class alltogether  implements KeyListener{
	private static double SR=44100;
	private boolean onoff;
	double Res = 0.5;
	
	public alltogether(){
	
	}
	
	public void setRes(double res){
		Res=res;
	}
	
	public void toggleStatus(boolean st) {
		onoff=st;
		
		if(onoff) {}
			//startSynth(60);
	}

	public void allSynth(double osc1Midi, int osc2Octave, double osc2Detune, double mix, double Amp, double LFOrate, double LFOdepth, String LFOtype, double AmpEnvAttack, double AmpEnvDecay, double AmpEnvSustainLevel, double AmpEnvSustainTime,double AmpEnvRelease, double FilterCutoff, double Res, double FilterEnvDepth, double FilterEnvAttack, double FilterEnvDecay, double FilterEnvSustainLevel, double FilterEnvSustainTime, double FilterEnvRelease){
		
		System.out.println("running");
		int sampleIndex;
		int TimeSeconds=1;
		int TotalTime=(int) (TimeSeconds*(int) SR); //this is the length of the sound in samples
		double[] outputFile=new double [TotalTime]; //create an array for the output to make a wav file
			
		double oscil1, oscil2;
		double osc2MidiAdj;
		double osc1Hz, osc2Hz;
		double lfowave, lfopitch;
		
		double FilterEnv, FilterInput, FilterOutput;
		double AmpEnv;
		double output;
		
		Oscillators Osc1=new Oscillators();
		DPW Osc2=new DPW(); //use the DPW oscillator class for the second oscillator
		//LFO lfoWave=new LFO();
		double lfoWave;
		
		ADSR adsrFilt=new ADSR();
		ADSR adsrAmp=new ADSR();
		Filter filt=new Filter();
		
		
		for (sampleIndex=0;sampleIndex<TotalTime;sampleIndex++) {	
				//if(!onoff) sampleIndex=TotalTime;
				osc1Hz=midi2hz(osc1Midi);
				osc2MidiAdj=osc1Midi+12*osc2Octave; //adjust the midi note value depending on the octave
				//System.out.println(" os2mid "+osc2MidiAdj);
				//just in case the incorrect value is specified
				if (osc2MidiAdj>128) osc2MidiAdj=osc1Midi;
				else if (osc2MidiAdj<0) osc2MidiAdj=osc1Midi;
	
				//generate oscillator1 
				
				oscil1=Osc1.geoSaw(osc1Hz,SR,sampleIndex);
							
						
				//oscillator2
				osc2Hz=midi2hz(osc2MidiAdj);
				//LFO, below audible range but manipulate other parameters at regular, repeating rates.
				//common is creating vibrato by modulating the frequency of a sine oscillator
				//adjusting a filter’s cutoff over time
				//LFOdepth determines the excursion of the LFO wave (e.g., -7.5 to +7.5)
				lfowave=LFO.makelfo(SR,LFOrate,LFOdepth,LFOtype,sampleIndex);
				lfopitch=(osc2Hz+osc2Detune)+lfowave; //vary the pitch using an LFO
				oscil2=Osc2.GenOsc(lfopitch, sampleIndex,SR,LFOtype);
				//System.out.println("lfowave "+ lfowave+ " lfoP "+lfopitch);
			
		
				//Amplifier envelope, filter envelope adn then filtering of the oscillator outputs
				AmpEnv=adsrAmp.envGenNew(sampleIndex,AmpEnvAttack, AmpEnvDecay, AmpEnvSustainTime, AmpEnvSustainLevel, AmpEnvRelease, SR);	
				FilterEnv=adsrFilt.envGenNew(sampleIndex,FilterEnvAttack, FilterEnvDecay, FilterEnvSustainTime,FilterEnvSustainLevel,  FilterEnvRelease, SR);
				FilterEnv=FilterCutoff+(FilterEnvDepth-FilterCutoff)*FilterEnv;
				FilterInput=mix*oscil1+(1-mix)*oscil2;
				
				//uncomment the particular line below to get the filter you want to try 
				//
				FilterOutput=filt.SallenKeyfiltering(FilterInput, FilterEnv, Res, SR);
				//FilterOutput=filt.StateVariablefiltering(FilterInput, FilterEnv, Res, SR);
				
				output=FilterOutput;
				//output=(Amp/100)*AmpEnv*FilterOutput; //Amp is just used to scale the output so it is between 1 and -1	
				outputFile[sampleIndex]=output;
		
				//
				//StdAudio.play(oscil1);
				//System.out.println(output);
			    	StdAudio.play(output);	
			}
		StdAudio.save("testOutput7.wav",outputFile);
	}
	
	
	public static void EnvTest(double AmpEnvAttack, double AmpEnvDecay, double AmpEnvSustainTime, double AmpEnvSustainLevel, double AmpEnvRelease){
		//This method was written to test the output of the ADSR class to ensure it was correct
			int sampleIndex;
			int TotalTime=(int) (1*(int) SR);
			double AmpEnv;
			double[] EnvArray=new double[TotalTime];
			ADSR adsrAmp=new ADSR();
			for (sampleIndex=0;sampleIndex<TotalTime;sampleIndex++) {
				AmpEnv=adsrAmp.envGenNew(sampleIndex,AmpEnvAttack, AmpEnvDecay, AmpEnvSustainTime, AmpEnvSustainLevel, AmpEnvRelease, SR);
				EnvArray[sampleIndex]=AmpEnv;
				//System.out.println(AmpEnv);					
				}
			
			PrintStream out;
			try {
				out = new PrintStream(new FileOutputStream("EnvArray.txt"));
				for (int index=0;index<EnvArray.length;index++){
				out.printf("%f", EnvArray[index]);
			    out.println();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*	Test code for this method
			    double AmpEnvAttack=0.25; 
				double AmpEnvDecay=0.6; 
				double AmpEnvSustainTime=0.8;
				double AmpEnvSustainLevel=0.4; 
				double AmpEnvRelease=1;
				*/
				//EnvTest(AmpEnvAttack,AmpEnvDecay,AmpEnvSustainTime,AmpEnvSustainLevel,AmpEnvRelease);	
		}

		
	public  double midi2hz(double midi) {
		double hertz;
		double conv;
		
		conv=(double) (midi-69)/12;
		hertz=440*(Math.pow(2,conv));
		return hertz;
	}

	
	public static void main(String args[]) throws Exception{
		alltogether al=new alltogether();
		al.toggleStatus(true);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("typed");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("pressed");
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("released");
		
	}
	
	public void startSynth(double osc1, double A, double D, double S, double R, String wave) {
		System.out.println("***********Starting*********");
		double osc1Midi=osc1; //the midi note value for the pitch of Osc1
		int osc2Octave=0;   //octave below
		double osc2Detune=1.2; //in terms of hertz 
		double mix=0.5; //this is a value from 0 to 1 to balance between Osc1 and Osc 2
		double Amp=10; //this should be between 0 and 100% 
		double LFOrate=2.5; //this is in hertz, typically betwen 0-20Hz
		double LFOdepth=7.5; //this is in terms of hertz 
		String LFOtype=wave; 
		double AmpEnvAttack=A; //in seconds 
		double AmpEnvDecay=D; //in seconds
		double AmpEnvSustainTime=S; //in seconds
		double AmpEnvSustainLevel=0.6; 
		double AmpEnvRelease=R; //in seconds
		double FilterCutoff=20; //this is in hertz 
		double FilterEnvDepth=6000; // in hertz - adjusts cutoff
		Res=0.03;
		double FilterEnvAttack=A; //in seconds
		double FilterEnvDecay=D; //in seconds
		double FilterEnvSustainTime=S;//in seconds
		double FilterEnvSustainLevel=0.5; 
		double FilterEnvRelease=R; //in seconds
		
		allSynth(osc1Midi,osc2Octave, osc2Detune,mix,Amp,LFOrate,LFOdepth,LFOtype,AmpEnvAttack,AmpEnvDecay,AmpEnvSustainLevel,AmpEnvSustainTime,AmpEnvRelease,FilterCutoff,Res, FilterEnvDepth,FilterEnvAttack, FilterEnvDecay,FilterEnvSustainLevel,FilterEnvSustainTime,FilterEnvRelease);
	}
}
