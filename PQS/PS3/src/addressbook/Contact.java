package addressbook;


/**
 * Class containing the specifics of a contact. A Builder class is included
 * to allow optional arguments, and is the only way provided to create a Contact.
 * 
 * Includes a match method to determine whether the Contact matches a search term
 * pass as an argument. 
 * 
 * The getX() methods will return null if the attribute is not populated and 
 * can cause NullPointerExceptions if not checked.
 * 
 * @author William Brantley
 *
 */

public class Contact {
  /*
   * Parameters of a Contact. Each is of the type MatchableString to allow for
   * easy matching based on a partial search term.
   */
  private MatchableString name;
  private PhoneNumber phoneNumber;
  private MatchableString emailAddress;
  private MatchableString address;
  private MatchableString note;
  
  /**
   * Builder class for Contact. This allows the user to leave some elements blank. 
   * Use builder as: Contact.Builder().withName(...).withEmail(...).build() with the methods
   * for withEmail, withName, withNote, withAddress, and withPhoneNumber in any order,
   *  so long as there is at least one and no method is passed multiple times.
   * Attempting to build a contact with no attributes will throw an IllegalArgumentException 
   * since it doesn't make sense to include create a completely empty contact.
   */
  public static class Builder {
    private MatchableString name = null;
    private PhoneNumber phoneNumber = null;
    private MatchableString emailAddress = null;
    private MatchableString address = null;
    private MatchableString note = null;
    
    //Number of fields passed to Builder. If this field is 0, the entry is null and invalid
    private int fieldCount = 0;
    
    /**
     * Returns a Builder with the String name you pass as the name attribute.
     * @param name String of the name of the contact you are creating
     * @return Builder with the Name you passed in
     */
    public Builder withName(String name) {
      if (this.name != null) {
        throw new IllegalArgumentException("Multiple names passed to the Builder");
      }
      this.name = new MatchableString(name);
      fieldCount++;
      return this;
    }
    
    /**
     * Returns a Builder with the String email you pass as the emailAddress attribute.
     * @param email String of the email address of the contact you are creating
     * @return Builder with the email you passed in
     */
    public Builder withEmail(String email) {
      if (this.emailAddress != null) {
        throw new IllegalArgumentException("Multiple email addresses passed to the Builder");
      }
      this.emailAddress = new MatchableString(email);
      fieldCount++;
      return this;
    }
    
    /**
     * Returns a Builder with the String address you pass in as the Address attribute.
     * @param address String of the address of the contact you are creating
     * @return Builder with the address you passed in
     */
    public Builder withAddress(String address) {
      if (this.address != null) {
        throw new IllegalArgumentException("Multiple addresses passed to the Builder");
      }
      this.address = new MatchableString(address);
      fieldCount++;
      return this;
    }
    
    /**
     * Returns a Builder with the String phoneNumber you pass in as the phoneNumber attribute
     * @param phoneNumber String of the phone number of the contact you are creating
     * @return Builder with the phone number you passed in
     */
    public Builder withPhoneNumber(String phoneNumber) {
      if (this.phoneNumber != null) {
        throw new IllegalArgumentException("Multiple phone numbers passed to the Builder");
      }
      this.phoneNumber = new PhoneNumber(phoneNumber);
      fieldCount++;
      return this;
    }
    
    /**
     * Returns a Builder with the String note you pass in as the note attribute
     * @param note String containing a note about the contact you'd like to keep
     * @return Builder with the note attribute included
     */
    public Builder withNote(String note) {
      if (this.note != null) {
        throw new IllegalArgumentException("Multiple notes passed to the builder");
      }
      this.note = new MatchableString(note);
      fieldCount++;
      return this;
    }
    
    /**
     * Returns a Contact with the attributes included in the builder. Must be used at the 
     * end of the Builder chain to create a Contact. If followed my any other methods, the 
     * behavior is undefined.
     * 
     * @return Contact you built using the Builder methods
     * @throws IllegalArgumentException if the Contact you are creating has no attributes,
     * i.e. no Builder methods were passed to it, as in Contact.Builder().build(). 
     */
    public Contact build() {
      //Check the number of filled in arguments passed. We will throw an exception if
      //there are no arguments passed to the build method.
      if (fieldCount == 0) {
        throw new IllegalArgumentException("No arguments passed to the Builder method. Can't create"
            + " an empty entry.");
      } else {
        return new Contact(this);
      }
    }
  }
  
