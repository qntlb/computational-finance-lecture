package info.quantlab.computationfinance.lecture;

public class Assignment2Checker {

	public class Result {
		public Result(boolean success, String message) {
			super();
			this.success = success;
			this.message = message;
		}
		public boolean success;
		public String message;
	}

	public Result check(Assignment2 solution, int level) {
		return new Result(false, "Your solution may or may not be correct. Tests are not implemented yet.");
	}
}
