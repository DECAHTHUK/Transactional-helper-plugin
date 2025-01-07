import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class MandatoryPropagationWithOngoingInLambda {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactional callProxy = new RunnableCallProxyTransactional();
        callProxy.call(() -> nestedTransactionalMethod.innerMethod()());
    }
}

class NestedTransactionalMethod {

    @Transactional(propagation = Propagation.MANDATORY)
    public void innerMethod() {
        // Some code
    }
}

@Transactional(propagation = Propagation.REQUIRED)
class RunnableCallProxyTransactional {

    public void call(Runnable r) {
        r.run();
    }

    public void callWithProp(Runnable r) {
        r.run();
    }
}