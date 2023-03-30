package ssvv.example;

import exceptions.ValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.NotaXMLRepository;
import repository.StudentXMLRepository;
import repository.TemaXMLRepository;
import service.Service;
import validation.NotaValidator;
import validation.StudentValidator;
import validation.TemaValidator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.StreamSupport;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    private static final String VALID_ID = "9";
    private static final String VALID_NAME = "George";
    private static final int VALID_GROUP = 937;

    private static final String STUDENT_TEST_XML_FILE = "student_test.xml";
    private static final String HOMEWORK_TEST_XML_FILE = "homework_test.xml";
    private static final String GRADES_TEST_XML_FILE = "grade_test.xml";

    private static final String INVALID_ID_ERROR_MESSAGE = "ID invalid!\n";
    private static final String INVALID_NAME_ERROR_MESSAGE = "Nume invalid!\n";
    private static final String GROUP_NAME_ERROR_MESSAGE = "Grupa invalid!\n";

    private Service service;

    private File createXMLFile(String fileName) throws IOException {
        File createdFile = new File(fileName);
        if (createdFile.createNewFile()) {
            try (FileWriter fileWriter = new FileWriter(createdFile)) {
                fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<Entitati>\n</Entitati>");
            }
        } else {
            throw new RuntimeException("File " + fileName + " could not be created!");
        }
        return createdFile;
    }

    @Before
    public void setUp() throws IOException {
        File studentFile = createXMLFile(STUDENT_TEST_XML_FILE);
        File temeFile = createXMLFile(HOMEWORK_TEST_XML_FILE);
        File noteFile = createXMLFile(GRADES_TEST_XML_FILE);

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        TemaXMLRepository fileRepository2 = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        NotaXMLRepository fileRepository3 = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @After
    public void tearDown() {
        new File(STUDENT_TEST_XML_FILE).delete();
        new File(HOMEWORK_TEST_XML_FILE).delete();
        new File(GRADES_TEST_XML_FILE).delete();
    }

    @Test
    public void addStudent_Fail_nullIdTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(null, VALID_NAME, VALID_GROUP), INVALID_ID_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_emptyIdTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent("", VALID_NAME, VALID_GROUP), INVALID_ID_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_nullNameTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, null, VALID_GROUP), INVALID_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_emptyNameTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, "", VALID_GROUP), INVALID_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_tooLowGroupTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, VALID_NAME, 110), GROUP_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_Fail_tooHighGroupTest() {
        assertThrows(ValidationException.class, () -> service.saveStudent(VALID_ID, null, 938), GROUP_NAME_ERROR_MESSAGE);
    }

    @Test
    public void addStudent_OK_lowLimitGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent(VALID_ID, VALID_NAME, 111));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), VALID_ID);
        assertEquals(addedStudent.getNume(), VALID_NAME);
        assertEquals(addedStudent.getGrupa(), 111);
    }

    @Test
    public void addStudent_OK_lowLimitPlusOneGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent(VALID_ID, VALID_NAME, 112));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), VALID_ID);
        assertEquals(addedStudent.getNume(), VALID_NAME);
        assertEquals(addedStudent.getGrupa(), 112);
    }

    @Test
    public void addStudent_OK_highLimitMinusOneGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent(VALID_ID, VALID_NAME, 936));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), VALID_ID);
        assertEquals(addedStudent.getNume(), VALID_NAME);
        assertEquals(addedStudent.getGrupa(), 936);
    }

    @Test
    public void addStudent_OK_highLimitGroupTest() {
        assertDoesNotThrow(() -> service.saveStudent(VALID_ID, VALID_NAME, VALID_GROUP));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), VALID_ID);
        assertEquals(addedStudent.getNume(), VALID_NAME);
        assertEquals(addedStudent.getGrupa(), VALID_GROUP);
    }

    @Test
    public void addStudent_OK_highIdTest() {
        String testId = "99";

        assertDoesNotThrow(() -> service.saveStudent(testId, VALID_NAME, VALID_GROUP));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(testId)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), testId);
        assertEquals(addedStudent.getNume(), VALID_NAME);
        assertEquals(addedStudent.getGrupa(), VALID_GROUP);
    }

    @Test
    public void addStudent_OK_oneLetterNameTest() {
        String testName = "A";

        assertDoesNotThrow(() -> service.saveStudent(VALID_ID, testName, VALID_GROUP));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), VALID_ID);
        assertEquals(addedStudent.getNume(), testName);
        assertEquals(addedStudent.getGrupa(), VALID_GROUP);
    }

    @Test
    public void addStudent_OK_twoLettersNameTest() {
        String testName = "Ab";

        assertDoesNotThrow(() -> service.saveStudent(VALID_ID, testName, VALID_GROUP));
        var addedStudentOptional = StreamSupport.stream(service.findAllStudents().spliterator(), false).filter(student -> student.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedStudentOptional::get);

        var addedStudent = addedStudentOptional.orElse(null);
        assertNotNull(addedStudent);

        assertEquals(addedStudent.getID(), VALID_ID);
        assertEquals(addedStudent.getNume(), testName);
        assertEquals(addedStudent.getGrupa(), VALID_GROUP);
    }
}
