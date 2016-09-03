package addressbook;

import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class ContactTest {
  
  @Test
  public void nameIncludedTest()
  {
    String nameToTest = "Bob";
    Contact contact = new Contact.Builder().withName(nameToTest).build();
    assertEquals(contact.getName(), nameToTest);
  }
  
  @Test(expected = NullPointerException.class)
  public void nameNullTest()
  {
    String nameToTest = null;
    // Building contact with null as note should be invalid.
    Contact contact = new Contact.Builder().withName(nameToTest).build();
    assertEquals(contact.getName(), nameToTest);  
  }

  @Test
  public void phoneNumberIncludedTest()
  {
    String phoneNumberToTest = "1234567890";
    Contact contact = new Contact.Builder().withPhoneNumber(phoneNumberToTest).build();
    assertEquals(contact.getPhoneNumber(), phoneNumberToTest);
  }
  
  @Test(expected = NullPointerException.class)
  public void phoneNumberNullTest()
  {
    String phoneNumberToTest = null;
    // Building contact with null as note should be invalid.
    Contact contact = new Contact.Builder().withPhoneNumber(phoneNumberToTest).build();
    assertEquals(contact.getPhoneNumber(), phoneNumberToTest);
  }
  
  @Test
  public void emailAddressIncludedTest()
  {
    String emailAddressToTest = "abc@gmail.com";
    Contact contact = new Contact.Builder().withEmail(emailAddressToTest).build();
    assertEquals(contact.getEmailAddress(), emailAddressToTest);
  }
  
  @Test(expected = NullPointerException.class)
  public void emailAddressNullTest()
  {
    String emailAddressToTest = null;
    // Building contact with null as note should be invalid.
    Contact contact = new Contact.Builder().withEmail(emailAddressToTest).build();
    assertEquals(contact.getEmailAddress(), emailAddressToTest);
  }
  
  @Test
  public void addressIncludedTest()
  {
    String addressToTest = "12, Some Street, Some City";
    Contact contact = new Contact.Builder().withAddress(addressToTest).build();
    assertEquals(contact.getAddress(), addressToTest);
  }
  
  @Test(expected = NullPointerException.class)
  public void addressNullTest()
  {
    String addressToTest = null;
    // Building contact with null as address should be invalid.
    Contact contact = new Contact.Builder().withAddress(addressToTest).build();
    assertEquals(contact.getAddress(), addressToTest);
  }

  @Test
  public void noteIncludedTest()
  {
    String noteToTest = "notes";
    Contact contact = new Contact.Builder().withNote(noteToTest).build();
    assertEquals(contact.getNote(), noteToTest);
  }
  
  @Test(expected = NullPointerException.class)
  public void noteNullTest()
  {
    String noteToTest = null;
    // Building contact with null as note should be invalid.
    Contact contact = new Contact.Builder().withNote(noteToTest).build();
    assertEquals(contact.getNote(), noteToTest);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void emptyFieldTest()
  {
    Contact contact = new Contact.Builder().build();
  }
  
  @Test
  public void multipleFieldsSetTest()
  {
    String noteToTest = "notes";
    String addressToTest = "address";
    Contact contact = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).build();
    assertEquals(contact.getNote(), noteToTest);
    assertEquals(contact.getAddress(), addressToTest);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void multipleTimesNameFieldSetTest()
  {
    Contact contact = new Contact.Builder().withName("FieldValue").
      withName("FieldValue").build();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void multipleTimesAddressFieldSetTest()
  {
    Contact contact = new Contact.Builder().withAddress("FieldValue").
      withAddress("FieldValue").build();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void multipleTimesEmailAddressFieldSetTest()
  {
    Contact contact = new Contact.Builder().withEmail("FieldValue").
      withEmail("FieldValue").build();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void multipleTimesNoteFieldSetTest()
  {
    Contact contact = new Contact.Builder().withNote("FieldValue").
      withNote("FieldValue").build();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void multipleTimesPhoneNumberFieldSetTest()
  {
    Contact contact = new Contact.Builder().withPhoneNumber("FieldValue").
      withPhoneNumber("FieldValue").build();
  }
  
  @Test
  public void getNameFieldNotSetTest()
  {
    String noteToTest = "notes";
    String addressToTest = "address";
    Contact contact = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).build();
	// getName() should return null but throws NullPointerException.
    try {
      assertEquals(contact.getName(), null);
	  }
	  catch(NullPointerException exception) {
	    fail();
	  }
  }
  
  @Test
  public void getAddressFieldNotSetTest()
  {
    String noteToTest = "notes";
    Contact contact = new Contact.Builder().withNote(noteToTest).
      build();
    // getAddress() should return null but throws NullPointerException.
    try {
      assertEquals(contact.getAddress(), null);
	  }
	  catch(NullPointerException exception) {
	    fail();
	  }
  }
  
  @Test
  public void getPhoneNumberFieldNotSetTest()
  {
    String noteToTest = "notes";
    String addressToTest = "address";
    Contact contact = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).build();
    // getPhoneNumber() should return null but throws NullPointerException.
    try {
	  assertEquals(contact.getPhoneNumber(), null);
    }
    catch(NullPointerException exception) {
      fail();
    }
  }
  
  @Test
  public void getNoteFieldNotSetTest()
  {
    String addressToTest = "address";
    Contact contact = new Contact.Builder().
      withAddress(addressToTest).build();
    // getNote() should return null but throws NullPointerException.
    try {
	    assertEquals(contact.getNote(), null);
	  }
	  catch(NullPointerException exception) {
	    fail();
	  }
  }
  
  @Test
  public void getEmailAddressFieldNotSetTest()
  {
    String noteToTest = "notes";
    String addressToTest = "address";
    Contact contact = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).build();
    // getEmailAddress() should return null but throws NullPointerException.
    try {
	    assertEquals(contact.getEmailAddress(), null);
	  }
    catch(NullPointerException exception) {
      fail();
	  }
  }
  
  @Test
  public void multipleTimesContactCreateTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    Contact contact1 = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    nameToTest = "name2";
    Contact contact2 = new Contact.Builder().withName(nameToTest).build();
    assertEquals(contact2.getName(), nameToTest);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void multipleTimesContactCreateWithEmptyContactTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    Contact contact1 = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    Contact contact2 = new Contact.Builder().build();
  }
  
  @Test
  public void matchAttributeNameMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    Contact contact1 = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertTrue(contact1.match(AddressBook.ContactAttribute.NAME, "name1"));
  }
  
  @Test
  public void matchAttributeNameDoesntMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    Contact contact1 = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertFalse(contact1.match(AddressBook.ContactAttribute.NAME, "bob"));
  }
  
  @Test
  public void matchAttribueEmailMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    Contact contact1 = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertTrue(contact1.match(AddressBook.ContactAttribute.EMAIL, "EmailAddress1"));
  }
  
  @Test
  public void matchAttribueEmailDoesntMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    Contact contact1 = new Contact.Builder().withNote(noteToTest).
      withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertFalse(contact1.match(AddressBook.ContactAttribute.EMAIL, "abc@g.com"));
  }
  
  @Test
  public void matchAttributePhoneMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    String phoneNumberToTest = "phoneNumber";
    Contact contact1 = new Contact.Builder().withPhoneNumber(phoneNumberToTest).
      withNote(noteToTest).withAddress(addressToTest).
      withEmail(emailAddress).withName(nameToTest).build();
    assertTrue(contact1.match(AddressBook.ContactAttribute.PHONE, "phone"));
  }
  
  @Test
  public void matchAttributePhoneDoesntMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    String phoneNumberToTest = "phoneNumber";
    Contact contact1 = new Contact.Builder().withPhoneNumber(phoneNumberToTest).
      withNote(noteToTest).withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertFalse(contact1.match(AddressBook.ContactAttribute.PHONE, "9876"));
  }
  
  @Test
  public void matchAttributeAddressMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    String phoneNumberToTest = "phoneNumber";
    Contact contact1 = new Contact.Builder().withPhoneNumber(phoneNumberToTest).
      withNote(noteToTest).withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertTrue(contact1.match(AddressBook.ContactAttribute.ADDRESS, "address1"));
  }
  
  @Test
  public void matchAttributeAddressDoesntMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    String phoneNumberToTest = "phoneNumber";
    Contact contact1 = new Contact.Builder().withPhoneNumber(phoneNumberToTest).
      withNote(noteToTest).withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertFalse(contact1.match(AddressBook.ContactAttribute.ADDRESS, "road1"));
  }
  
  @Test
  public void matchAttributeNoteMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    String phoneNumberToTest = "phoneNumber";
    Contact contact1 = new Contact.Builder().withPhoneNumber(phoneNumberToTest).
      withNote(noteToTest).withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertTrue(contact1.match(AddressBook.ContactAttribute.NOTE, "notes1"));
  }
  
  @Test
  public void matchAttributeNoteDoesntMatchTest()
  {
    String noteToTest = "notes1";
    String addressToTest = "address1";
    String nameToTest = "name1";
    String emailAddress = "EmailAddress1";
    String phoneNumberToTest = "phoneNumber";
    Contact contact1 = new Contact.Builder().withPhoneNumber(phoneNumberToTest).
      withNote(noteToTest).withAddress(addressToTest).withEmail(emailAddress).
      withName(nameToTest).build();
    assertFalse(contact1.match(AddressBook.ContactAttribute.NOTE, "..."));
  }
  
  @Test
  public void toStringTest()
  {
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
	String expectedString = "nameToTest"
      + "\nemailAddress"
      + "\nphoneNumberToTest"
      + "\naddressToTest"
      + "\nnoteToTest\n";
	assertEquals(expectedString, contact1.toString());
	}
}