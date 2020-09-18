import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;


public class Main {
    public static void main(String[] args){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        Transaction transaction = session.beginTransaction();

        CriteriaQuery<PurchaseList> query = builder.createQuery(PurchaseList.class);
        Root<PurchaseList> root = query.from(PurchaseList.class);
        query.select(root);

        //Вывод зарплаты учителя под ID 2
        Teacher teacher = session.get(Teacher.class, 2);
        System.out.println(teacher.getSalary());

        final Course course = session.get(Course.class, 1);
        System.out.println(course.getStudents().size());


        Map<String, Integer> courses = new HashMap<>();
        session.createQuery("SELECT name, id FROM Course").getResultList().forEach(c -> {
            Object[] d = (Object[]) c;
            courses.put((String) d[0], (Integer) d[1]);
        });

        Map<String, Integer> students = new HashMap<>();
        session.createQuery("SELECT name, id FROM Student").getResultList().forEach(s -> {
            Object[] t = (Object[]) s;
            students.put((String)t[0], (Integer)t[1]);
        });


        session.createQuery(query).getResultList().forEach(purchase -> {

            PurchaseList.PurchaseListKey purchaseListKey = purchase.getPurchaseListKey();

            LinkedPurchaseList linkedPurchase = new LinkedPurchaseList(
                    students.get(purchaseListKey.getStudentName()),
                    courses.get(purchaseListKey.getCourseName()));

            session.save(linkedPurchase);
        });

        transaction.commit();
        sessionFactory.close();
    }
}
