import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "subscriptions")
@IdClass(Subscription.SubscriptionKeys.class)
public class Subscription {
    @Id
    @Column(name = "student_id")
    private int studentId;

    @Id
    @Column(name = "course_id")
    private int courseId;

    @Column(name = "subscription_date")
    private Date subscriptionDate;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public static class SubscriptionKeys implements Serializable {
        @Column(name = "student_id", insertable = false, updatable = false)
        private int studentId;

        @Column(name = "course_id", insertable = false, updatable = false)
        private int courseId;

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return studentId == that.studentId &&
                courseId == that.courseId &&
                subscriptionDate.equals(that.subscriptionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId, subscriptionDate);
    }
}
