import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Transactional
public class ClassLevelNestedRequiresNewWithOngoing {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

<warning descr="Nested transaction here. Creating a new transaction inside an existing one may produce unexpected results(Applicable to hibernate).">@Transactional(propagation = Propagation.REQUIRES_NEW)</warning>
class NestedTransactionalMethod {

    public void innerMethod() {
        // Some code
    }
}