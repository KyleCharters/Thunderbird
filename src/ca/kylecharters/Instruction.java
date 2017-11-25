package ca.kylecharters;

public enum Instruction {
	MOVE(new Fiber() {
		boolean reversing = false;
		float time = 0f;

		public boolean check(Pilot pilot, Input input) {
			boolean leftEdge = input.leftDetect == Input.EDGE;
			boolean rightEdge = input.rightDetect == Input.EDGE;
			boolean leftRamp = input.leftDetect == Input.RAMP;
			boolean rightRamp = input.rightDetect == Input.RAMP;

			if (leftEdge && rightEdge) {
				// Move back, start timer
				pilot.leftScalar = -1;
				pilot.rightScalar = -1;
				pilot.update();
				reversing = true;

				return true;
			} else if (rightEdge) {

				if (!leftRamp) {
					// Turning Left
					pilot.leftScalar = -2;
					pilot.rightScalar = -1;
					pilot.update();
				}

				return true;
			} else if (leftEdge) {

				if (!rightRamp) {
					// Turning Right
					pilot.leftScalar = -1;
					pilot.rightScalar = -2;
					pilot.update();
				}

				return true;
			}

			return false;
		}

		public boolean update(Pilot pilot, Input input, float delta) {
			if (reversing && (time += delta) > 1.2 && !input.backSensed) {
				// Turn left when timer is up
				pilot.leftScalar = -2;
				pilot.rightScalar = -1;
				pilot.update();

				return true;
			} else {
				pilot.leftScalar = Math.min(1, pilot.leftScalar + delta * 1.5f);
				pilot.rightScalar = Math.min(1, pilot.rightScalar + delta * 1.5f);
				pilot.update();
			}

			return false;
		}

		public void cutoff(Pilot pilot, Input input) {
			reversing = false;
			time = 0f;
		}
	}),

	PUSH(new Fiber() {
		public boolean check(Pilot pilot, Input input) {
			if (input.frontTouched || input.backSensed) {
				pilot.speed = Pilot.MAX_SPEED * 0.7f;

				return true;
			}

			return false;
		}

		public boolean update(Pilot pilot, Input input, float delta) {
			if (input.frontTouched) {
				pilot.leftScalar = 1;
				pilot.rightScalar = 1;
				pilot.update();

				return false;
			} else if (input.backSensed) {
				pilot.leftScalar = -1;
				pilot.rightScalar = -1;
				pilot.update();

				return false;
			}

			return true;
		}

		public void cutoff(Pilot pilot, Input input) {
			pilot.speed = Pilot.MAX_SPEED;
		}
	}),

	ALIGN_RAMP(new Fiber() {
		boolean aligning = false;
		float time = 0f;

		public boolean check(Pilot pilot, Input input) {
			boolean leftRamp = input.leftDetect == Input.RAMP;
			boolean rightRamp = input.rightDetect == Input.RAMP;

			if (!(leftRamp && rightRamp)) {
				if (rightRamp) {
					aligning = true;
					pilot.leftScalar = 1;
					pilot.rightScalar = -0.25f;
					pilot.update();

					return true;
				} else if (leftRamp) {
					aligning = true;
					pilot.leftScalar = -0.25f;
					pilot.rightScalar = 1;
					pilot.update();

					return true;
				}
			}

			return false;
		}

		public boolean update(Pilot pilot, Input input, float delta) {
			if (aligning && (time += delta) < 2 && input.leftDetect == Input.RAMP
					|| input.leftDetect == Input.EDGE && input.rightDetect == Input.RAMP
					|| input.rightDetect == Input.EDGE) {

				pilot.leftScalar = 1;
				pilot.rightScalar = 1;
				pilot.update();

				return true;
			}

			return false;
		}

		public void cutoff(Pilot pilot, Input input) {
			aligning = false;
			time = 0f;
		}
	});

	interface Fiber {
		// Return true if demands control
		boolean check(Pilot pilot, Input input);

		// Return true if finished
		boolean update(Pilot pilot, Input input, float delta);

		// When control is cut off
		void cutoff(Pilot pilot, Input input);
	}

	private Fiber fiber;

	Instruction(Fiber fiber) {
		this.fiber = fiber;
	}

	public boolean check(Pilot pilot, Input input) {
		return fiber.check(pilot, input);
	}

	public boolean update(Pilot pilot, Input input, float delta) {
		return fiber.update(pilot, input, delta);
	}

	public void cutoff(Pilot pilot, Input input) {
		fiber.cutoff(pilot, input);
	}
}
