import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class MandatoryPropagationWithNoProblems {

    @Transactional
    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedTransactionalMethod {

    @Transactional(propagation = Propagation.MANDATORY)
    public void innerMethod() {
        // Some code
    }
}