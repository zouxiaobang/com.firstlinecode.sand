package com.firstlinecode.sand.emulators.gateway;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import com.firstlinecode.sand.emulators.thing.IThing;

public class ThingInternalFrame extends JInternalFrame {
	private static final long serialVersionUID = 4975138886817512398L;
	
	private IThing thing;
	
	public ThingInternalFrame(IThing thing, String title) {
		super(title, false, false, false, false);
		
		this.thing = thing;
		JPanel panel = thing.getPanel();
		setContentPane(panel);
	}
	
	public IThing getThing() {
		return thing;
	}
}
