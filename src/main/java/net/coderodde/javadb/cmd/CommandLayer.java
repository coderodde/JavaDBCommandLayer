package net.coderodde.javadb.cmd;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import net.coderodde.javadb.Database;
import net.coderodde.javadb.Table;
import net.coderodde.javadb.TableCellType;
import net.coderodde.javadb.TableColumnDescriptor;
import net.coderodde.javadb.TableRow;
import net.coderodde.javadb.TableView;

public final class CommandLayer {

    private Database database;
    
    public void emptyDatabase(String databaseName) {
        this.database = new Database(databaseName);
        Database db = this.database;
        TableColumnDescriptor table1Id =
                new TableColumnDescriptor("id", TableCellType.TYPE_INT);
        
        TableColumnDescriptor table1First =
                new TableColumnDescriptor("first_name", 
                                          TableCellType.TYPE_STRING);
        
        TableColumnDescriptor table1Last =
                new TableColumnDescriptor("last_name", 
                                          TableCellType.TYPE_STRING);
        
        Table table1 = db.createTable("person", 
                                      table1Id, 
                                      table1First, 
                                      table1Last);
        
        TableColumnDescriptor table2Id =
                new TableColumnDescriptor("id", TableCellType.TYPE_INT);
        
        TableColumnDescriptor table2PersonId =
                new TableColumnDescriptor("person_id", 
                                          TableCellType.TYPE_LONG);
        
        TableColumnDescriptor table2Msg =
                new TableColumnDescriptor("msg", 
                                          TableCellType.TYPE_STRING);
        
        Table table2 = db.createTable("msg", 
                                      table2Id, 
                                      table2PersonId, 
                                      table2Msg);
        
        table1.putTableRow(1, "Rodion", "Efremov");
        table1.putTableRow(2, "Violetta", "Ervasti");
        
        table2.putTableRow(10, 1L, "Hello!");
        table2.putTableRow(11, 2L, "Bye!");
    }
    
    public void openDatabase(File file) {
        database = Database.read(file);
    }
    
    public void openDatabase(String path) {
        openDatabase(new File(path));
    }
    
    public void saveDatabaseAs(File file) {
        Objects.requireNonNull(database, "Current database is null.");
        database.save(file);
    }
    
    public void saveDatabaseAs(String path) {
        saveDatabaseAs(new File(path));
    }
    
    public void saveDatabase() {
        Objects.requireNonNull(database, "Current database is null.");
        database.save();
    }
    
    public boolean createTable(String cmd) {
        Objects.requireNonNull(database, "No current database.");
        String[] tokens = cmd.split("\\s+");
        
        return false;
    }
    
    public TableView select(String cmd) {
        Objects.requireNonNull(database, "No current database.");
        String[] tokens = cmd.split("\\s+");
        
        if (!tokens[0].toLowerCase().equals("select")) {
            throw new IllegalArgumentException("Not a select query.");
        }
        
        int fromIndex = findFromIndex(tokens);
        
        if (fromIndex < 0) {
            throw new IllegalArgumentException(
                    "No 'from' keyword in the query.");
        }
        
        if (fromIndex < 2) {
            throw new IllegalArgumentException(
                    "No columns selected.");
        }
        
        if (fromIndex == tokens.length - 1) {
            throw new IllegalArgumentException("No table name.");
        }
        
        StringBuilder columnPartBuilder = new StringBuilder();
        
        for (int i = 1; i < fromIndex; ++i) {
            columnPartBuilder.append(tokens[i]).append(' ');
        }
        
        String columnPart = columnPartBuilder.toString();
        String[] columnStrings = columnPart.split(",");
        
        for (int i = 0; i < columnStrings.length; ++i) {
            columnStrings[i] = columnStrings[i].trim().toLowerCase();
        }
        
        String tableName = tokens[fromIndex + 1];
        
        if (tableName.endsWith(";")) {
            tableName = tableName.substring(0, tableName.length() - 1);
        }
        
        Table table = database.getTable(tableName);
        
        if (table == null) {
            throw new IllegalArgumentException(tableName + ": no such table.");
        }
        
        TableColumnDescriptor[] tableColumnDescriptors =
                new TableColumnDescriptor[columnStrings.length];
        
        for (int i = 0; i < tableColumnDescriptors.length; ++i) {
            String tableColumnName = columnStrings[i];
            TableColumnDescriptor tableColumnDescriptor = 
                    table.getTableColumnDescriptor(tableColumnName);
            
            if (tableColumnDescriptor == null) {
                throw new IllegalArgumentException(tableColumnName + ": no " +
                        "such table column.");
            }
            
            tableColumnDescriptors[i] = tableColumnDescriptor;
        }
        
        int whereIndex = findWhereIndex(tokens);
        
        if (whereIndex < 0) {
            // No where, print all rows.
            TableView tableView = table.createTableView(tableColumnDescriptors);
            
            for (TableRow tableRow : table) {
                tableView.addTableRow(tableRow);
            }
            
            return tableView;
        }
        
        // Once here, there are some WHERE specifications.
        StringBuilder sb = new StringBuilder();
        
        for (int i = whereIndex + 1; i < tokens.length; ++i) {
            sb.append(tokens[i]).append(' ');
        }
        
        String whereSpecification = sb.toString().trim().toLowerCase();
        whereSpecification =
                whereSpecification.substring(
                        0,
                        whereSpecification.length() - 1);
        
        TokenTreeNode tokenTreeRoot = getTokenTreeRoot(whereSpecification);
        TableView tableView = table.createTableView(tableColumnDescriptors);
        
        for (TableRow tableRow : table) {
            if (tableRowMatchesSelect(tableRow, tokenTreeRoot)) {
                tableView.addTableRow(tableRow);
            }
        }
        
        return null;
    }
    
