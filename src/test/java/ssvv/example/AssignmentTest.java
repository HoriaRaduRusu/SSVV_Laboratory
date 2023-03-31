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
import java.io.IOException;
import java.util.stream.StreamSupport;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class AssignmentTest{

    private static final String VALID_ID = "1";
    private static final String VALID_DESCRIPTION = "description";
    private static final Integer VALID_DEADLINE = 14;
    private static final Integer VALID_STARTLINE = 1;

    private static final String ID_ERROR_MESSAGE = "ID invalid! \n";

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
    public void addAssignment_OK() {
        assertDoesNotThrow(() -> service.saveTema(VALID_ID, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE));
        var addedAssignmentOptional = StreamSupport.stream(service.findAllTeme().spliterator(), false).filter(assignment -> assignment.getID().equals(VALID_ID)).findFirst();
        assertDoesNotThrow(addedAssignmentOptional::isPresent);

        var addedAssignment = addedAssignmentOptional.orElse(null);
        assertNotNull(addedAssignment);

        assertEquals(addedAssignment.getID(), VALID_ID);
        assertEquals(addedAssignment.getDescriere(), VALID_DESCRIPTION);
        assertEquals(addedAssignment.getDeadline(), VALID_DEADLINE.intValue());
        assertEquals(addedAssignment.getStartline(), VALID_STARTLINE.intValue());
    }

    @Test
    public void addAssignment_Fail_InvalidId() {
        assertThrows(ValidationException.class, () -> service.saveTema(null, VALID_DESCRIPTION, VALID_DEADLINE, VALID_STARTLINE), ID_ERROR_MESSAGE);
    }


}
