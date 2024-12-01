import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Transactional
public class ClassLevelNeverPropagationWithOngoing {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

<warning descr="Never propagated transactional declaration may have an ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.NEVER)</warning>
class NestedTransactionalMethod {

    public void innerMethod() {
        // Some code
    }
}