    private static boolean tableRowMatchesSelect(TableRow tableRow,
                                                 TokenTreeNode root) {
        
        if (root.mode == TokenTreeNode.Mode.TEST) {
            switch (root.operation) {
                case EQUAL:
                    
                case NOT_EQUAL:
            }
        }
    }
    
    private static final class TokenTreeNode {
        enum Mode {
            AND,
            OR,
            TEST
        }
        
        enum Operation {
            EQUAL,
            NOT_EQUAL,
            LESS,
            LESS_EQ,
            GREATER,
            GREATER_EQ
        }
        
        private static final class Operand {
            enum OperandType {
                COLUMN,
                CONST
            }
            
            OperandType operandType;
            Object value;
        }
        
        Mode mode;
        Operation operation;
        Operand leftOperand;
        Operand rightOperand;
        TokenTreeNode leftChild;
        TokenTreeNode rightChild;
    }
    
    private static TokenTreeNode getTokenTreeRoot(String spec) {
        checkParentheses(spec);
        return getTokenTreeRoot(spec, 0, spec.length());
    }
    
    private static TokenTreeNode getTokenTreeRoot(String spec, 
                                                  int start, 
                                                  int end) {
        String currentSpec = spec.substring(start, end);
        
        int firstParenthesisIndex = currentSpec.indexOf("(");
        int lastParenthesisIndex = currentSpec.lastIndexOf(")");
        
        if (firstParenthesisIndex >= 0) {
            // Current spec substring contains a parentheses:
            TokenTreeNode ttn1 = getTokenTreeRoot(currentSpec, 
                                                  firstParenthesisIndex + 1,
                                                  lastParenthesisIndex);
        } else {
            int ands = currentSpec.split("and").length;
            int index = 0;
            
            for (int i = 0; i <= ands; ++i) {

            }
        }
        
        TokenTreeNode subtree = 
                getTokenTreeRoot(
                        spec.substring(firstParenthesisIndex + 1, 
                                       lastParenthesisIndex - 1));
    }
    
    private static void checkParentheses(String spec) {
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char c : spec.toCharArray()) {
            switch (c) {
                case '(':
                    stack.addLast(c);
                    break;
                    
                case ')':
                    if (stack.isEmpty() || stack.getLast() != '(') {
                        throw new IllegalArgumentException(
                                "Bad parenthesis structure."); 
                    }
                    
                    stack.removeLast();
            }
        }
    }
    
    private static int findTokenIndex(String[] tokens, String token) {
        token = token.toLowerCase();
        
        for (int i = 0; i < tokens.length; ++i) {
            if (tokens[i].toLowerCase().equals(token)) {
                return i;
            }
        }
        
        return -1;
    }
    
    private static int findFromIndex(String[] tokens) {
        return findTokenIndex(tokens, "from");
    }
    
    private static int findWhereIndex(String[] tokens) {
        return findTokenIndex(tokens, "where");
    }
}
