package warehousemanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Benjaminas Vedrickas
 */
public class WarehouseManager {

    private final ArrayList<Item> items = new ArrayList();
    private final String delimiter = ",";
    private final short itemParamsNum = 4;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public WarehouseManager(String filename) {    
        parseFromCSV(filename);
    }
    
    private void parseFromCSV(String filename) {
        
        BufferedReader br = null;
        String line;
        int currentLine = 0;
        
        try {
            br = new BufferedReader(new FileReader(filename));
            
            while ((line = br.readLine()) != null) {
                currentLine++;
                
                // if first line of csv, read values into static Item columns names
                if (currentLine == 1){
                    String[] columns = line.split(delimiter);
                    
                    if (columns.length != itemParamsNum)
                        throw new IllegalArgumentException("False number of item's parameters columns in a given csv file.");
                    
                    Item.setColumnNames(columns);
                    
                    continue;
                }
                
                // read csv by line, split string into separate values,
                // convert them and add new Item to array
                String[] item = line.split(delimiter);
                
                if (item.length != itemParamsNum)
                    throw new IllegalArgumentException("False number of item's parameters in a given csv file.");
                
                long code = Long.parseLong(item[1]);
                int quantity = Integer.parseInt(item[2]);
                Date expiration = dateFormat.parse(item[3]);
                
                items.add(new Item(item[0], code, quantity, expiration));
            }
        // terminate if fault occured while opening/reading csv file
        } catch (IOException e){
            System.err.println("Program terminated: " + e.getMessage());
            System.exit(1);
        // terminate if error happened while trying to assign values to Item
        } catch (IllegalArgumentException | ParseException e){
            System.err.println("Program terminated.");
            System.err.println(e.getClass() + ": " + e.getMessage());
            System.err.println("Incorrect .csv file line: " + currentLine);
            System.exit(1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println(e.getClass() + ": " + e.getMessage());
                }
            }
        }
        
        // sort list after all values are read
        Collections.sort(items);
        
        // combine identical items
        for (int i = 0; i < items.size() - 1; i++){
            Item current = items.get(i);
            Item next = items.get(i+1);
            
            if (current.compareTo(next) == 0){
                current.setQuantity(current.getQuantity() + next.getQuantity());
                items.remove(next);
                i--;
            }
        }
    }
     
    public void printItems(){
        Item.printColumnNames();
        
        for (Item i: items)
            i.printItemParameters(dateFormat);
    }
    
    public void printItems(Date expiration){
        Item.printColumnNames();
        
        for (Item i: items)
            if (i.getExpiration().before(expiration) || i.getExpiration().equals(expiration))
                i.printItemParameters(dateFormat);
    }
    
    public void printItems(int quantity){
        Item.printColumnNames();
        
        for (Item i: items)
            if (i.getQuantity() < quantity)
                i.printItemParameters(dateFormat);
    }
    
    public static void main(String[] args){
        
        if (args.length != 1){
            System.err.println("Program must have one parameter - filename.csv");
            return;
        }
        
        WarehouseManager wm = new WarehouseManager(args[0]);
        
        System.out.println("Warehouse Manager started...\n");
        System.out.println("Type some command for action, type \":h\" for help");
        
        while (true){
            Scanner input = new Scanner(System.in);
            String option = input.next();
            
            switch (option){
                case ":h":
                    System.out.println(":p - print all items");
                    System.out.println(":d - print items with deficit");
                    System.out.println(":e - print expired items");
                    System.out.println(":q - quit program");
                    break;
                case ":p":
                    wm.printItems();
                    break;
                case ":d":
                    try {
                        System.out.println("Enter item's quantity:");
                        int kiekis = Integer.parseInt(input.next());
                        wm.printItems(kiekis);
                    } catch (NumberFormatException e){
                        System.out.println("Incorrect quantity. Number must be integer.");
                    }
                    break;
                case ":e":
                    try {
                        System.out.println("Enter expiration date:");
                        wm.printItems(dateFormat.parse(input.next()));
                    } catch (ParseException ex) {
                        System.out.println("Incorrect date format.");
                        System.out.println("Date format must be yyyy-mm-dd");
                    }
                    break;
                case ":q":
                    return;
                default:
                    System.out.println("Invalid command. Type \":h\" for help.");
            }
            
            System.out.println("Enter next command:");
        }
    }
}
