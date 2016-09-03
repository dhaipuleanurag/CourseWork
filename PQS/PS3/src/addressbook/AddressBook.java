package addressbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This package provides a simple API for an Address Book.
 * An Address Book is a collection of Contacts. Each contact consists
 * of at least one of the following: name, email address, phone number,
 * address, or a note about the contact. 
 * 
 * AddressBooks can be saved to a file by invoked a.save(filePath) on
 * an AddressBook, and can be loaded by providing the filepath of a previously
 * saved AddressBook to a constructor.
 * 
 * There is minimal checking involved with the elements of a contact.
 * A user could input an emoji as a name or question marks as a phone number,
 * and there will be no exceptions throw. It is up to the user or application
 * to check these values. The iOS contacts application is presented in a similar
 * way.
 * 
 * Inputs the Address as a string and may not be suited for applications that
 * rely on a well formed address of Street1, Street2, City, State, Zip, Country, etc.
 * 
 * @author William Brantley
 *
 */

public class AddressBook {
  /*
   * Contact attributes used for searching the address book.
   * The search method will take one of these as a parameter.
   */
  public enum ContactAttribute { 
    NAME, EMAIL, PHONE, ADDRESS, NOTE
  }
  private ArrayList<Contact> contactDirectory;
  
  /**
   * Constructor used to create a new empty AddressBook object.
   */
  public AddressBook() {
    contactDirectory = new ArrayList<Contact>();
  }
  
  /***
   * Gets a copy of all contacts of Address Book.
   * @return
   */
  public ArrayList<Contact> getAllContacts()
  {
	ArrayList<Contact> Directory = new ArrayList<Contact>();  
    for(Contact contact: contactDirectory) {
    	Directory.add(contact);
    }
    return Directory;
  }
  
  /**
   * Constructor used to load a previously stored file. User must handle
   * FileNotFoundExceptions and IOExceptions. The filepath must be either
   * absolute or in the classpath. Using relative paths may throw a FileNotFoundException
   * 
   * @param filepath filepath for file. Maybe filename in classpath or absolute path
   * @throws IOException Thrown when an error occurs on reading the file
   * @throws FileNotFoundException Thrown when the file cannot be found through the passed
   * argument
   */
  public AddressBook(String filepath) throws IOException, FileNotFoundException {
    //Setup to read contents of filepath
    File inFile = new File(filepath);
    FileInputStream inputStream = new FileInputStream(inFile);
    InputStreamReader streamReader = new InputStreamReader(inputStream);
    BufferedReader buffReader = new BufferedReader(streamReader);
    StringBuilder json = new StringBuilder();
    String line;
    
    //Read all of the JSON file
    while ((line = buffReader.readLine()) != null) {
      json.append(line);
    }
    buffReader.close();
    
    //Convert the JSON file to an ArrayList and assign it as contactDirectory
    String formattedJSON = json.toString();
    Contact[] tempArray = new Gson().fromJson(formattedJSON, Contact[].class);
    contactDirectory = new ArrayList<>(Arrays.asList(tempArray));
  }
  
  /**
   * Adds contact to the Address Book and returns a boolean
   * for whether the Contact was successfully added or not.
   *  See javadoc for Contact.Builder for information on how to create a Contact. 
   *  
   *  @param newEntry new Contact to add to the address book
   *  @return boolean of the success or failure of adding to the address book
   */
  public boolean addContact(Contact newEntry) {
    return contactDirectory.add(newEntry);
  }
  
  /**
   * Removes the a contact from address book and returns a boolean
   * for whether the Contact was successfully removed or not.
   * 
   * @param entry Contact you want to delete from the address book
   * @return boolean of the success or failure of deleting the contact from the
   * address book
   */
  public boolean removeContact(Contact entry) {
    return contactDirectory.remove(entry);
  }
  
  /**
   * A search feature that allows you to search the address book by an attribute
   * and for a certain term. This method returns an ArrayList of the Contacts
   * that match the search term. The attributes are exposed as ContactAttributes and 
   * intuitively correspond to the attributes of each contact (NAME, EMAIL, PHONE,
   * ADDRESS, NOTE). For example, a.search(AddressBook.ContactAttribute.EMAIL, "nyu.edu") will
   * return all the contacts in the AddressBook, a, that have an NYU domain as their email.
   * 
   * The searchTerm does not have to exactly match an the contact, so if the user accidently
   * inputs the name "TOm", that Contact will be included if if searching for the name
   * "tom" or "Tom".
   *  
   *  If you give this method an empty string as the search term, it will return all
   *  the Contacts that have a user-specified value (even if that value is itself the 
   *  empty string). 
   *  
   *
   * @param attribute Specifier for which attribute of the contact you wish to search by.
   * Options included under AddressBook.ContactAttributes
   * @param searchTerm String for which you want to want to search the address book for
   * @return ArrayList containing all the contacts that provided a match. 
   */
  public ArrayList<Contact> search(ContactAttribute attribute, String searchTerm) {
    ArrayList<Contact> matchedContacts = new ArrayList<Contact>();
    for(Contact contact : contactDirectory) {
      if(contact.match(attribute, searchTerm)){
        matchedContacts.add(contact);
      }
    }
    return matchedContacts;
  }
  
  /**
   * Writes AddressBook to JSON object specified by filepath. The filepath must be in the
   * current classpath or an absolute path. This method throws IOException and FileNotFound,
   * which the user must handle.
   * 
   * @param filepath Absolute path or filename where you'd like to store this JSON object
   * @throws IOException Thrown when IOException is encountered when writing the file
   * @throws FileNotFoundException Thrown when Filepath can't be resolved
   */
  public void save(String filepath) throws IOException, FileNotFoundException {
    File outFile = new File(filepath);

    //Serialize contactDirectory to JSON object and store that as a String
    Gson gson = new GsonBuilder()
        .serializeNulls()
        .create();
    String json = gson.toJson(contactDirectory);

    //Write the JSON string to a file specified by filepath
    FileOutputStream outputStream = new FileOutputStream(outFile);
    OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);
    outWriter.append(json);
      
    outWriter.close();
    outputStream.close();
  }
  
  @Override public String toString() {
    StringBuilder allContacts = new StringBuilder();
    for (Contact contact: contactDirectory) {
      allContacts.append(contact);
      allContacts.append("\n");
    }
    return allContacts.toString();
  }
}
