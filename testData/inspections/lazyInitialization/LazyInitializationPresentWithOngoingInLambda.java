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

public class LazyInitializationPresentWithOngoingInLambda {

    @Transactional
    public void outerMethod() {
        OtherClass otherClass = new OtherClass();
        RunnableCallProxyTransactional callProxy = new RunnableCallProxyTransactional();
        callProxy.call(() -> otherClass.innerMethod());
        callProxy.callWithProp(() -> otherClass.innerMethod());
    }
}

class OtherClass {

    public void innerMethod() {
        TestEntity testEntity = new TestEntity();
        testEntity.getUserIds();
    }
}

@Table(name = "test")
@Entity
class TestEntity {

    public TestEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "t_user", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "user_id")
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
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