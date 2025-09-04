import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class TransactionalSelfInvocationPresentWithNoOngoing {

    public void outerMethod() {
        <warning descr="Transactional method self-invocation from the same class. Proxy won't work.">innerMethod()</warning>;
    }

    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }
}