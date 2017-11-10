/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package synth;

import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.*;
import javax.accessibility.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import java.awt.*;
import java.awt.event.*;

import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
/* 
 * LayeredPaneDemo.java requires
 * images/dukeWaveRed.gif. 
 */
public class LayeredPaneDemo extends JPanel{
    private String[] layerStrings = { "Z", "S", "X", "D", "C", "F", "V", "G", "B", "H", "N", "J", "M", "K" };
    private String[] bLayerStrings = { "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[" };
    
    private static HashMap<Integer, Integer> Ascii2Midi = new HashMap<Integer, Integer>();
    private static HashMap<String, Integer> Key2Midi = new HashMap<String, Integer>();
    
    private JLabel whiteKey2Midi[]=new JLabel[14];
    private JLabel blackkKey2Midi[]=new JLabel[10];
    
    private static RotaryKnob A;
    private static RotaryKnob D;
    private static RotaryKnob S;
    private static RotaryKnob R;
    
    private static JSpinner mode;
    private static JSpinner bpm;
    private static JSpinner wave;
    private static JSpinner octave;
    
    private static JComboBox LFO1type;
    private static JComboBox LFO2type;
    
    private static JComboBox Osc1Type;
    private static JComboBox Osc2Type;
    
    protected static JCheckBox metronome;

    private JLayeredPane layeredPane;


    public LayeredPaneDemo()    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        initializeMaps();