  /**
   * Private constructor for a Contact. The only way to create one is by using the Builder class
   * @param builder A Builder which creates a Contact based on the arguments passed to it.
   */
  private Contact(Builder builder) {
    name = builder.name;
    phoneNumber = builder.phoneNumber;
    emailAddress = builder.emailAddress;
    address = builder.address;
    note = builder.note;
  }
  
  /**
   * Returns whether an attribute of this Contact matches searchTerm passed to it.
   * Returns false if the attribute is null in this Contact.
   * 
   * @param attribute Specific attribute of the Contact where we are searching.
   * Defined in AddressBook.ContactAttribute.
   * @param searchTerm String of the term we are searching by.
   * @return boolean of whether the attribute of this Contact matches the search 
   * term argument
   */
  boolean match(AddressBook.ContactAttribute attribute, String searchTerm) {
    boolean matchFound = false;
    switch(attribute) {
      case NAME:
        matchFound = (name == null) ? false : name.match(searchTerm);
        break;
      case EMAIL:
        matchFound = (emailAddress == null) ? false : emailAddress.match(searchTerm);
        break;
      case PHONE:
        matchFound = (phoneNumber == null) ? false :phoneNumber.match(searchTerm);
        break;
      case ADDRESS:
        matchFound = (address == null) ? false : address.match(searchTerm);
        break;
      case NOTE:
        matchFound = (note == null) ? false : note.match(searchTerm);
        break;
    }
    return matchFound;
  }
  
  /** Returns the Name of this Contact, null if not present */
  public String getName() {
    return name.toString();
  }
  
  /** Returns the Email Address of this Contact, null if not present */
  public String getEmailAddress() {
    return emailAddress.toString();
  }
  
  /** Returns the Phone Number of this Contact, null if not present */
  public String getPhoneNumber() {
    return phoneNumber.toString();
  }
  
  /** Returns the Address of this Contact, null if not present */
  public String getAddress() {
    return address.toString();
  }
  
  /** Returns the Note associated with this Contact, null if not present */
  public String getNote() {
    return note.toString();
  }
  
  @Override public String toString() {
    StringBuilder contactInfo = new StringBuilder();
    if (name != null){
      contactInfo.append(name);
      contactInfo.append("\n");
    } if (emailAddress != null) {
      contactInfo.append(emailAddress);
      contactInfo.append("\n");
    } if (phoneNumber != null) {
      contactInfo.append(phoneNumber);
      contactInfo.append("\n");
    } if (address != null) {
      contactInfo.append(address);
      contactInfo.append("\n");
    } if (note != null) {
      contactInfo.append(note);
      contactInfo.append("\n");
    }
    return contactInfo.toString(); 
  }
}

/**
 * Most of the attributes of a Contact share a similar qualities:
 * They stored as a String, and they must be able to be searched for
 * approximately by a string. This is a small class that includes a 
 * match method which will return approximate searches.
 *
 */
class MatchableString {
  protected final String str;
  
  MatchableString (String matchable) {
    str = matchable;
  }
  
  /**
   * Returns true if the search term being searched for approximately 
   * matches this MatchableString. By approximately, we simply lowercase
   * both the search string and this string, and see if the search term
   * is a substring of this string.
   * 
   * @param searchTerm String you are searching for in this MatchableString
   * @return whether the string gives a match for this string
   */
  boolean match(String searchTerm) {
    String sanitizedSearchTerm = searchTerm.toLowerCase();
    
    return str.toLowerCase()
        .contains(sanitizedSearchTerm);
  }
  
  @Override public String toString() {
    return str;
  }
}

/**
 * MatchableString works for most of the attributes, but won't work correctly for
 * Phone Numbers. If the number stored is 212-2222 and the search term is 2122222.
 *  For this, we subclass MatchableString, and override the match method.
 *
 */
class PhoneNumber extends MatchableString {
  
  PhoneNumber(String phoneNumber) {
    super(phoneNumber);
  }
  
  /**
   * Takes a string, removes all non-decimal characters and returns 
   * a String with those characters removed. Helper method for match.
   * 
   * @param number phoneNumber String you want to sanitize
   */
  private String sanitizeNumber(String number) {
    return number.replaceAll("[^\\d]", "");
  }
  
  /**
   * Returns true if the sanitized input string is a substring of the 
   * sanitized stored string.
   * @param searchTerm Phone Number input you are searching for
   * @return whether the sanitized input provides a match for this string
   */
  @Override boolean match(String searchTerm) {    
    String sanitizedInput = sanitizeNumber(searchTerm);
    
    return sanitizeNumber(str).contains(sanitizedInput);
  }
}