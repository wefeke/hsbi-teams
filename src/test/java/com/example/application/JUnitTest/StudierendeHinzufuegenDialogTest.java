//package com.example.application.JUnitTest;
//
//import com.example.application.models.Teilnehmer;
//import com.example.application.models.User;
//import com.example.application.views.studierende.StudierendeHinzufuegenDialog;
//import com.example.application.services.TeilnehmerService;
//import com.example.application.security.AuthenticatedUser;
//import com.example.application.views.studierende.StudierendeView;
//import com.vaadin.flow.component.textfield.NumberField;
//import com.vaadin.flow.component.textfield.TextField;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.testng.annotations.Test;
//import org.mockito.Mockito;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//public class StudierendeHinzufuegenDialogTest {
//
//    private StudierendeHinzufuegenDialog dialog;
//    private TeilnehmerService teilnehmerService;
//    private AuthenticatedUser authenticatedUser;
//    private StudierendeView studierendeView;
//
//    @BeforeEach
//    public void setup() {
//        teilnehmerService = Mockito.mock(TeilnehmerService.class);
//        authenticatedUser = Mockito.mock(AuthenticatedUser.class);
//        studierendeView = Mockito.mock(StudierendeView.class);
//        dialog = new StudierendeHinzufuegenDialog(teilnehmerService, authenticatedUser, studierendeView);
//    }
//
//    @Test
//    public void testIsValidInputReturnsFalseWhenFieldsAreEmpty() {
//        TextField firstName = dialog.firstName;
//        TextField lastName = dialog.lastName;
//        NumberField matrikelNr = dialog.matrikelNr;
//
//        firstName.setValue("");
//        lastName.setValue("");
//        matrikelNr.setValue(null);
//
//        assertFalse(dialog.isValidInput());
//    }
//
//    @Test
//    public void testIsDuplicateMatrikelNrReturnsTrueWhenMatrikelNrExists() {
//        // Mock the TeilnehmerService to return a Teilnehmer when findByMatrikelNrAndUserId is called
//        Teilnehmer existingTeilnehmer = new Teilnehmer();
//        existingTeilnehmer.setId(1L);
//        Mockito.when(teilnehmerService.findByMatrikelNrAndUserId(Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Optional.of(existingTeilnehmer));
//
//        // Set the value of matrikelNr
//        dialog.matrikelNr.setValue(1234567.0);
//
//        // Assert that isDuplicateMatrikelNr returns true
//        Assertions.assertTrue(dialog.isDuplicateMatrikelNr());
//    }
//
//    @Test
//    public void testSaveTeilnehmerSavesTeilnehmerWhenInputIsValid() {
//        // Mock the TeilnehmerService to do nothing when saveTeilnehmer is called
//        Mockito.doNothing().when(teilnehmerService).saveTeilnehmer(Mockito.any(Teilnehmer.class), Mockito.any(User.class));
//
//        // Set the values of firstName, lastName, and matrikelNr
//        dialog.firstName.setValue("Test");
//        dialog.lastName.setValue("User");
//        dialog.matrikelNr.setValue(1234567.0);
//
//        // Call saveTeilnehmer
//        dialog.saveTeilnehmer();
//
//        // Verify that saveTeilnehmer was called on the TeilnehmerService
//        Mockito.verify(teilnehmerService, Mockito.times(1)).saveTeilnehmer(Mockito.any(Teilnehmer.class), Mockito.any(User.class));
//    }
//}
