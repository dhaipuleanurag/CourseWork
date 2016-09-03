package assignment1.addressbook;

/***
 * This class is meant to store contact information of a user. The contact information is in the
 * form of name, e-mail address, phone number and notes. 
 * The class makes use of builder patter and objects are instantiated using the build function of  
 * static class named Builder. The Builder class is also provided by different API with which 
 * different fields of a contact can be set. None of the fields in Contact are mandatory.
 * A typical instantiation of Contact would look like: 
 * new Contact.Builder().setName("Anurag").setEmailAddress("anurag@gmail.com").setPhoneNumber("987").build(); 
 * @author Anurag
 */
public class Contact {
  private final String name;
  private final String phoneNumber;
  private final String emailAddress;
  private final String notes;

  /***
   * The Builder class is a static class that is meant to serve a typical builder class in a Builder pattern.
   * With different API for setting, different fields can be set and finally a build function that returns an
   * instance of Contact.
   * @author Anurag
   */
  public static class Builder{
    private String name = "";
    //Make sure you do validations also
    private String phoneNumber = "";
    private String emailAddress = "";
    private String notes = "";
	
    /***
     * Function to set name field of a Contact. Throws IllegalArgumentException if argument is null.
     * @param name Name of user.
     * @return Builder class reference.
     */
    public Builder setName(String name){
      if(name == null){
        throw new IllegalArgumentException("name cannot be null.");
      }
      this.name = name;
      return this;
    }
    
    /***
     * Function to set phone number field of a Contact. Throws NumberFormatException 
     * if argument is not a number. 
     * @param phoneNumber Phone Number of user. The parameter should be a number without '(', '-' or any other 
     * character.
     * @return Builder class reference.
     */
    public Builder setPhoneNumber (String phoneNumber){
      // Checks if the phoneNumber is a number. If not, throw NumberFormatException.
      try{
        Integer.parseInt(phoneNumber);
      }
      catch(NumberFormatException e){
        throw e;
      }
      this.phoneNumber = phoneNumber;
      return this;
    }
    
    /***
     * Function to set e-mail address field of a Contact. Throws IllegalArgumentException if argument is null.
     * @param emailAddress e-mail address of user.
     * @return Builder class reference.
     */
    public Builder setEmailAddress(String emailAddress){
      if(emailAddress == null){
        throw new IllegalArgumentException("email Address cannot be null.");
      }
      this.emailAddress = emailAddress;
      return this;
    }
		
    /***
     * Function to set notes field of a Contact. This can be any miscellaneous information
     * about user. Throws IllegalArgumentException if argument is null. 
     * @param notes Any miscellaneous information about user.
     * @return Builder class reference.
     */
    public Builder notes(String notes){
      if(notes == null){
        throw new IllegalArgumentException("notes cannot be null.");
      }
      this.notes = notes;
      return this;
    }
    
    /***
     * Function to get an instance of Contact class. The contact object is build based on the fields of
     * static Builder class.
     * @return An instance of Contact class.
     */
    public Contact build(){
      return new Contact(this);
    }
  }
		
  private Contact(Builder builder){
    this.name = builder.name;
    this.emailAddress = builder.emailAddress;
    this.phoneNumber = builder.phoneNumber;
    this.notes = builder.notes;
  }
  
  /***
   * Function to return name field of contact.
   * @return name field of contact.
   */ 
  public String getName(){
  	return name;
  }
  
  /***
   * Function to return phone number.
   * @return phone number field from contact.
   */
  public String getPhoneNumber(){
    return phoneNumber;
  }
  
  /***
   * Function to get emailAddress field of contact.
   * @return email Address field of contact.
   */
  public String getEmailAddress(){
    return emailAddress;
  }
  
  /***
   * Function to get notes field of contact.
   * @return notes field of contact.
   */
  public String getNotes(){
    return notes;	
  }
  
  /***
   * Auto-generated code to return hashCode based on emailAddress, name, notes and phoneNumber
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((notes == null) ? 0 : notes.hashCode());
    result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
    return result;
  }

  /***
   * Checks if given Contact object has fields same as the current object.
   * @param other Object to be compared with.
   * @return Boolean value indicating equality. 
   */
  @Override
  public boolean equals(Object other){
    Contact otherContact = (Contact)other;
    if (this == other)
		return true;
	if (other == null)
		return false;
	if (getClass() != other.getClass())
		return false;
	return this.name == otherContact.name && 
        this.phoneNumber == otherContact.phoneNumber &&
        this.emailAddress == otherContact.emailAddress &&
        this.notes == otherContact.notes;
  }
}
