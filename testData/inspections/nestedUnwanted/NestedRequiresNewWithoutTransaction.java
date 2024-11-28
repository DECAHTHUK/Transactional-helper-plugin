package testData.inspections.nestedUnwanted;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NestedRequiresNewWithoutTransaction {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedTransactionalMethod {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerMethod() {
        // Some code
    }
}