        //Create and set up the layered pane.
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1350, 450));
        
        addKeyListener(new KeyListener() {
	
	    		public void keyTyped(KeyEvent e) {
	    			System.out.println("typed");
	    		}
	
	    		public void keyReleased(KeyEvent e) {
	    			System.out.println("released");
	    		}
	
	    		public void keyPressed(KeyEvent e) {
	    			System.out.println("pressed with k");
	    			alltogether al = new alltogether();
	    			int k = e.getKeyCode();
	    			al.startSynth(Ascii2Midi.get(e.getKeyCode())*(Integer.parseInt(octave.getValue().toString().substring(1, octave.getValue().toString().length()))+1), A.val, D.val, S.val, R.val, wave.getValue().toString());
	
	    		}
    		});

        //This is the origin of the first label added.
        Point origin = new Point(10, 20);
        Point bOrigin = new Point(75, 20);

        //Add several overlapping, colored labels to the layered pane
        //using absolute positioning/sizing.
        

        for (int i = 0; i < 14; i++) {
            JLabel label = createWhiteKey(layerStrings[i], origin);
            layeredPane.add(label);
            origin.x += 95;
        }
        for(int j=0; j<10; j++) {
        		if(j==2 || j==5 || j==7) {
        			bOrigin.x+=100;
        		}
		    		JLabel label  = createBlackKey(bLayerStrings[j], bOrigin);
		    		layeredPane.add(label);
		        layeredPane.moveToFront(label);
	    		bOrigin.x += 95;
	    }


        //Add control pane and layered pane to this JPanel.
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createControlPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(layeredPane);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */


    //Create and set up a colored label.
    private JLabel createWhiteKey(String text,
                                      Point origin) {
        JLabel label = new JLabel(text);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.white); //change to lightGray on click
        label.setForeground(Color.black);
        label.setBorder(BorderFactory.createLineBorder(Color.black));
        label.setBounds(origin.x, origin.y, 90, 400);
		label.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				label.setBackground(Color.LIGHT_GRAY);
				alltogether al = new alltogether();
	    			String sOctave=octave.getValue().toString().substring(1, octave.getValue().toString().length());
	    			int numOctave=Integer.parseInt(sOctave);
	    			int oscVal=0;
	    			if(numOctave==-1) {
	    				oscVal=Key2Midi.get(text);
	    			}
	    			else {
	    				oscVal=Key2Midi.get(text)+(12*(numOctave+1));
	    				if(oscVal>127) {
	    					oscVal=127;
	    				}
	    			}
	    			al.startSynth(oscVal, A.val, D.val, S.val, R.val, LFO1type.getSelectedItem().toString());}

			@Override
			public void mouseReleased(MouseEvent e) {
				//label.setBackground(Color.white);
			}

			@Override
			public void mouseEntered(MouseEvent e) {					
				label.setBackground(Color.LIGHT_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				label.setBackground(Color.white);
			}
			
		});
        return label;
    }
    private JLabel createBlackKey(String text,
            Point origin) {
			JLabel label = new JLabel(text);
			label.setVerticalAlignment(JLabel.BOTTOM);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setOpaque(true);
			label.setBackground(Color.black); //change to darkGray on click
			label.setForeground(Color.white);
			label.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					label.setBackground(Color.DARK_GRAY);
					alltogether al = new alltogether();
		    			String sOctave=octave.getValue().toString().substring(1, octave.getValue().toString().length());
		    			int numOctave=Integer.parseInt(sOctave);
		    			int oscVal=0;
		    			if(numOctave==-1) {
		    				oscVal=Key2Midi.get(text);
		    			}
		    			else {
		    				oscVal=Key2Midi.get(text)+(12*(numOctave+1));
		    				if(oscVal>127) {
		    					oscVal=127;
		    				}
		    			}
		    			al.startSynth(oscVal, A.val, D.val, S.val, R.val, LFO1type.getSelectedItem().toString());
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					//label.setBackground(Color.black);

				}

				@Override
				public void mouseEntered(MouseEvent e) {	
					label.setBackground(Color.DARK_GRAY);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					label.setBackground(Color.black);
				}
				
			});
			label.setBorder(BorderFactory.createLineBorder(Color.black));
			label.setBounds(origin.x, origin.y, 75, 210);
			return label;
    }
    
    private void initializeMaps(){
    	
    		Ascii2Midi.put(90, 0);	//z
    		Ascii2Midi.put(83, 2);	//s	
    		Ascii2Midi.put(88, 4);	//x
    		Ascii2Midi.put(68, 5);	//d
    		Ascii2Midi.put(67, 7);	//c
    		Ascii2Midi.put(70, 9);	//f
    		Ascii2Midi.put(86, 11);	//v
    		Ascii2Midi.put(71, 12);	//g
    		Ascii2Midi.put(66, 14);	//b
    		Ascii2Midi.put(72, 16);	//h
    		Ascii2Midi.put(78, 17);	//n
    		Ascii2Midi.put(74, 19);	//j
    		Ascii2Midi.put(77, 21);	//m
    		Ascii2Midi.put(75, 23);	//k
		
    		Ascii2Midi.put(87, 1);	//w
    		Ascii2Midi.put(69, 3);	//e
    		Ascii2Midi.put(82, 6);	//r
    		Ascii2Midi.put(84, 8);	//t
    		Ascii2Midi.put(89, 10);	//y
    		Ascii2Midi.put(85, 13);	//u
    		Ascii2Midi.put(73, 15);	//i
    		Ascii2Midi.put(79, 18);	//o
    		Ascii2Midi.put(80, 20);	//p
    		Ascii2Midi.put(91, 22);	//[
		
		Key2Midi.put("Z", 0);	//z
		Key2Midi.put("S", 2);	//s	
		Key2Midi.put("X", 4);	//x
		Key2Midi.put("D", 5);	//d
		Key2Midi.put("C", 7);	//c
		Key2Midi.put("F", 9);	//f
		Key2Midi.put("V", 11);	//v
		Key2Midi.put("G", 12);	//g
		Key2Midi.put("B", 14);	//b
		Key2Midi.put("H", 16);	//h
		Key2Midi.put("N", 17);	//n
		Key2Midi.put("J", 19);	//j
		Key2Midi.put("M", 21);	//m
		Key2Midi.put("K", 23);	//k
		
		Key2Midi.put("W", 1);	//w
		Key2Midi.put("E", 3);	//e
		Key2Midi.put("R", 6);	//r
		Key2Midi.put("T", 8);	//t
		Key2Midi.put("Y", 10);	//y
		Key2Midi.put("U", 13);	//u
		Key2Midi.put("I", 15);	//i
		Key2Midi.put("O", 18);	//o
		Key2Midi.put("P", 20);	//p
		Key2Midi.put("[", 22);	//[
    }

    //Create the control pane for the top of the frame.
    private JPanel createControlPanel() {
    		A = new RotaryKnob();
    		D = new RotaryKnob();
    		S = new RotaryKnob();
    		R = new RotaryKnob();
        JPanel controlPanel = new JPanel();
        JPanel ADSR = new JPanel();
        ADSR.setLayout(new GridLayout(2,2));
        ADSR.add(A);
        ADSR.add(S);
        ADSR.add(D);
        ADSR.add(R);
        ADSR.setBorder(BorderFactory.createTitledBorder("ADSR"));

        JPanel spinners = new JPanel();
        spinners.setLayout(new GridLayout(3,2));

        SpinnerNumberModel BPMoptions = new SpinnerNumberModel(72, 0, 200, 1);
        bpm=new JSpinner(BPMoptions);
        metronome = new JCheckBox("Metronome");
        metronome.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Metronome met=new Metronome();
				if(metronome.isSelected()) {
					System.out.println(Integer.parseInt(bpm.getValue().toString()));
					met.start(Integer.parseInt(bpm.getValue().toString()));
				}
				else {
					met.stop();
				}
			}
        	
        });
        spinners.add(bpm);
        spinners.add(metronome);
        
        mode = new JSpinner(new SpinnerListModel(new String[]{"Play","Tutorial"}));
        ((DefaultEditor) mode.getEditor()).getTextField().setEditable(false);
        spinners.add(mode);
        
        octave = new JSpinner(new SpinnerListModel(new String[]{"C-1","C0","C1","C2","C3","C4","C5","C6","C7","C8"}));
        octave.setValue("C4");
        ((DefaultEditor) octave.getEditor()).getTextField().setEditable(false);
        spinners.add(octave);
   

		Osc1Type=new JComboBox(new String[] {"sawtooth", "triangle", "square", "sine"});
		Osc2Type=new JComboBox(new String[] {"sawtooth", "triangle", "square", "sine"});
		
		LFO1type=new JComboBox(new String[] {"sawtooth", "triangle", "square"});
		LFO2type=new JComboBox(new String[] {"sawtooth", "triangle", "square"});

        JPanel Osc = new JPanel();
        JPanel Osc1=new JPanel();
        JPanel Osc2=new JPanel();
		RotaryKnob Oscillator1 = new RotaryKnob();
		RotaryKnob Oscillator2 = new RotaryKnob();
		
        Osc1.setLayout(new GridLayout(1,1));
		Osc1.setBorder(BorderFactory.createTitledBorder("Osc 1"));
		Osc1.add(Oscillator1);
		Osc1.add(Osc1Type);

		Osc2.setLayout(new GridLayout(1,1));
		Osc2.setBorder(BorderFactory.createTitledBorder("Osc 2"));
		Osc2.add(Oscillator2);
		Osc2.add(Osc2Type);

		Osc.setLayout(new GridLayout(2,2));
		//Osc.setBorder(BorderFactory.createTitledBorder("Oscillators"));
		Osc.add(Osc1);
		Osc.add(Osc2);
		
        JPanel LFO = new JPanel();
        JPanel LFO1=new JPanel();
        JPanel LFO2=new JPanel();
		RotaryKnob LF01Knob = new RotaryKnob();
		RotaryKnob LFO2Knob = new RotaryKnob();
		
        LFO1.setLayout(new GridLayout(1,1));
		LFO1.setBorder(BorderFactory.createTitledBorder("LFO 1"));
		LFO1.add(LF01Knob);
		LFO1.add(LFO1type);

		LFO2.setLayout(new GridLayout(1,1));
		LFO2.setBorder(BorderFactory.createTitledBorder("LFO 2"));
		LFO2.add(LFO2Knob);
		LFO2.add(LFO2type);

		LFO.setLayout(new GridLayout(2,2));
		//LFO.setBorder(BorderFactory.createTitledBorder("LFO"));
		LFO.add(LFO1);
		LFO.add(LFO2);
		
        controlPanel.add(spinners);
        controlPanel.add(Osc);
        controlPanel.add(LFO);
        controlPanel.add(ADSR);
        return controlPanel;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("LayeredPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		alltogether al=new alltogether();

        //Create and set up the content pane.
        JComponent newContentPane = new LayeredPaneDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
        frame.addKeyListener(new KeyListener() {
	    		public void keyTyped(KeyEvent e) {
	    			System.out.println("typed");
	    		}
	
	    		public void keyReleased(KeyEvent e) {
	    			System.out.println("released");
	    		}
	
	    		public void keyPressed(KeyEvent e) {
	    			if(e.getKeyCode()==61) {
	    				if(Integer.parseInt(octave.getValue().toString().substring(1, octave.getValue().toString().length()))<8) {
		    				int val=Integer.parseInt(octave.getValue().toString().substring(1, octave.getValue().toString().length()));
		    				octave.setValue("C"+(val+1));
	    				}
	    			}
	    			else if(e.getKeyCode()==45) {
	    				if(Integer.parseInt(octave.getValue().toString().substring(1, octave.getValue().toString().length()))>-1) {
		    				int val=Integer.parseInt(octave.getValue().toString().substring(1, octave.getValue().toString().length()));
		    				octave.setValue("C"+(val-1));
	    				}	
	    			}
	    			else{
		    			System.out.println("pressed "+e.getKeyChar()+" "+e.getKeyCode());
		    			alltogether al = new alltogether();
		    			String sOctave=octave.getValue().toString().substring(1, octave.getValue().toString().length());
		    			int numOctave=Integer.parseInt(sOctave);
		    			int oscVal=0;
		    			if(numOctave==-1) {
		    				oscVal=Ascii2Midi.get(e.getKeyCode());
		    			}
		    			else {
		    				oscVal=Ascii2Midi.get(e.getKeyCode())+(12*(numOctave+1));
		    				if(oscVal>127) {
		    					oscVal=127;
		    				}
		    			}
		    			al.startSynth(oscVal, A.val, D.val, S.val, R.val, LFO1type.getSelectedItem().toString());
		    			int k = e.getKeyCode();
	    			}
	    		}
        });
        frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				frame.requestFocus();
			}

			public void mouseClicked(MouseEvent me) {
				frame.requestFocus();
			}
        });
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}