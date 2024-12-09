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

public class LazyInitializationPresentWithNoOngoing {

    public void outerMethod() {
        innerMethod();
    }

    public void innerMethod() {
        TestEntity testEntity = new TestEntity();
        <warning descr="Getter call on a lazy initialized class without session. Make sure you are calling @Id getter or the class is initialized.">testEntity.getUserIds()</warning>;
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