import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NestedRequiresNewWithOngoing {

    @Transactional
    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedTransactionalMethod {

    <warning descr="Nested transaction here. Creating a new transaction inside an existing one may produce unexpected results(Applicable to hibernate).">@Transactional(propagation = Propagation.REQUIRES_NEW)</warning>
    public void innerMethod() {
        // Some code
    }
}