import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class NeverPropagationWithOngoingInLambda {

    @Transactional(propagation = Propagation.NEVER)
    public void outerMethod() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }

    @Transactional
    public void outerMethod2() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        RunnableCallProxyTransactional callProxy = new RunnableCallProxyTransactional();
        callProxy.call(() -> nestedTransactionalMethod.innerMethod());
        RunnableCallProxyTransactionalSecond callProxySecond = new RunnableCallProxyTransactionalSecond();
        callProxySecond.call(() -> nestedTransactionalMethod.innerMethod());
    }
}

class NestedTransactionalMethod {

    <warning descr="Never propagated transactional declaration may have an ongoing transaction here. Check your call tree.">@Transactional(propagation = Propagation.NEVER)</warning>
    public void innerMethod() {
        // Some code
    }
}

class RunnableCallProxyTransactional {

    public void call(Runnable r) {
        r.run();
    }

    public void callWithProp(Runnable r) {
        r.run();
    }
}

class RunnableCallProxyTransactionalSecond {

    @Transactional
    public void call(Runnable r) {
        r.run();
    }
}