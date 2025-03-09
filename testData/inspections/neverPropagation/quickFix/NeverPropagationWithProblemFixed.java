import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Transactional
public class NeverPropagationWithProblemFixed {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

@Transactional
class NestedTransactionalMethod {

    public void innerMethod() {
        // Some code
    }
}