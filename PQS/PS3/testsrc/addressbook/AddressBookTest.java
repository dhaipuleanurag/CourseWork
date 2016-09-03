package addressbook;

import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import addressbook.AddressBook.ContactAttribute;


public class AddressBookTest {
  
  @Test
  public void addContactTest() {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    assertTrue(addressBook.addContact(contact1));
    assertEquals(contact1, addressBook.getAllContacts().get(0));
  }
  
  @Test
  public void addMultipleContactTest() {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
    	      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
    	      withName("nameToTest2").build();
    	    addressBook.addContact(contact2);
    assertEquals(2, addressBook.getAllContacts().size());
  }
  
  @Test
  public void emptyAddressBookTest()
  {
    AddressBook addressBook = new AddressBook();
    assertEquals(0, addressBook.getAllContacts().size());
  }
  
  @Test(expected = FileNotFoundException.class)
  public void emptyAddressBookWithWrongFileTest() throws FileNotFoundException, IOException
  {
    AddressBook addressBook = new AddressBook("someFile");
    assertEquals(0, addressBook.getAllContacts().size());
  }
  
  @Test(expected = NullPointerException.class)
  public void emptyAddressBookWithNullFileTest() throws FileNotFoundException, IOException
  {
    AddressBook addressBook = new AddressBook(null);
  }
  
  @Test(expected = NullPointerException.class)
  public void addNullContactTest() {
    AddressBook addressBook = new AddressBook();
    // Adding null contact should throw NullPointerException.
    addressBook.addContact(null);
  }
  
