import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Transactional
public class TransactionalSelfInvocationPresentWithClassLevel {

    public void outerMethod() {
        innerMethod();
    }

    @Transactional
    public void innerMethod() {
        System.out.println("inner method");
    }
}