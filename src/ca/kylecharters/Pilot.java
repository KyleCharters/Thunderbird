package ca.kylecharters;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Pilot {

	private static final NXTRegulatedMotor centerMotor = Motor.B;
	private static final NXTRegulatedMotor leftMotor = Motor.C;
	private static final NXTRegulatedMotor rightMotor = Motor.A;

	public static final float MAX_SPEED = Math.min(centerMotor.getMaxSpeed(),
			Math.min(leftMotor.getMaxSpeed(), rightMotor.getMaxSpeed()));

	public float leftScalar, rightScalar, speed;

	Pilot() {
		leftScalar = 1;
		rightScalar = 1;
		speed = MAX_SPEED;
	}

	public void update() {
		float center = speed * ((leftScalar + rightScalar) / 2);
		float left = speed * leftScalar;
		float right = speed * rightScalar;

		centerMotor.setSpeed(center);
		leftMotor.setSpeed(left);
		rightMotor.setSpeed(right);

		if (center > 0) {
			centerMotor.forward();
		} else {
			centerMotor.backward();
		}

		if (left > 0) {
			leftMotor.forward();
		} else {
			leftMotor.backward();
		}

		if (right > 0) {
			rightMotor.forward();
		} else {
			rightMotor.backward();
		}
	}
}