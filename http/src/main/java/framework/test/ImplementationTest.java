package framework.test;

import framework.annotations.dependency_injection.*;

@Qualifier("implementation")
@Component
public class ImplementationTest implements ITest {
}
