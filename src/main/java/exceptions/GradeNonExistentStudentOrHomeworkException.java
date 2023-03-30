package exceptions;

public class GradeNonExistentStudentOrHomeworkException extends RuntimeException{
    public GradeNonExistentStudentOrHomeworkException(String exception) {
        super(exception);
    }
}
