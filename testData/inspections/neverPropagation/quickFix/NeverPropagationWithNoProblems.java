import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NeverPropagationWithNoProblems {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

@Transactional(propagation = Propagation.NEVER)
class NestedTransactionalMethod {

    public void innerMethod() {
        // Some code
    }
}