package ssvv.example;

import domain.Nota;
import domain.Pair;
import domain.Student;
import domain.Tema;
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
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BigBangIntegrationTest {
    private Service service;

    @Before
    public void setUp() throws IOException {
        File studentFile = TestUtils.createXMLFile(TestUtils.STUDENT_TEST_XML_FILE);
        File temeFile = TestUtils.createXMLFile(TestUtils.HOMEWORK_TEST_XML_FILE);
        File noteFile = TestUtils.createXMLFile(TestUtils.GRADES_TEST_XML_FILE);

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(new StudentValidator(), studentFile.getPath());
        TemaXMLRepository fileRepository2 = new TemaXMLRepository(new TemaValidator(), temeFile.getPath());
        NotaXMLRepository fileRepository3 = new NotaXMLRepository(new NotaValidator(), noteFile.getPath());
        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @After
    public void tearDown() {
        new File(TestUtils.STUDENT_TEST_XML_FILE).delete();
        new File(TestUtils.HOMEWORK_TEST_XML_FILE).delete();
        new File(TestUtils.GRADES_TEST_XML_FILE).delete();
    }

    @Test
    public void addStudent_DB_Success() {
        assertDoesNotThrow(() -> service.saveStudent("1", "IONEL", 935));

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), TestUtils.STUDENT_TEST_XML_FILE);
        Iterable<Student> students = studentRepo.findAll();
        ArrayList<Student> studentList = new ArrayList<>();
        students.forEach(studentList::add);

        assertEquals(studentList.size(), 1);
        assertEquals(studentList.get(0).getID(), "1");
        assertEquals(studentList.get(0).getNume(), "IONEL");
        assertEquals(studentList.get(0).getGrupa(), 935);
    }

    @Test
    public void addAssignment_DB_Success() {
        assertDoesNotThrow(() -> service.saveTema("1", "DESCRIPTION", 12, 1));

        TemaXMLRepository assignmentRepo = new TemaXMLRepository(new TemaValidator(), TestUtils.HOMEWORK_TEST_XML_FILE);

        Iterable<Tema> assignments = assignmentRepo.findAll();
        ArrayList<Tema> assignmentList = new ArrayList<>();
        assignments.forEach(assignmentList::add);

        assertEquals(assignmentList.size(), 1);
        assertEquals(assignmentList.get(0).getID(), "1");
        assertEquals(assignmentList.get(0).getDescriere(), "DESCRIPTION");
        assertEquals(assignmentList.get(0).getDeadline(), 12);
        assertEquals(assignmentList.get(0).getStartline(), 1);
    }

    @Test
    public void addGrade_DB_Nonexistent_Fail() {
        assertThrows(RuntimeException.class, () -> service.saveNota("1", "1", 10, 5, "SLAB"));
    }

    @Test
    public void addAll_DB_Success() {
        assertDoesNotThrow(() -> service.saveStudent("1", "IONEL", 935));
        assertDoesNotThrow(() -> service.saveTema("1", "DESCRIPTION", 12, 1));
        assertDoesNotThrow(() -> service.saveNota("1", "1", 6, 11, "SLAB"));

        StudentXMLRepository studentRepo = new StudentXMLRepository(new StudentValidator(), TestUtils.STUDENT_TEST_XML_FILE);
        Iterable<Student> students = studentRepo.findAll();
        ArrayList<Student> studentList = new ArrayList<>();
        students.forEach(studentList::add);

        TemaXMLRepository assignmentRepo = new TemaXMLRepository(new TemaValidator(), TestUtils.HOMEWORK_TEST_XML_FILE);
        Iterable<Tema> assignments = assignmentRepo.findAll();
        ArrayList<Tema> assignmentList = new ArrayList<>();
        assignments.forEach(assignmentList::add);

        NotaXMLRepository gradeRepo = new NotaXMLRepository(new NotaValidator(), TestUtils.GRADES_TEST_XML_FILE);
        Iterable<Nota> grades = gradeRepo.findAll();
        ArrayList<Nota> gradeList = new ArrayList<>();
        grades.forEach(gradeList::add);

        assertEquals(studentList.size(), 1);
        assertEquals(studentList.get(0).getID(), "1");
        assertEquals(studentList.get(0).getNume(), "IONEL");
        assertEquals(studentList.get(0).getGrupa(), 935);

        assertEquals(assignmentList.size(), 1);
        assertEquals(assignmentList.get(0).getID(), "1");
        assertEquals(assignmentList.get(0).getDescriere(), "DESCRIPTION");
        assertEquals(assignmentList.get(0).getDeadline(), 12);
        assertEquals(assignmentList.get(0).getStartline(), 1);

        assertEquals(gradeList.size(), 1);
        assertEquals(gradeList.get(0).getID(), new Pair<>("1", "1"));
        assertEquals(gradeList.get(0).getNota(), 8.5, 0.01);
        assertEquals(gradeList.get(0).getSaptamanaPredare(), 11);
        assertEquals(gradeList.get(0).getFeedback(), "SLAB");
    }
}