  @Test
  public void removeFromEmptyAddressBookTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact = new Contact.Builder().withPhoneNumber("phoneNumberToTest").build();
    assertFalse(addressBook.removeContact(contact));
  }
  
  @Test
  public void removeContactTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact = new Contact.Builder().withPhoneNumber("phoneNumberToTest").build();
    addressBook.addContact(contact);
    assertTrue(addressBook.removeContact(contact));
    assertEquals(0, addressBook.getAllContacts().size());
  }
  
  @Test
  public void removeMultipleContactsTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
      addressBook.addContact(contact2);
    assertEquals(2, addressBook.getAllContacts().size());
    addressBook.removeContact(contact1);
    addressBook.removeContact(contact2);
    assertEquals(0, addressBook.getAllContacts().size());
  }
  
  @Test(expected = NullPointerException.class)
  public void removeNullTest()
  {
	AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
	// Removing a null contact should throw NullPointerException.
    addressBook.removeContact(null);	    
  }
  
  @Test
  public void searchAddressBookTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
    addressBook.addContact(contact2);
    Contact contact3 = new Contact.Builder().withPhoneNumber("phoneNumberToTest3").
      withNote("noteToTest3").withAddress("addressToTest3").withEmail("emailAddress3").
      withName("nameToTest3").build();
    addressBook.addContact(contact3);
    Contact contact4 = new Contact.Builder().withPhoneNumber("phoneNumberToTest4").
      withNote("...").withAddress("addressToTest4").withEmail("emailAddress4").
      withName("nameToTest4").build();
    addressBook.addContact(contact4);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.NOTE, "noteToTest");
    assertEquals(3, results.size());
  }
  
  @Test
  public void searchEmptyAddressBookTest()
  {
    AddressBook addressBook = new AddressBook();
    ArrayList<Contact> results = addressBook.search(ContactAttribute.NAME, "name");
    assertEquals(0, results.size());
  }
  
  @Test
  public void searchAddressBookWithNoMatchTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
    addressBook.addContact(contact2);
    Contact contact3 = new Contact.Builder().withPhoneNumber("phoneNumberToTest3").
      withNote("noteToTest3").withAddress("addressToTest3").withEmail("emailAddress3").
      withName("nameToTest3").build();
    addressBook.addContact(contact3);
    Contact contact4 = new Contact.Builder().withPhoneNumber("phoneNumberToTest4").
      withNote("noteToTest4").withAddress("addressToTest4").withEmail("emailAddress4").
      withName("nameToTest4").build();
    addressBook.addContact(contact4);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.ADDRESS, "won't match");
    assertEquals(0, results.size());
  }
  
 @Test
  public void searchByNameTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.NAME, "name");
    assertEquals(1, results.size());
  }
  
  @Test
  public void searchByAddressTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.ADDRESS, "address");
    assertEquals(1, results.size());
  }
  
  @Test
  public void searchByPhoneNumberTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.PHONE, "phoneNumber");
    assertEquals(1, results.size());
  }
  
  @Test
  public void searchByNoteTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.NOTE, "note");
    assertEquals(1, results.size());
  }
  
  @Test
  public void searchAddressBookWithAllMatchTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
    addressBook.addContact(contact2);
    Contact contact3 = new Contact.Builder().withPhoneNumber("phoneNumberToTest3").
      withNote("noteToTest3").withAddress("addressToTest3").withEmail("emailAddress3").
      withName("nameToTest3").build();
    addressBook.addContact(contact3);
    Contact contact4 = new Contact.Builder().withPhoneNumber("phoneNumberToTest4").
      withNote("noteToTest4").withAddress("addressToTest4").withEmail("emailAddress4").
      withName("nameToTest4").build();
    addressBook.addContact(contact4);
    ArrayList<Contact> results = addressBook.search(ContactAttribute.PHONE, "phoneNumber");
    assertEquals(4, results.size());
  }
  
  @Test
  public void searchPhoneNumberSavedWithDashes()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("98-98-98").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1); 
    ArrayList<Contact> results = addressBook.search(ContactAttribute.PHONE, "9898");
    assertEquals(1, results.size());
  }
  
  @Test
  public void saveEmptyAddressBookTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();  
    File temp = File.createTempFile("contacts", ".gson");
    addressBook.save(temp.toString());
  }
  
  @Test
  public void saveAddressBookTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    File temp = File.createTempFile("contacts", ".gson");
    addressBook.save(temp.toString());
  }
  
  @Test
  public void saveAddressBookMultipleContactTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
    addressBook.addContact(contact2);
    File temp = File.createTempFile("contacts", ".gson");
    addressBook.save(temp.toString());
  }
  
  @Test(expected = NullPointerException.class)
  public void saveAddressBookNullFilePathTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    addressBook.save(null);
  }
  
  @Test
  public void loadEmptyAddressBookTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();  
    File temp = File.createTempFile("contacts", ".gson");
    addressBook.save(temp.toString());
    AddressBook newAddressBook = new AddressBook(temp.toString());
    assertEquals(0, newAddressBook.getAllContacts().size());
  }
  
  @Test
  public void loadAddressBookTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    File temp = File.createTempFile("contacts", ".gson");
    addressBook.save(temp.toString());
    AddressBook newAddressBook = new AddressBook(temp.toString());
    assertEquals(1, newAddressBook.getAllContacts().size());

  }
  
  @Test
  public void loadAddressBookMultipleContactTest() throws IOException
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
    addressBook.addContact(contact2);
    File temp = File.createTempFile("contacts", ".gson");
    addressBook.save(temp.toString());
    AddressBook newAddressBook = new AddressBook(temp.toString());
    assertEquals(2, newAddressBook.getAllContacts().size());
  }
  
  @Test
  public void toStringTest()
  {
    AddressBook addressBook = new AddressBook();
    Contact contact1 = new Contact.Builder().withPhoneNumber("phoneNumberToTest").
      withNote("noteToTest").withAddress("addressToTest").withEmail("emailAddress").
      withName("nameToTest").build();
    addressBook.addContact(contact1);
    Contact contact2 = new Contact.Builder().withPhoneNumber("phoneNumberToTest2").
      withNote("noteToTest2").withAddress("addressToTest2").withEmail("emailAddress2").
      withName("nameToTest2").build();
    addressBook.addContact(contact2);
    String expectedString = "nameToTest"
    		+ "\nemailAddress"
    		+ "\nphoneNumberToTest"
    		+ "\naddressToTest"
    		+ "\nnoteToTest"
    		+ "\n\nnameToTest2"
    		+ "\nemailAddress2"
    		+ "\nphoneNumberToTest2"
    		+ "\naddressToTest2"
    		+ "\nnoteToTest2"
    		+ "\n\n";
    assertEquals(expectedString, addressBook.toString());
  }
}
