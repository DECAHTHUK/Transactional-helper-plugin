import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

public class TransactionalSelfInvocationProblemWithNoSelf {

    @Autowired
    private TransactionalSelfInvocationProblemWithNoSelf self;

    public void outerMethod() {
         self.innerMethod() ;
    }

    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }
}