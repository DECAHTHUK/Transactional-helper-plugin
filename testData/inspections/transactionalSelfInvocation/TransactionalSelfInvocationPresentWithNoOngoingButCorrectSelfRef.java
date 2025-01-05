import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class TransactionalSelfInvocationPresentWithNoOngoingButCorrectSelfRef {

    private TransactionalSelfInvocationPresentWithNoOngoingButCorrectSelfRef self;

    public void outerMethod() {
        self.innerMethod();
    }

    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }
}