package net.coderodde.javadb.cmd;

import java.util.Arrays;
import java.util.Objects;
import net.coderodde.javadb.TableCell;
import net.coderodde.javadb.TableRow;

final class Test {

    private static final String NULL_STR = "null";
    
    private String operandName1;
    private String operandName2;
    private TestOperation testOperation;
    
    Test(String operandName1, 
         String operandName2, 
         TestOperation testOperation) {
        Objects.requireNonNull(operandName1, "1st operand is null.");
        Objects.requireNonNull(operandName2, "2nd operand is null.");
        
        this.testOperation = Objects.requireNonNull(
                testOperation, "The test operation is null.");
        this.operandName1 = operandName1.trim().toLowerCase();
        this.operandName2 = operandName2.trim().toLowerCase();
    }
    
    boolean tableRowMatches(TableRow tableRow) {
        if (operandName1.equals(NULL_STR)) {
            return handleNull(tableRow);
        }
        
        TableCell cell1 = tableRow.get(operandName1);
        TableCell cell2 = tableRow.get(operandName2);
        
        if (cell1 == null && cell2 == null) {
            return tableRowMatchesLiterals(operandName1, operandName2);
        } else if (cell1 != null && cell2 != null) {
            if (!cell1.getTableCellType().equals(cell2.getTableCellType())) {
                throw new IllegalArgumentException(
                        "Comparing two attributes of different types. " +
                                "Type 1 is: " +
                                cell1.getTableCellType().getTypeName() + 
                                ", type 2 is: " + 
                                cell2.getTableCellType().getTypeName());
            }
            
            return tableRowMatches(cell1, cell2);
        } else if (cell1 == null) {
            return handleConstantAndCell(operandName1, cell2);
        } else {
            // Here cell1 != null and cell2 == null:
            return handleCellAndConstant(cell1, operandName2);
        }
    }
    
    private boolean tableRowMatches(TableCell cell1, TableCell cell2) {
        switch (testOperation) {
            case EQ:
                return cellsEqual(cell1, cell2);
                
            case NEQ:
                return !cellsEqual(cell1, cell2);
                
            case LT:
                return cellLess(cell1, cell2);
                
            case LEQ:
                return cellLess(cell1, cell2) || cellsEqual(cell1, cell2);
                
            case GT:
                return cellLess(cell2, cell1);
                
            case GEQ:
                return cellLess(cell2, cell1) || cellsEqual(cell1, cell2);
                
            default:
                throw new IllegalStateException("This should not be thrown.");
        }
    }
    
    private boolean cellsEqual(TableCell cell1, TableCell cell2) {
        switch (cell1.getTableCellType()) {
            case TYPE_INT:
                return cell1.getIntValue() == cell2.getIntValue();
                
            case TYPE_LONG:
                return cell1.getLongValue() == cell2.getLongValue();
                
            case TYPE_FLOAT:
                return cell1.getFloatValue() == cell2.getFloatValue();
                
            case TYPE_DOUBLE:
                return cell1.getDoubleValue() == cell2.getDoubleValue();
                
            case TYPE_BOOLEAN:
                return cell1.getBooleanValue() == cell2.getBooleanValue();
                
            case TYPE_STRING:
                return cell1.getStringValue().equals(cell2.getStringValue());
                
            case TYPE_BINARY:
                return Arrays.equals(cell1.getBinaryData(), 
                                     cell2.getBinaryData());
                
            default:
                throw new IllegalStateException("This should not be thrown.");
        }
    }
    
    private boolean cellLess(TableCell cell1, TableCell cell2) {
        switch (cell1.getTableCellType()) {
            case TYPE_INT:
                return cell1.getIntValue() < cell2.getIntValue();
                
            case TYPE_LONG:
                return cell1.getLongValue() < cell2.getLongValue();
                
            case TYPE_FLOAT:
                return cell1.getFloatValue() < cell2.getFloatValue();
                
            case TYPE_DOUBLE:
                return cell1.getDoubleValue() < cell2.getDoubleValue();
                
            case TYPE_BOOLEAN:
                throw new IllegalArgumentException(
                        "Less is not defined for boolean values.");
                
            case TYPE_STRING:
                return cell1.getStringValue()
                            .compareTo(cell2.getStringValue()) < 0;
                
            case TYPE_BINARY:
                throw new IllegalArgumentException(
                        "Less is not defined for binary objects.");
                
            default:
                throw new IllegalStateException("This should not be thrown.");
        }
    }

