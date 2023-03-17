package ssvv.example;

import domain.Nota;
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
import validation.Validator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class StudentTest {

    private static final String VALID_ID = "19";
    private static final String VALID_NAME = "NumeValid";
    private static final int LOW_GROUP = 101;
    private static final int VALID_GROUP = 111;
    private static final int HIGH_GROUP = 1000;


    private Validator<Student> studentValidator;
    private Validator<Tema> temaValidator;
    private Validator<Nota> notaValidator;
    private StudentXMLRepository fileRepository1;
    private TemaXMLRepository fileRepository2;
    private NotaXMLRepository fileRepository3;
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
        File studentFile = createXMLFile("studentitest.xml");
        File temeFile = createXMLFile("temetest.xml");
        File noteFile = createXMLFile("notetest.xml");

        studentValidator = new StudentValidator();
        temaValidator = new TemaValidator();
        notaValidator = new NotaValidator();
        fileRepository1 = new StudentXMLRepository(studentValidator,studentFile.getPath());
        fileRepository2 = new TemaXMLRepository(temaValidator, temeFile.getPath());
        fileRepository3 = new NotaXMLRepository(notaValidator, noteFile.getPath());
        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @After
    public void tearDown() {
        File studentFile = new File("studentitest.xml");
        studentFile.delete();
        File temeFile = new File("temetest.xml");
        temeFile.delete();
        File noteFile = new File("notetest.xml");
        noteFile.delete();
    }


    @Test
    public void addStudent_groupTooLow() {
       assertEquals(1, service.saveStudent(VALID_ID, VALID_NAME, LOW_GROUP));
    }

    @Test
    public void addStudent_OK() {
        assertEquals(0, service.saveStudent(VALID_ID, VALID_NAME, VALID_GROUP));
    }

    @Test
    public void addStudent_groupTooHigh() {
        assertEquals(1, service.saveStudent(VALID_ID, VALID_NAME, HIGH_GROUP));
    }

}
