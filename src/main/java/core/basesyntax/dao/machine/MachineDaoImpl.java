package core.basesyntax.dao.machine;

import core.basesyntax.DataProcessingException;
import core.basesyntax.dao.AbstractDao;
import core.basesyntax.model.machine.Machine;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class MachineDaoImpl extends AbstractDao implements MachineDao {
    public MachineDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Machine save(Machine machine) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(machine);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                throw new DataProcessingException("Can't insert this machine into DB "
                        + machine, e);
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return machine;
    }

    @Override
    public List<Machine> findByAgeOlderThan(int age) {
        try (Session session = sessionFactory.openSession()) {
            Query<Machine> query = session.createQuery("FROM Machine m WHERE m.year < :year",
                    Machine.class);
            return query.setParameter("year", LocalDate.now().getYear() - age)
                    .getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find any machine elder than: " + age, e);
        }
    }
}