    private boolean tableRowMatchesLiterals(String operandName1, 
                                            String operandName2) {
        if (isInteger(operandName1)) {
            if (!isInteger(operandName2)) {
                throw new IllegalArgumentException(
                        "Comparing an integer with a non-integer value.");
            }
            
            int integer1 = convertToInt(operandName1);
            int integer2 = convertToInt(operandName2);
            
            switch (testOperation) {
                case EQ:
                    return integer1 == integer2;
                    
                case NEQ:
                    return integer1 != integer2;
                    
                case LT:
                    return integer1 < integer2;
                    
                case LEQ:
                    return integer1 <= integer2;
                    
                case GT:
                    return integer1 > integer2;
                    
                case GEQ:
                    return integer1 >= integer2;
                    
                default:
                    throw new IllegalStateException(
                            "This should not be thrown.");
            }
            
        } else if (isLong(operandName1)) {
            if (!isLong(operandName2)) {
                throw new IllegalArgumentException(
                        "Comparing a long with a non-long value.");
            }
            
            long long1 = convertToLong(operandName1);
            long long2 = convertToLong(operandName2);
            
            switch (testOperation) {
                case EQ:
                    return long1 == long2;
                    
                case NEQ:
                    return long1 != long2;
                    
                case LT:
                    return long1 < long2;
                    
                case LEQ:
                    return long1 <= long2;
                    
                case GT:
                    return long1 > long2;
                    
                case GEQ:
                    return long1 >= long2;
                    
                default:
                    throw new IllegalStateException(
                            "This should not be thrown.");
            }
        } 
        else if (isFloat(operandName1)) {
            if (!isFloat(operandName2)) {
                throw new IllegalArgumentException(
                        "Comparing a float with a non-float value.");
            }
                
            float float1 = convertToFloat(operandName1);
            float float2 = convertToFloat(operandName2);
            
            switch (testOperation) {
                case EQ:
                    return float1 == float2;
                    
                case NEQ:
                    return float1 != float2;
                    
                case LT:
                    return float1 < float2;
                    
                case LEQ:
                    return float1 <= float2;
                    
                case GT:
                    return float1 > float2;
                    
                case GEQ:
                    return float1 >= float2;
                    
                default:
                    throw new IllegalStateException(
                            "This should not be thrown.");
            }
        } else if (isDouble(operandName1)) {
          if (!isDouble(operandName2)) {
              throw new IllegalArgumentException(
                    "Comparing a double with a non-double value.");
          }  
          
          double double1 = convertToDouble(operandName1);
          double double2 = convertToDouble(operandName2);
          
          switch (testOperation) {
              case EQ:
                  return double1 == double2;
                  
              case NEQ:
                  return double1 != double2;
                  
              case LT:
                  return double1 < double2;
                  
              case LEQ:
                  return double1 <= double2;
                  
              case GT:
                  return double1 > double2;
                  
              case GEQ:
                  return double1 >= double2;
                  
              default:
                  throw new IllegalStateException(
                        "This should not be thrown.");
          }
        } else if (isBoolean(operandName1)) {
            boolean bool1 = convertToBoolean(operandName1);
            boolean bool2 = convertToBoolean(operandName2);
            
            switch (testOperation) {
                case EQ:
                    return bool1 == bool2;
                    
                case NEQ:
                    return bool1 != bool2;
                    
                case LT:
                case LEQ:
                case GT:
                case GEQ:
                    throw new IllegalStateException(
                            "Operation " + testOperation.name() + " is not " +
                                    "defined for a boolean value.");
                    
                default:
                    throw new IllegalStateException(
                            "This should not be thrown.");
            }
            
        } else {
            // Strings:
            switch (testOperation) {
                case EQ:
                    return operandName1.equals(operandName2);
                    
                case NEQ:
                    return !operandName1.equals(operandName2);
                    
                case LT:
                    return operandName1.compareTo(operandName2) < 0;
                    
                case LEQ:
                    return operandName1.compareTo(operandName2) <= 0;
                    
                case GT:
                    return operandName1.compareTo(operandName2) > 0;
                    
                case GEQ:
                    return operandName1.compareTo(operandName2) >= 0;
                    
                default:
                    throw new IllegalStateException(
                            "This should not be thrown.");
            }
        }
    }
    
