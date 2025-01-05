import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.List;

public class NestedRequiresNewWithOngoingButCorrectSelfRef {

    private NestedRequiresNewWithOngoingButCorrectSelfRef self;

    public void outerMethod() {
        self.outerMethodTransactional();
    }

    @Transactional
    public void outerMethodTransactional() {
        NestedTransactionalMethod nestedTransactionalMethod = new NestedTransactionalMethod();
        nestedTransactionalMethod.innerMethod();
    }
}

class NestedTransactionalMethod {

    <warning descr="Nested transaction here. Creating a new transaction inside an existing one may produce unexpected results(Applicable to hibernate).">@Transactional(propagation = Propagation.REQUIRES_NEW)</warning>
    public void innerMethod() {
        // Some code
    }
}