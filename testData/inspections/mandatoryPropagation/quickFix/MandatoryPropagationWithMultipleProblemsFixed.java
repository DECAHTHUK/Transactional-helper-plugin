import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class MandatoryPropagationWithMultipleProblems {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
        nestedTransactionalMethod.innerMethod2();
    }
}

class NestedTransactionalMethod {

    @Transactional
    public void innerMethod() {
        // Some code
    }

    @Transactional
    public void innerMethod2() {
        // Some code
    }
}