    private static int convertToInt(String str) {
        str = str.trim().toLowerCase();
        
        int radix = 10;
        
        if (str.startsWith("0b")) {
            radix = 2;
        } else if (str.startsWith("0x")) {
            radix = 16;
        }
        
        return Integer.parseInt(str, radix);
    }
    
    private static long convertToLong(String str) {
        str = str.trim().toLowerCase();
        
        int radix = 10;
        
        if (str.startsWith("0b")) {
            radix = 2;
        } else if (str.startsWith("0x")) {
            radix = 16;
        }
        
        return Long.parseLong(str, radix);
    }
    
    private static float convertToFloat(String str) {
        return Float.parseFloat(str);
    }
    
    private static double convertToDouble(String str) {
        return Double.parseDouble(str);
    }
    
    private static boolean convertToBoolean(String str) {
        switch (str) {
            case "false":
                return false;
                
            case "true":
                return true;
                
            default:
                throw new IllegalArgumentException("\"" + str + "\": not a " +
                        "boolean literal.");
        }
    }
    
    private static boolean isInteger(String str) {
        str = str.trim().toLowerCase();
        
        int radix = 10;
        
        if (str.startsWith("0b")) {
            radix = 2;
        } else if (str.startsWith("0x")) {
            radix = 16;
        }
        
        try {
            Integer.parseInt(str, radix);
            return true;
        } finally {
            return false;
        }
    }
    
