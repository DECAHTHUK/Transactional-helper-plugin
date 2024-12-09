import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.util.List;

public class LazyInitializationPresentDifferentEagerRelationsWithNoOngoing {

    public void outerMethod() {
        innerMethod();
    }

    public void innerMethod() {
        TestEntity testEntity = new TestEntity();
        testEntity.getTestEntity2RelList();
        TestEntity2 testEntity2 = new TestEntity2();
        testEntity2.getTestEntity3RelList();
        <warning descr="Getter call on a lazy initialized class without session. Make sure you are calling @Id getter or the class is initialized.">testEntity2.getTestEntityRel()</warning>;
        TestEntity3 testEntity3 = new TestEntity3();
        testEntity3.getTestEntity2RelList();
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

    @OneToMany(mappedBy="testEntityRel")
    private List<TestEntity2> testEntity2RelList;

    public List<TestEntity2> getTestEntity2RelList() {
        return testEntity2RelList;
    }
}

@Table(name = "test2")
@Entity
class TestEntity2 {

    public TestEntity2() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = TestEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "te1")
    private TestEntity testEntityRel;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "test2_test3",
            joinColumns = @JoinColumn(name = "test3"),
            inverseJoinColumns = @JoinColumn(name = "test2"))
    private List<TestEntity3> testEntity3RelList;

    public TestEntity getTestEntityRel() {
        return testEntityRel;
    }

    public List<TestEntity3> getTestEntity3RelList() {
        return testEntity3RelList;
    }
}

@Table(name = "test3")
@Entity
class TestEntity3 {

    public TestEntity3() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "test2_test3",
        joinColumns = @JoinColumn(name = "test2"),
        inverseJoinColumns = @JoinColumn(name = "test3"))
    private List<TestEntity2> testEntity2RelList;

    public List<TestEntity2> getTestEntity2RelList() {
        return testEntity2RelList;
    }
}