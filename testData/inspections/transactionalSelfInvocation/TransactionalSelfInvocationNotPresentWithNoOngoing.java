import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class TransactionalSelfInvocationNotPresentWithNoOngoing {

    public void outerMethod() {

    }

    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }
}