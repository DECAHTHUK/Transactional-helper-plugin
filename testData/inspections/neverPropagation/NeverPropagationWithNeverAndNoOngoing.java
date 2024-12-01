import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NeverPropagationWithNeverAndNoOngoing {

    @Transactional(propagation = Propagation.NEVER)
    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedTransactionalMethod {

    @Transactional(propagation = Propagation.NEVER)
    public void innerMethod() {
        // Some code
    }
}