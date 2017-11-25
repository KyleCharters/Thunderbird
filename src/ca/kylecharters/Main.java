package ca.kylecharters;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Sound;
import lejos.util.Delay;

public class Main {

	private Pilot pilot;
	private Input input;

	private float pastTime;

	private Instruction currentControl;

	Main() {

		pilot = new Pilot();
		input = new Input();

		Sound.beep();
		System.out.println("Ready.");

		Button.waitForAnyPress();

		for (int i = 0; i < 5; i++) {
			Sound.beep();
			System.out.println(Integer.toString(5 - i));
			Delay.msDelay(1000);
		}

		System.out.println("Go.");

		pilot.update();

		currentControl = Instruction.MOVE;

		pastTime = System.currentTimeMillis();

		while (true) {
			input.update();

			for (Instruction control : Instruction.values()) {
				if (control.check(pilot, input)) {
					currentControl = control;
					break;
				}
			}

			float delta = (System.currentTimeMillis() - pastTime) / 1000;
			pastTime = System.currentTimeMillis();

			if (currentControl.update(pilot, input, delta)) {
				currentControl.cutoff(pilot, input);
				currentControl = Instruction.MOVE;
			}
		}
	}

	public static void main(String[] args) {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) {
			}

			public void buttonPressed(Button b) {
				System.exit(0);
			}
		});

		new Main();
	}
}
