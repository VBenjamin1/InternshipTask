package warehousemanager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Benjaminas Vedrickas
 */
public class Item implements Comparable<Item>{
    
    private static String[] columnNames = new String[4];
    private final String name;
    private final long code;
    private int quantity;
    private final Date expiration;
    
    public Item (String name, long code, int quantity, Date expiration){
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.expiration = expiration;
    }
    
    public int getQuantity(){
        return quantity;
    }
    
    public Date getExpiration(){
        return expiration;
    }
    
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public static void setColumnNames(String[] names){
        System.arraycopy(names, 0, columnNames, 0, 4);
    }
    
    public static void printColumnNames(){
        for (String s: columnNames)
            System.out.printf("%-20s|", s);
        
        System.out.println();
        System.out.println(new String(new char[84]).replace('\u0000', '-'));
    }
    
    public void printItemParameters(SimpleDateFormat format){
        System.out.printf("%-20s|", name);
        System.out.printf("%-20s|", code);
        System.out.printf("%-20s|", quantity);
        System.out.printf("%-20s|\n", format.format(expiration));
    }
    
    @Override
    public int compareTo(Item i) {
        
        int order;
        order = name.compareTo(i.name);
        
        if (order == 0){
            if (code < i.code)
                order = -1;
            if (code > i.code)
                order = 1;
        }
        
        if (order == 0){
            if (expiration.before(i.expiration))
                order = -1;
            if (expiration.after(i.expiration))
                order = 1;
        }
        
        return order;
    }
}
