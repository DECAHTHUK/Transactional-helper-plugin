import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class MandatoryPropagationWithNoOngoingBranchInLambda {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactional callProxy = new RunnableCallProxyTransactional();
        callProxy.call(() -> nestedTransactionalMethod.innerMethod());
        callProxy.callWithProp(() -> nestedTransactionalMethod.innerMethod());
    }
}

class NestedTransactionalMethod {

    <warning descr="Mandatory propagated transactional declaration may have no ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.MANDATORY)</warning>
    public void innerMethod() {
        // Some code
    }
}

class RunnableCallProxyTransactional {

    public void call(Runnable r) {
        r.run();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void callWithProp(Runnable r) {
        r.run();
    }
}