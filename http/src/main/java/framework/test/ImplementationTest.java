package framework.test;

import framework.annotations.dependency_injection.*;

@Qualifier("implementation")
@Service
public class ImplementationTest implements ITest {
}
