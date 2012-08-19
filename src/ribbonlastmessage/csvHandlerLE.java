/*
 * This code is distributed under terms of GNU GPLv2.
 * *See LICENSE file.
 * ©UKRINFORM 2011-2012
 */

package ribbonlastmessage;

/**
 * Parse messages commands
 * @author Stanislav Nepochatov
 */
public abstract class csvHandlerLE {
 
    /**
     * Notify main parser method about special chars<br><br>
     * <<b>Statuses:</b><br>
     * <b>0</b> : ordinary char<br>
     * <b>1</b> : comma separator<br>
     * <b>2</b> : solid begining<br>
     * <b>3</b> : solid ending<br>
     * <b>4</b> : group begining<br>
     * <b>5</b> : group ending<br>
     * <b>6</b> : ignore comma separator<br>
     * <b>7</b> : increase index command<br>
     * @param prevCh previos char
     * @param ch current char
     * @param nextCh next char
     * @return parse code status<br>
     */
    private static Integer parseMarker(char prevCh, char ch, char nextCh) {
        switch (ch) {
            case ',':
                if (((nextCh == '{') || (nextCh == '[')) && ((prevCh == '}') || (prevCh == ']'))) {
                    return 0;
                } else if ((prevCh == '}') || (prevCh == ']')) {
                    return 7;
                } else {
                    return 1;
                }
            case '{':
                return 2;
            case '}':
                return 3;
            case '[':
                return 4;
            case ']':
                return 5;
        }
        return 0;
    }
    
    /**
     * Find out if there is more separators
     * @param restOfLine rest of parsed line
     * @return true if rest of line contains at least one separator;
     */
    private static Boolean hasMoreSeparators(String restOfLine) {
        String[] separators = new String[] {",", "{", "}", "[", "]"};
        for (Integer sepIndex = 0; sepIndex < separators.length; sepIndex++) {
            if (restOfLine.contains(separators[sepIndex]) == true) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Complex parse line method (with groups support).<br>
     * Using to unify parse methodic.
     * @param inputLine line to parse
     * @param baseArrLength length of base fields in csv line
     * @param groupsCount count of additional arrays with groups parsed words
     * @return arraylist with string arrays of parsed words or null if parsing error occured
     * @since RibbonServer a2
     */
    public static java.util.ArrayList<String[]> complexParseLine(String inputLine, Integer baseArrLength, Integer groupsCount) {
        java.util.ArrayList<String[]> returnedArr = new java.util.ArrayList();
        String[] baseArray = new String[baseArrLength];
        java.util.ArrayList<String[]> tempGroupArray = new java.util.ArrayList();
        Integer beginSlice = 0;
        Integer acceptedIndex = -1;
        Boolean ignoreComma = false;
        for (Integer index = 0; index < inputLine.length(); index++) {
            char currChar = inputLine.charAt(index);
            char nextChar = '1';
            char prevChar = '1';
            if (index > 0) {
                prevChar = inputLine.charAt(index - 1);
            }
            if (index < inputLine.length() - 1) {
                nextChar = inputLine.charAt(index + 1);
            }
            switch (parseMarker(prevChar, currChar, nextChar)) {
                case 0:
                    continue;
                case 1:
                    if (ignoreComma == false) {
                        if (acceptedIndex < baseArrLength) {
                            baseArray[++acceptedIndex] = inputLine.substring(beginSlice, index);
                            beginSlice = index + 1;
                        } else {
                            return null;
                        }
                    }
                    break;
                case 2:
                    beginSlice = index + 1;
                    ignoreComma = true;
                    break;
                case 3:
                    if (ignoreComma == true) {
                        ignoreComma = false;
                        if (acceptedIndex < baseArrLength) {
                            baseArray[++acceptedIndex] = inputLine.substring(beginSlice, index);
                            beginSlice = index + 1;
                        } else {
                            return null;
                        }
                    }
                    break;
                case 4:
                    beginSlice = index + 1;
                    ignoreComma = true;
                    break;
                case 5:
                    if (ignoreComma == true) {
                        ignoreComma = false;
                        if (tempGroupArray.size() < groupsCount) {
                            tempGroupArray.add(inputLine.substring(beginSlice, index).split(","));
                        } else {
                            return null;
                        }
                        beginSlice = index + 1;
                    }
                    break;
                case 6:
                    ignoreComma = true;
                    break;
                case 7:
                    beginSlice = index + 1;
                    break;
            }
            if ((!hasMoreSeparators(inputLine.substring(index + 1))) && (index < inputLine.length() - 1)) {
                if (acceptedIndex < baseArrLength) {
                    baseArray[++acceptedIndex] = inputLine.substring(index + 1);
                } else {
                    return null;
                }
                break;
            }
        }
        returnedArr.add(baseArray);
        returnedArr.addAll(tempGroupArray);
        return returnedArr;
    }
    
    /**
     * Render given array to group format<br>
     * Format: <b>'[arr1,arr2,arr3,->arrN]'</b>
     * @param givenGroup group to render
     * @return rendered string 
     */
    public static String renderGroup(String[] givenGroup) {
        String returned = "[";
        for (Integer rIndex = 0; rIndex < givenGroup.length; rIndex++) {
            returned += givenGroup[rIndex];
            if (rIndex == givenGroup.length - 1) {
                returned += "]";
            } else {
                returned += ",";
            }
        }
        return returned;
    }
}
