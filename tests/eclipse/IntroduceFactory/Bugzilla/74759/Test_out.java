package p;

public class Test {

	public static Test createTest(Test tal) {
		return new Test(tal);
	}

	private /*[*/Test/*]*/(Test tal) {
		return;
	}

	public static void main(String[] args) {
		final Test test= createTest(createTest(null));

		System.out.println(createTest(decorate(createTest(null))));
	}

	private static Test decorate(Test test) {
		return test;
	}
}
