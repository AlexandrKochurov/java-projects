import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(LinkedPurchaseList.LinkedPurchaseListKey.class)
@Table(name = "LinkedPurchaseList")
public class LinkedPurchaseList {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Id
    @Column(name = "course_id")
    private Integer courseId;

    public LinkedPurchaseList(Integer studentId, Integer courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public static class LinkedPurchaseListKey implements Serializable {
        @Id
        private Integer studentId;

        @Id
        private Integer courseId;

        public Integer getStudentId() {
            return studentId;
        }

        public void setStudentId(Integer studentId) {
            this.studentId = studentId;
        }

        public Integer getCourseId() {
            return courseId;
        }

        public void setCourseId(Integer courseId) {
            this.courseId = courseId;
        }
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedPurchaseList that = (LinkedPurchaseList) o;
        return studentId.equals(that.studentId) &&
                courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}
