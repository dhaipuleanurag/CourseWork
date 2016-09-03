package assignment1.addressbook;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;

/**
 * The AddressBook class is meant to store information of users in the form of name, email address, phone number
 * and notes. This class provides with APIs to add, remove and search for a contact. There are API to store and 
 * retrieve a address book from a file.
 * @author Anurag
 */
public class AddressBook {
  private ArrayList<Contact> allContacts = new ArrayList<Contact>();
	
  /***
   * Function to add a new contact to the address book.  
   * @param contact Contact to be added. 
   * @return Boolean value based on success of operation.
   */
  public boolean add(Contact contact){
    if(contact == null){
      throw new IllegalArgumentException("contact cannot be null.");
    }
    return allContacts.add(contact);
  }
	
  /***
   * Function to remove a contact from address book. Finds a contact that matches all the field of 
   * the contact to be deleted and removes its first occurrence from the address book.
   * @param contact Contact to be removed.
   * @return Boolean value based on success of operation.
   */	
  public boolean remove(Contact contact){
    if(contact == null){
      throw new IllegalArgumentException("contact cannot be null.");
    }
    int indexOfElementToBeRemoved = -1;
    for(int i = 0; i < allContacts.size(); i++){
      if(contact.equals(allContacts.get(i))){
        indexOfElementToBeRemoved = i;
        break;
      }
    }
    // Implies contact not found.
    if(indexOfElementToBeRemoved == -1){
      return false;
    }
    else{
      allContacts.remove(indexOfElementToBeRemoved);
      return true;
    }
  }
	
  /***
   * Function to get a list of all contacts that matches a given search text. The function looks for
   * the given text in all the fields of contact. It is not case sensitive search.
   * @param searchText The text that needs to be matched to.
   * @return List of contacts that matched searchText.
   */
  public ArrayList<Contact> search(String searchText){
    ArrayList<Contact> result = new ArrayList<Contact>();
    for(Contact contact: allContacts){
      if(match(contact, searchText)){
        result.add(contact);
      }
    }
    return result;
  }
	
  /***
   * Function used to match if a given contact is right match for given text. All field are contact are
   * looked into.
   * @param contact Object of type Contact that is to be checked if it matches given text.
   * @param textToMatch The text that needs to be matched to.
   * @return Boolean value based on a match or not.
   */
  private boolean match(Contact contact,String textToMatch){
    String textToMatchLower = textToMatch.toLowerCase();
    // Appending text from all fields into one string and checking if it contains the text
    // to be matched.
    String contactInfo = contact.getName().toLowerCase() + " " + contact.getPhoneNumber().toLowerCase() + " " +
                  contact.getEmailAddress().toLowerCase() + " " + contact.getNotes().toLowerCase();
    return contactInfo.contains(textToMatchLower);
  }
  
  /***
   * Function to import contacts from a .json file.	
   * @param path Path to the file that contains address book information.
   * @return Boolean value indicating success of import of contact.
   */
  public boolean importContact(String path){
    Gson gson = new Gson();
    try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        //Reads the json file one line at a time and convert it to instance of Contact.        
    	Contact contact = gson.fromJson(line, Contact.class);
        allContacts.add(contact);
      }
      return true;
    } 
    catch (IOException e) {
      return false;
    }
  }
	
  /***
   * Function used to write contact information to a file.
   * @param path This parameter specifies the path to file where entries should be written. The file should be
   * in .json format.
   * @return Boolean value indicating if export was successful.
   */
  public boolean exportContacts(String path){
    Gson gson = new Gson();    
    try(FileWriter writer = new FileWriter(path.toString())){
      for(Contact contact: allContacts) {
        gson.toJson(contact, writer);
        writer.write("\n");
      }
      return true;
    } 
    catch(Exception e){
      return false;
    }
  }
}

