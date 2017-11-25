package ca.kylecharters;

import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorConstants;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;

public class Input {
	public static final int EDGE = Color.WHITE;
	public static final int RAMP = Color.GREEN;

	private TouchSensor frontSensor;
	private UltrasonicSensor backSensor;
	private ColorSensor rightSensor, leftSensor;

	public int rightDetect, leftDetect;
	public boolean frontTouched, backSensed;

	Input() {
		frontSensor = new TouchSensor(SensorPort.S1);
		backSensor = new UltrasonicSensor(SensorPort.S2);
		rightSensor = new ColorSensor(SensorPort.S3, SensorConstants.WHITE);
		leftSensor = new ColorSensor(SensorPort.S4, SensorConstants.WHITE);
	}

	public void update() {
		rightDetect = rightSensor.getColorID();
		leftDetect = leftSensor.getColorID();
		frontTouched = frontSensor.isPressed();
		backSensed = backSensor.getDistance() < 15;
	}
}
