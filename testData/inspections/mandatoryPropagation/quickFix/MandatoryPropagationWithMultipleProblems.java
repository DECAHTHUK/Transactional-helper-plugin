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

    <warning descr="Mandatory propagated transactional declaration may have no ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.MANDATORY)</warning>
    public void innerMethod() {
        // Some code
    }

    <warning descr="Mandatory propagated transactional declaration may have no ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.MANDATORY)</warning>
    public void innerMethod2() {
        // Some code
    }
}
