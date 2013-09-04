Parametric JUnit Runner
=======================

A JUnit test runner that makes it easy for developers to write parameterized test cases. I created `Parametric` as an exercise to devise a better parameterized testing API than the one used with JUnit's `Parameterized` runner. When teaching TDD, I find the `Parameterized` runner is slightly too difficult to grok for beginners, to the point where I would not recommend using it even in situations where it might be useful.

Example
-------

A contrived example would be testing an `Adder` object whose responsibility is to compute the sum of two integers. Rather than creating many similar test cases with expected results, an enterprising developer might parameterize the tests:

```java
@RunWith(Parametric.class)
public class AdderTest {

	private final int left;
	private final int right;
	private final long expectedSum;

	public AdderTest(final int left, final int right, final long expectedSum) {
		this.left = left;
		this.right = right;
		this.expectedSum = expectedSum;
	}

	@TestCases
	public static Iterable<AdderTest> testCases() {
		final ArrayList<AdderTest> cases = new ArrayList<AdderTest>();
		cases.add(new AdderTest(1, 2, 3));
		cases.add(new AdderTest(8, 46, 54));
		cases.add(new AdderTest(42, 0, 42));
		cases.add(new AdderTest(-3, 3, 0));
		cases.add(new AdderTest(44728, 2346, 47074));
		cases.add(new AdderTest(2147483647, 3, 2147483650L));
		return cases;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0} and {1} make {2}", 
									this.left, this.right, this.expectedSum);
	}

	@Test
	public void additionIsPerformedCorrectlyLeftToRight() {
		final Adder adder = new Adder();
		final long sum = adder.add(this.left, this.right);
		assertThat(sum, is(equalTo(this.expectedSum)));
	}

	@Test
	public void additionIsCommutativeRightToLeft() {
		final Adder adder = new Adder();
		final long sum = adder.add(this.right, this.left);
		assertThat(sum, is(equalTo(this.expectedSum)));
	}
}
```

Comparison to `Parameterized`
-----------------------------

`Parametric` provides very similar functionality to JUnit's `Parameterized` runner, however it preserves compile-time type checking and readability by not calling constructors reflectively. The above example could be written using the `Parameterized` runner as follows:

```java
	@Parameters(name = "{0} and {1} make {2}")
	public static Iterable<Object[]> getParameters() {
		final ArrayList<Object[]> cases = new ArrayList<Object[]>();
		cases.add(new Object[] { 1, 2, 3 });
		cases.add(new Object[] { 8, 46, 54 });
		cases.add(new Object[] { 42, 0, 42 });
		cases.add(new Object[] { -3, 3, 0 });
		cases.add(new Object[] { 44728, 2346, 47074 });
		cases.add(new Object[] { 2147483647, 3, 2147483650L });
		return cases;
	}
```

Unless you are accustomed to reading code that reflectively provides method parameters, the above can be somewhat confusing at first. Additionally, the `name` attribute of the `@Parameters` annotation is convenient, but not obvious to many developers.


To-Do
-----

- Compile-time, inline warnings and errors for invalid `Parametric` test classes
- Fix `@BeforeClass` and `@AfterClass` so they are not executed for each test case
- A more explicit way to provide the test case name than overriding `toString()`