    private static boolean isLong(String str) {
        str = str.trim().toLowerCase();
        
        int radix = 10;
        
        if (str.startsWith("0b")) {
            radix = 2;
        } else if (str.startsWith("0x")) {
            radix = 16;
        }
        
        try {
            Long.parseLong(str, radix);
            return true;
        } finally {
            return false;
        }
    }
    
    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } finally {
            return false;
        }
    }
    
    private static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } finally {
            return false;
        }
    }
    
    private static boolean isBoolean(String str) {
        return str.equals("true") || str.endsWith("false");
    }
    
    private boolean handleNull(TableRow tableRow) {
        if (operandName1.equals(NULL_STR) && operandName2.equals(NULL_STR)) {
            switch (testOperation) {
                case EQ:
                    return true;
                    
                case NEQ:
                    return false;
                    
                default:
                    throw new IllegalArgumentException(
                            "Cannot compare two nulls with operation " + 
                                    testOperation);
            }
        }
        
        if (!operandName1.equals(NULL_STR)) {
            String tmp = operandName1;
            operandName1 = operandName2;
            operandName2 = tmp;
        }
        
        if (operandName1.equals(NULL_STR)) {
            // First operand is NULL, second is a value or column name:
            if (isInteger(operandName2)) {
                throw new IllegalArgumentException(
                        "Cannot compare NULL and integer value.");
            } else if (isLong(operandName2)) {
                throw new IllegalArgumentException(
                        "Cannot compare NULL and long value.");
            } else if (isFloat(operandName2)) {
                throw new IllegalArgumentException(
                        "Cannot compare NULL and float value.");
            } else if (isDouble(operandName2)) {
                throw new IllegalArgumentException(
                        "Cannot compare NULL and double vallue.");
            } else if (isBoolean(operandName2)) {
                throw new IllegalArgumentException(
                        "Cannot compare NULL and boolean value.");
            }
            
            TableCell cell = tableRow.get(operandName2);
            
            if (cell == null) {
                throw new IllegalArgumentException(
                        "\"" + operandName2 + "\": no such column.");
            }
            
            switch (testOperation) {
                case EQ:
                    return cell.getValue() == null;
                    
                case NEQ:
                    return cell.getValue() != null;
                    
                default:
                    throw new IllegalArgumentException(
                            "Cannot compare with " + testOperation.name() +
                            "NULL values and attributes.");
            }
        }
        
        throw new IllegalStateException("Should not get here.");
    }

    private boolean handleConstantAndCell(String operandName1, 
                                          TableCell cell2) {
        switch (cell2.getTableCellType()) {
            case TYPE_INT:
                if (!isInteger(operandName1)) {
                    throw new IllegalArgumentException(
                            "Integer literal expected here.");
                }
                
                int intConstant = convertToInt(operandName2);
                int intAttribute = cell2.getIntValue();
                
                switch (testOperation) {
                    case EQ:
                        return intConstant == intAttribute;
                        
                    case NEQ:
                        return intConstant != intAttribute;
                        
                    case LT:
                        return intConstant < intAttribute;
                        
                    case LEQ:
                        return intConstant <= intAttribute;
                        
                    case GT:
                        return intConstant > intAttribute;
                        
                    case GEQ:
                        return intConstant >= intAttribute;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_LONG:
                if (!isLong(operandName1)) {
                    throw new IllegalArgumentException(
                            "Long literal expected here.");
                }
                
                long longConstant = convertToLong(operandName2);
                long longAttribute = cell2.getLongValue();
                
                switch (testOperation) {
                    case EQ:
                        return longConstant == longAttribute;
                        
                    case NEQ:
                        return longConstant != longAttribute;
                        
                    case LT:
                        return longConstant < longAttribute;
                        
                    case LEQ:
                        return longConstant <= longAttribute;
                        
                    case GT:
                        return longConstant > longAttribute;
                        
                    case GEQ:
                        return longConstant >= longAttribute;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_FLOAT:
                if (!isFloat(operandName1)) {
                    throw new IllegalArgumentException(
                            "Float literal expected here.");
                }
                
                float floatConstant = convertToFloat(operandName2);
                float floatAttribute = cell2.getFloatValue();
                
                switch (testOperation) {
                    case EQ:
                        return floatConstant == floatAttribute;
                        
                    case NEQ:
                        return floatConstant != floatAttribute;
                        
                    case LT:
                        return floatConstant < floatAttribute;
                        
                    case LEQ:
                        return floatConstant <= floatAttribute;
                        
                    case GT:
                        return floatConstant > floatAttribute;
                        
                    case GEQ:
                        return floatConstant >= floatAttribute;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_DOUBLE:
                if (!isDouble(operandName1)) {
                    throw new IllegalArgumentException(
                            "Double literal expected here.");
                }
                
                double doubleConstant = convertToDouble(operandName2);
                double doubleAttribute = cell2.getDoubleValue();
                
                switch (testOperation) {
                    case EQ:
                        return doubleConstant == doubleAttribute;
                        
                    case NEQ:
                        return doubleConstant != doubleAttribute;
                        
                    case LT:
                        return doubleConstant < doubleAttribute;
                        
                    case LEQ:
                        return doubleConstant <= doubleAttribute;
                        
                    case GT:
                        return doubleConstant > doubleAttribute;
                        
                    case GEQ:
                        return doubleConstant >= doubleAttribute;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_BOOLEAN:
                if (!isBoolean(operandName2)) {
                    throw new IllegalArgumentException(
                            "Boolean literal expected here.");
                }
                    
                boolean booleanConstant = convertToBoolean(operandName2);
                boolean booleanAttribute = cell2.getBooleanValue();
                
                switch (testOperation) {
                    case EQ:
                        return booleanConstant == booleanAttribute;
                        
                    case NEQ:
                        return booleanConstant != booleanAttribute;
                        
                    case LT:
                    case LEQ:
                    case GT:
                    case GEQ:
                        throw new IllegalArgumentException(
                                "Operation " + testOperation + " is not " +
                                        "defined for boolean values.");
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_STRING:
                String stringAttribute = cell2.getStringValue();
                
                switch (testOperation) {
                    case EQ:
                        return operandName1.equals(stringAttribute);
                        
                    case NEQ:
                        return !operandName1.endsWith(stringAttribute);
                        
                    case LT:
                        return operandName1.compareTo(stringAttribute) < 0;
                        
                    case LEQ:
                        return operandName1.compareTo(stringAttribute) <= 0;
                        
                    case GT:
                        return operandName1.compareTo(stringAttribute) > 0;
                        
                    case GEQ:
                        return operandName1.compareTo(stringAttribute) >= 0;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                        
                }
                
            case TYPE_BINARY:
                throw new IllegalStateException("oh 2 fucek yeha");
                
            default:
                throw new IllegalStateException(
                        cell2.getTableCellType() + ": unknown cell type.");
        }
    }
    
    private boolean handleCellAndConstant(TableCell cell1, 
                                          String operandName2) {
        switch (cell1.getTableCellType()) {
            case TYPE_INT:
                if (!isInteger(operandName2)) {
                    throw new IllegalArgumentException(
                            "Integer literal expected here.");
                }
                
                int intConstant = convertToInt(operandName2);
                int intAttribute = cell1.getIntValue();
                
                switch (testOperation) {
                    case EQ:
                        return intAttribute == intConstant;
                        
                    case NEQ:
                        return intAttribute != intConstant;
                        
                    case LT:
                        return intAttribute < intConstant;
                        
                    case LEQ:
                        return intAttribute <= intConstant;
                        
                    case GT:
                        return intAttribute < intConstant;
                        
                    case GEQ:
                        return intAttribute >= intConstant;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_LONG:
                if (!isLong(operandName2)) {
                    throw new IllegalArgumentException(
                            "Long literal expected here.");
                }
                
                long longConstant = convertToLong(operandName2);
                long longAttribute = cell1.getLongValue();
                
                switch (testOperation) {
                    case EQ:
                        return longAttribute == longConstant;
                        
                    case NEQ:
                        return longAttribute != longConstant;
                        
                    case LT:
                        return longAttribute < longConstant;
                        
                    case LEQ:
                        return longAttribute <= longConstant;
                        
                    case GT:
                        return longAttribute < longConstant;
                        
                    case GEQ:
                        return longAttribute >= longConstant;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_FLOAT:
                if (!isFloat(operandName2)) {
                    throw new IllegalArgumentException(
                            "Float literal expected here.");
                }
                
                float floatConstant = convertToFloat(operandName2);
                float floatAttribute = cell1.getFloatValue();
                
                switch (testOperation) {
                    case EQ:
                        return floatAttribute == floatConstant;
                        
                    case NEQ:
                        return floatAttribute != floatConstant;
                        
                    case LT:
                        return floatAttribute < floatConstant;
                        
                    case LEQ:
                        return floatAttribute <= floatConstant;
                        
                    case GT:
                        return floatAttribute < floatConstant;
                        
                    case GEQ:
                        return floatAttribute >= floatConstant;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
            case TYPE_DOUBLE:
                if (!isDouble(operandName2)) {
                    throw new IllegalArgumentException(
                            "Float literal expected here.");
                }
                
                double doubleConstant = convertToDouble(operandName2);
                double doubleAttribute = cell1.getDoubleValue();
                
                switch (testOperation) {
                    case EQ:
                        return doubleAttribute == doubleConstant;
                        
                    case NEQ:
                        return doubleAttribute != doubleConstant;
                        
                    case LT:
                        return doubleAttribute < doubleConstant;
                        
                    case LEQ:
                        return doubleAttribute <= doubleConstant;
                        
                    case GT:
                        return doubleAttribute < doubleConstant;
                        
                    case GEQ:
                        return doubleAttribute >= doubleConstant;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_BOOLEAN:
                if (!isBoolean(operandName2)) {
                    throw new IllegalArgumentException(
                            "Boolean literal expected here.");
                }
                
                boolean booleanConstant = convertToBoolean(operandName2);
                boolean booleanAttribute = cell1.getBooleanValue();
                
                switch (testOperation) {
                    case EQ:
                        return booleanAttribute == booleanConstant;
                        
                    case NEQ:
                        return booleanAttribute != booleanConstant;
                        
                    case LT:
                    case LEQ:
                    case GT:
                    case GEQ:
                        throw new IllegalArgumentException(
                                "Operation " + testOperation + " is not " +
                                        "defined for boolean values.");
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " + 
                                testOperation);
                }
                
            case TYPE_STRING:
                String stringAttribute = cell1.getStringValue();
                
                switch (testOperation) {
                    case EQ:
                        return stringAttribute.equals(operandName2);
                        
                    case NEQ:
                        return stringAttribute.equals(operandName2);
                        
                    case LT:
                        return stringAttribute.compareTo(operandName2) < 0;
                        
                    case LEQ:
                        return stringAttribute.compareTo(operandName2) <= 0;
                        
                    case GT:
                        return stringAttribute.compareTo(operandName2) > 0;
                        
                    case GEQ:
                        return stringAttribute.compareTo(operandName2) >= 0;
                        
                    default:
                        throw new IllegalStateException("Unknown operation: " +
                                testOperation);
                }
                
            case TYPE_BINARY:
                throw new IllegalStateException("oh fucke yea!");
                
            default:
                throw new IllegalStateException(
                        cell1.getTableCellType() + ": unknown cell type.");
        }
    }
}
