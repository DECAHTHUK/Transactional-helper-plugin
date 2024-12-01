import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NestedRequiresNewWithSupportsAndUpper {

    @Transactional
    public void outerMethod() {
        SupportsMethodCLass supportsMethodCLass = new SupportsMethodCLass();
        supportsMethodCLass.innerMethod();
    }
}

class SupportsMethodCLass {

    @Transactional(propagation = Propagation.SUPPORTS)
    public void innerMethod() {
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