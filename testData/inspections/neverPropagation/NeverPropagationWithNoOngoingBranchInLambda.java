import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NeverPropagationWithNoOngoingBranchInLambda {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactional callProxy = new RunnableCallProxyTransactional();
        callProxy.callWithProp(() -> nestedTransactionalMethod.innerMethod());
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedUpperTransactionalMethod {

    @Transactional
    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactionalSecond callProxySecond = new RunnableCallProxyTransactionalSecond();
        callProxySecond.call(() -> nestedTransactionalMethod.innerMethod());
    }
}

class NestedTransactionalMethod {

    @Transactional(propagation = Propagation.NEVER)
    public void innerMethod() {
        // Some code
    }
}

@Transactional
class RunnableCallProxyTransactional {

    public void call(Runnable r) {
        r.run();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void callWithProp(Runnable r) {
        r.run();
    }
}

class RunnableCallProxyTransactionalSecond {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void call(Runnable r) {
        r.run();
    }
}