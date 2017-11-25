package ca.kylecharters;

public enum Instruction {
	MOVE(new Fiber() {
		boolean reversing = false;
		float time = 0f;

		public boolean check(Pilot pilot, Input input) {
			boolean leftEdge = input.leftDetect == Input.EDGE;
			boolean rightEdge = input.rightDetect == Input.EDGE;

			if (leftEdge && rightEdge) {
				// Move back, start timer
				pilot.leftScalar = -1;
				pilot.rightScalar = -1;
				pilot.update();
				reversing = true;

				return true;
			} else if (rightEdge) {
				// Turning Left
				pilot.leftScalar = -2;
				pilot.rightScalar = -1;
				pilot.update();

				return true;
			} else if (leftEdge) {
				// Turning Right
				pilot.leftScalar = -1;
				pilot.rightScalar = -2;
				pilot.update();

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
				pilot.leftScalar = Math.min(1, pilot.leftScalar + delta);
				pilot.rightScalar = Math.min(1, pilot.rightScalar + delta);
				pilot.update();
			}

			return false;
		}

		public void cutoff(Pilot pilot, Input input) {
			reversing = false;
			time = 0f;
		}
	}, "Moving"),

	FIX_STALL(new Fiber() {
		boolean twisting = false;
		float time = 0f;

		public boolean check(Pilot pilot, Input input) {
			if (pilot.isStalled()) {
				pilot.leftScalar = -1;
				pilot.rightScalar = 1;
				pilot.update();
				twisting = true;

				return true;
			}

			return false;
		}

		public boolean update(Pilot pilot, Input input, float delta) {
			if (twisting && (time += delta) > 2) {
				// Move straight after timer
				pilot.leftScalar = 1;
				pilot.rightScalar = 1;
				pilot.update();

				return true;
			}

			return false;
		}

		public void cutoff(Pilot pilot, Input input) {
			twisting = false;
			time = 0f;
		}
	}, "Fix Stall"),

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
	}, "Push"),

	ALIGN_RAMP(new Fiber() {
		boolean aligning = false;

		public boolean check(Pilot pilot, Input input) {
			boolean leftRamp = input.leftDetect == Input.RAMP;
			boolean rightRamp = input.rightDetect == Input.RAMP;

			if (!(leftRamp && rightRamp)) {
				if (rightRamp) {
					aligning = true;
					pilot.leftScalar = 1;
					pilot.rightScalar = 0;
					pilot.update();

					return false;
				} else if (leftRamp) {
					aligning = true;
					pilot.leftScalar = 0;
					pilot.rightScalar = 1;
					pilot.update();

					return false;
				}
			}

			return true;
		}

		public boolean update(Pilot pilot, Input input, float delta) {
			if (aligning && input.leftDetect == Input.RAMP && input.rightDetect == Input.RAMP) {
				pilot.leftScalar = 1;
				pilot.rightScalar = 1;
				pilot.update();

				return true;
			}

			return false;
		}

		public void cutoff(Pilot pilot, Input input) {
			aligning = false;
		}
	}, "Align Ramp");

	interface Fiber {
		// Return true if demands control
		boolean check(Pilot pilot, Input input);

		// Return true if finished
		boolean update(Pilot pilot, Input input, float delta);

		void cutoff(Pilot pilot, Input input);
	}

	private Fiber fiber;
	public String name;

	Instruction(Fiber fiber, String name) {
		this.fiber = fiber;
		this.name = name;
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
