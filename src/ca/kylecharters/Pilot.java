package ca.kylecharters;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Pilot {
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor centerMotor = Motor.B;
	private static final NXTRegulatedMotor rightMotor = Motor.A;
	public static final float MAX_SPEED = Math.min(rightMotor.getMaxSpeed(),
			Math.min(leftMotor.getMaxSpeed(), centerMotor.getMaxSpeed()));

	public float leftScalar, rightScalar, speed;

	Pilot() {
		leftMotor.setStallThreshold(1, 10_000);
		rightMotor.setStallThreshold(1, 10_000);
		centerMotor.setStallThreshold(1, 10_000);

		leftScalar = 1;
		rightScalar = 1;
		speed = MAX_SPEED;
	}

	public boolean isStalled() {
		return leftMotor.isStalled() && rightMotor.isStalled() && centerMotor.isStalled();
	}

	public void update() {
		leftMotor.setSpeed(speed * Math.max(leftScalar, -1));
		rightMotor.setSpeed(speed * Math.max(rightScalar, -1));
		centerMotor.setSpeed(speed * ((leftScalar + rightScalar) / 2));

		leftMotor.forward();
		rightMotor.forward();
		centerMotor.forward();
	}
}