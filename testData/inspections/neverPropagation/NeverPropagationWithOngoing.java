import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NeverPropagationWithOngoing {

    @Transactional
    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedTransactionalMethod {

    <warning descr="Never propagated transactional declaration may have an ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.NEVER)</warning>
    public void innerMethod() {
        // Some code
    }
}