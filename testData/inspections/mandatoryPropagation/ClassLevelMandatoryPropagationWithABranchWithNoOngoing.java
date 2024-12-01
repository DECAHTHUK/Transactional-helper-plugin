import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Transactional // ongoing
public class ClassLevelMandatoryPropagationWithABranchWithNoOngoing {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

@Transactional(propagation = Propagation.NEVER)
class BranchWithNoOngoing {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

<warning descr="Mandatory propagated transactional declaration may have no ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.MANDATORY)</warning>
class NestedTransactionalMethod {

    public void innerMethod() {
        // Some code
    }
}