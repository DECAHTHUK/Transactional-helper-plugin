import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class TransactionalSelfInvocationProblemWithMultipleProblems {

    @Autowired
    private TransactionalSelfInvocationProblemWithMultipleProblems self;

    public void outerMethod() {
         self.innerMethod() ;
    }

    public void outerMethod2() {
         self.innerMethod() ;
    }

    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }
}