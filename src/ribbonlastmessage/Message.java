/*
 * This code is distributed under terms of GNU GPLv2.
 * *See LICENSE file.
 * ©UKRINFORM 2011-2012
 */

package ribbonlastmessage;

/**
 * Message object class
 * @author Stanislav Nepochatov
 */
public class Message {

    /**
    * Index of message
    */
    public String INDEX;

    /**
    * Message's directories
    */
    public String[] DIRS;

    /**
    * Header of message;
    */
    public String HEADER;

    /**
    * Date of message release;
    */
    public String DATE;

    /**
    * Author of message
    */
    public String AUTHOR;

    /**
    * Message's tags
    */
    public String[] TAGS;
    
    /**
    * Message content
    */
    public String CONTENT;
    
    /**
     * Csv line constructor
     * @param csvLine csv line from Ribbon protocol command
     */
    Message(String csvLine) {
        java.util.ArrayList<String[]> parsedStruct = csvHandlerLE.complexParseLine(csvLine, 4, 2);
        if (parsedStruct == null) {
            RibbonLastMessage.warningMessage("Неможливо прочитати структуру повідомлення!\n\n" + csvLine);
        } else {
            INDEX = parsedStruct.get(0)[0];
            HEADER = parsedStruct.get(0)[1];
            DATE = parsedStruct.get(0)[2];
            AUTHOR = parsedStruct.get(0)[3];
            DIRS = parsedStruct.get(1);
            TAGS = parsedStruct.get(2);
        }
    }
}
