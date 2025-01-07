import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class TransactionalSelfInvocationMultiplePresentWithNoOngoing {

    public void outerMethod() {
        <warning descr="Transactional method self invocation from the same class. Proxy won't work.">innerMethod()</warning>;
        boolean somelogic = false;
        if (!somelogic) {
            <warning descr="Transactional method self invocation from the same class. Proxy won't work.">innerMethod2()</warning>;
        }
    }


    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void innerMethod2() {
        System.out.println("inner method 2");
    }
}