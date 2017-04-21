package com.company.addTasks.firstTask;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static Connection con;
    static Statement st;
    static PreparedStatement ps;
    static ResultSet rs;
    static Statement st1;
    static PreparedStatement ps1;
    static ResultSet rs1;
    static Statement st2;
    static PreparedStatement ps2;
    static ResultSet rs2;
    static Statement st3;
    static ResultSet rs3;
    static Statement st4;
    static Statement ps4;
    static ResultSet rs4;
    static Statement ps5;
    static ResultSet rs5;

    private static final String URL = "jdbc:oracle:thin:@10.7.124.101:1521:INFO";
    private static final String USER = "metasonic";
    private static final String PWD = "metasonic";

    public static void main(String[] args) {

        String query = "SELECT CARD_ID, POSTAMT_INDEX, POSTAMT_NAME FROM  RUSPOST_OFFICE_CARD WHERE UFPS_INDEX = ?";
        String query0= "SELECT DISTINCT POSTAMT_INDEX, POSTAMT_NAME FROM  RUSPOST_OFFICE_CARD WHERE UFPS_INDEX = ?";
        String ufps_index = null;
        String postamt_index = null;
        String postamt_name = null;
        String card_id = null;
        ArrayList<String> postamt_names = new ArrayList<>();
        ArrayList<String> distinct_postamt_names = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        if (sc.hasNextLine()) {
            ufps_index = sc.nextLine();
            System.out.println(ufps_index);
        }
        try {
            con = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("connection is set");
            ps = con.prepareStatement(query);
            ps.setString(1, ufps_index);
            rs = ps.executeQuery();
            while (rs.next()) {
                postamt_index = rs.getString("POSTAMT_INDEX");
                postamt_name = rs.getString("POSTAMT_NAME");
                //card_id = rs.getString("CARD_ID");

                //System.out.println(postamt_name + " " + postamt_index);
                String full_postamt_name = postamt_name + " " + postamt_index;
                postamt_names.add(full_postamt_name);
            }
            System.out.println(postamt_names.size());
            if (postamt_names.size() == 0){
                System.out.println("Все заполнено.");
                //the end
            } else {
                con = DriverManager.getConnection(URL, USER, PWD);
                System.out.println("connection is set");
                ps1 = con.prepareStatement(query0);
                ps1.setString(1, ufps_index);
                rs1 = ps1.executeQuery();
                while (rs1.next()) {
                    postamt_index = rs1.getString("POSTAMT_INDEX");
                    postamt_name = rs1.getString("POSTAMT_NAME");
                    System.out.println(postamt_name + " " + postamt_index);
                    //String full_postamt_name = postamt_name + " " + postamt_index;
                    distinct_postamt_names.add(postamt_index);
                }
                System.out.println(distinct_postamt_names.size());
                workWithOPSUsers(distinct_postamt_names, ufps_index);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if (rs1 != null)
                    rs1.close();
                if (ps1 != null)
                    ps1.close();
                if (con != null)
                    con.close();
            } catch (Throwable th) {
            }
        }
    }


        public static void workWithOPSUsers(ArrayList<String> array, String ufps_index) throws SQLException {

            //ArrayList<String> allOPSsFromAllPostamts = new ArrayList<>();
//            String query12 = "SELECT DISTINCT TABLE4.name, TABLE4.postamt, TABLE3.ufps_index FROM (SELECT TABLE1.name, TABLE1.postamt\n" +
//                    "  FROM (SELECT UM_USER.NAME AS name, UM_GROUP.NAME AS postamt FROM UM_USER, UM_GROUP, UM_GROUP_UM_USER WHERE UM_GROUP_UM_USER.UM_USER = UM_USER.ID and\n" +
//                    "UM_GROUP_UM_USER.UM_GROUP =UM_GROUP.ID AND REGEXP_LIKE(UM_GROUP.NAME, '\\d{5}$')) TABLE1 INNER JOIN (SELECT UM_USER.NAME AS name FROM UM_USER, UM_GROUP_UM_USER WHERE UM_GROUP_UM_USER.UM_USER =UM_USER.ID and UM_GROUP_UM_USER.UM_GROUP = 15) TABLE2 ON\n" +
//                    "    TABLE1.name = TABLE2.name) table4 inner JOIN (SELECT UFPS_INDEX  AS ufps_index, POSTAMT_INDEX AS postamt_index FROM POSTAMT_VIEW) TABLE3 ON table4.postamt LIKE UPPER(CONCAT('%', ?)) AND UFPS_INDEX = '" + ufps_index + "'";
            LOOP: while (true) {
                System.out.print("Введите значение, что вы хотите сделать с пользователями: \n" +
                        "1 - добавить пользователей рандомно, в зависимости только от УФПС \n" + //setMultOPSsforOnePostamt(String ufps_index, String postamt_index)
                        "2 - добавить пользователей рандомно, в зависимости от УФПС и Почтамта (пользователь принадлежит определенному Почтамту) \n" + //setOneOPSforOnePostamt(String ufps_index)
                        "3 - выйти из программы \n");
                int operation = 0;
                Scanner sc = new Scanner(System.in);
                if (sc.hasNextInt()) {
                    operation = sc.nextInt();
                }

                switch (operation) {
                    case 1:
                        for (int i = 0; i < array.size(); i++) {
                            setMultOPSsforOnePostamt(ufps_index, array.get(i));
                        }
                        break;
                    case 2:
                        setOneOPSforOnePostamt(ufps_index);
                        break;
                    case 3:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неправильное значение.");
                        continue LOOP;
                }
            }
            /*try {
                con = DriverManager.getConnection(URL, USER, PWD);
                System.out.println("connection is set");
                for (int i=0; i< array.size(); i++){
                    //System.out.println(array.get(i));
                    ps2 = con.prepareStatement(query12);
                    ps2.setString(1, array.get(i));
                    rs2 = ps2.executeQuery();
                    ArrayList<String> names = new ArrayList<>();
                    while (rs2.next()) {
                        String name = rs2.getString("NAME");
                        System.out.println(name);
                        names.add(name);
                    }
                    //System.out.println("countOPSUsers: " + names);
                    //System.out.println(names.size());
                    if (names.size() == 1){
                        System.out.println("Один ОПС");
                        setOneOPSforOnePostamt(names, ufps_index, array.get(i));
                    } else if (names.size() > 1){
                        System.out.println("Несколько ОПС");
                        setMultOPSsforOnePostamt(names, ufps_index, array.get(i));
                    } else {
                        //System.out.println("Нет пользователя с ролью ОПС в данном почтамте.");
                        //setOneOPSforOnePostamt(ufps_index);
                        setMultOPSsforOnePostamt(ufps_index, array.get(i));
                    }
                    for (String name: names){
                        allOPSsFromAllPostamts.add(name);
                    }
                }
                System.out.println("setMultOPSsforOnePostamt: " + allOPSsFromAllPostamts);*/

    }

    public static void setOneOPSforOnePostamt(ArrayList<String> names, String ufps_index, String postamt_index) throws SQLException {
        //System.out.println("setOneOPSforOnePostamt start");
        String query2 = "UPDATE RUSPOST_OFFICE_CARD" +
                "  SET OFFICE_MANAGER = '"+ names.get(0) + "', OFFICE_EMAIL = '" + getMail(names.get(0)) + "' WHERE UFPS_INDEX = '"+ ufps_index + "' AND POSTAMT_INDEX = '"+ postamt_index + "' AND OFFICE_MANAGER IS NULL";
        con = DriverManager.getConnection(URL, USER, PWD);
        ps4 = con.createStatement();
        rs4 = ps4.executeQuery(query2);
        System.out.println("Done.");

            /*try {
                if (rs4 != null)
                    rs4.close();
                if (ps4 != null)
                    ps4.close();
                if (con != null)
                    con.close();
            } catch (Throwable th) {
        }*/
    }

    /*
    Set full file path.
     */
    private static final String FILE_NAME = "C:\\Users\\AGoryuchkina\\Desktop\\testXLSX.xls";


    /*
    Read info from XLS (2003) file. To read from XLSX (2007) you need to change HSSF prefix to XSSF.

     */
    private static List<String> readXLSX(){
        List<String> names = new ArrayList<>();
        List<String> f = new ArrayList<>();
        List<String> n = new ArrayList<>();
        List<String> ot = new ArrayList<>();
        int numCells=0;
        int numRows=0;

        try{
            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
        Workbook workbook = new HSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = datatypeSheet.iterator();

        while (rowIterator.hasNext()) {
            numRows ++;
            Row currentRow = rowIterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            //ArrayList<Cell> cells = new ArrayList<>();

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                if (currentCell == null){
                    break;
                }
                numCells++;

                //getCellTypeEnum shown as deprecated for version 3.15
                //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                    //System.out.print(currentCell.getStringCellValue() + "--");
                } else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    //System.out.print(currentCell.getNumericCellValue() + "--");
                }
                /*
                Set particular cells to particular lists in order tp work with them further
                 */
                if (numCells == 1) f.add(currentCell.getStringCellValue());
                if (numCells == 2) n.add(currentCell.getStringCellValue());
                if (numCells == 3) ot.add(currentCell.getStringCellValue());
            //names.add(f.get(numRows) + " " + n.get(numRows) + " " + o.get(numRows));
            }
            names.add(f.get(numRows-1) + " " + n.get(numRows-1) + " " + ot.get(numRows-1));
            numCells = 0;
            System.out.println(f.get(numRows-1) + "\n" + n.get(numRows-1) + "\n" + ot.get(numRows-1) + "\n");
        }
            System.out.println("names: " + names);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return names;
    }

    public static void setOneOPSforOnePostamt(String ufps_index) throws SQLException {
        System.out.println("setOneOPSforOnePostamt start");
        List<String> names = readXLSX();
        System.out.println("return to setOneOPSforOnePostamt");
        for (int k = 0; k < names.size(); k++) {
            System.out.println("UPDATE RUSPOST_OFFICE_CARD" +
                    "  SET OFFICE_MANAGER = '" + names.get(k) + "', OFFICE_EMAIL = '" + getMail(names.get(k)) + "' WHERE UFPS_INDEX = '" + ufps_index + "' AND POSTAMT_INDEX = '" + getPostamt(names.get(k))+ "';");
        }
    }

    public static String getPostamt(String name) throws SQLException {
        String query0 = "SELECT UM_USER.name AS name,\n" +
                "       UM_GROUP.name AS postamt\n" +
                "  FROM UM_USER,\n" +
                "       UM_GROUP,\n" +
                "       UM_GROUP_UM_USER\n" +
                "  WHERE UM_GROUP_UM_USER.UM_USER = UM_USER.ID\n" +
                "    AND UM_GROUP_UM_USER.UM_GROUP = UM_GROUP.ID\n" +
                "    AND REGEXP_LIKE(UM_GROUP.name, '\\d{6}$')\n" +
                "    AND UM_USER.name = '"+ name +"'";
        String postamt = null;
        String postamt_index = null;
        con = DriverManager.getConnection(URL, USER, PWD);
        st = con.createStatement();
        rs5 = st.executeQuery(query0);
        if (rs5.next()){
            postamt = rs5.getString("POSTAMT");
            //System.out.println(postamt);
            String[] postamtParts = postamt.split(" ");
            for (int i = 0; i < postamtParts.length; i ++){
                postamt_index = postamtParts[i];
                Pattern p = Pattern.compile("^\\d{6}$");
                Matcher m = p.matcher(postamtParts[i]);
                if (m.matches()){
                   break;
                }
            }
            //System.out.println(postamt_index);
        }
        return postamt_index;
    }

    public static void setMultOPSsforOnePostamt(ArrayList<String> names, String ufps_index, String postamt_index) throws SQLException {
        //System.out.println("setMultOPSsforOnePostamt start");
        ArrayList<String> card_ids = getCard_id(ufps_index, postamt_index);
        int numRows = card_ids.size();
        int n = 0;
     for (int k = 0; k<numRows; k++){
         if (n == names.size())
             n =0;
         System.out.println("UPDATE RUSPOST_OFFICE_CARD" +
                 "  SET OFFICE_MANAGER = '" + names.get(n) + "', OFFICE_EMAIL = '" + getMail(names.get(n)) + "' WHERE CARD_ID = '"+ card_ids.get(k) +"';");
         n++;
     }

    }


    public static void setMultOPSsforOnePostamt(String ufps_index, String postamt_index) throws SQLException {
        //System.out.println("setMultOPSsforOnePostamt start  1");
        String[] names = {"Кочерга Татьяна Вячеславовна",
                "Дудкина Виктория Викторовна"
        };
        ArrayList<String> card_ids = getCard_id(ufps_index, postamt_index);
        int numRows = card_ids.size();
        int n = 0;
        for (int k = 0; k < numRows; k++) {
            if (n == names.length)
                n = 0;
            System.out.println("UPDATE RUSPOST_OFFICE_CARD" +
                   " SET OFFICE_MANAGER = '" + names[n] + "', OFFICE_EMAIL = '" + getMail(names[n]) + "' WHERE CARD_ID = '" + card_ids.get(k) + "';");

            n++;
        }
    }

    public static ArrayList<String> getCard_id(String ufps_index, String postamt_index) throws SQLException {
        //System.out.println("getCard_id start");
        ArrayList<String> card_ids = new ArrayList<>();
        String query4 = "SELECT CARD_ID FROM  RUSPOST_OFFICE_CARD WHERE UFPS_INDEX = '"+ ufps_index + "' AND POSTAMT_INDEX = '"+ postamt_index + "'";
        con = DriverManager.getConnection(URL, USER, PWD);
        st = con.createStatement();
        rs5 = st.executeQuery(query4);
        while (rs5.next()){
            String card_id = rs5.getString("CARD_ID");
            card_ids.add(card_id);
        }
        //System.out.println(card_ids);
        return card_ids;
    }

    public static String getMail(String name) throws SQLException {
        //System.out.println("getMail start");
        String mail = null;
        String query3 = "SELECT ATTRIBUTEVALUE FROM UM_USER_ATTRIBUTES, UM_USER WHERE UM_USER_ATTRIBUTES.UM_USER = UM_USER.ID AND UM_USER.NAME LIKE('%"+ name +"%') AND UM_USER_ATTRIBUTES.ATTRIBUTENAME != 'password'";
        con = DriverManager.getConnection(URL, USER, PWD);
        st3 = con.createStatement();
        rs3 = st3.executeQuery(query3);
        while (rs3.next()){
            mail = rs3.getString("ATTRIBUTEVALUE");
            if (mail.equals(null)) mail = "PochtaInfoPunkt@gmail.com";
            //System.out.println(mail);
        }
        //if (mail.equals(null)) mail = "PochtaInfoPunkt@gmail.com";
        return mail;
    }
}
