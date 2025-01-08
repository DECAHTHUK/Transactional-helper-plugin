import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NestedRequiresNewWithNoOngoingBranchesInLambda {

    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactional callProxy = new RunnableCallProxyTransactional();
        callProxy.call(() -> nestedTransactionalMethod.innerMethod());
        RunnableCallProxyTransactionalSecond callProxySecond = new RunnableCallProxyTransactionalSecond();
        callProxySecond.call(() -> nestedTransactionalMethod.innerMethod());
    }

    public void outerMethodSecond() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactionalSecond callProxySecond = new RunnableCallProxyTransactionalSecond();
        callProxySecond.call(() -> nestedTransactionalMethod.innerMethod());
    }
}

class NestedTransactionalMethod {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerMethod() {
        // Some code
    }
}

@Transactional
class RunnableCallProxyTransactional {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void call(Runnable r) {
        r.run();
    }

    @Transactional(propagation = Propagation.NEVER)
    public void callWithProp(Runnable r) {
        r.run();
    }
}

class RunnableCallProxyTransactionalSecond {

    public void call(Runnable r) {
        r.run();
    }
}