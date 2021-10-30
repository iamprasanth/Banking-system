/**
 * Packages for java project.
 */
package JavaProject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.Color;
import javax.swing.JTextField;

/**
 * This class is used for handling operations performed by the bank employee
 * @author Aravind  Aparna Jaison Parvathy Prasanth Surender
 *
 */
class EmployeeAccount {

    /**
     * This method is used to validate employee login
     * @param userName This parameter holds the username of the employee
     * @param password This parameter holds the password of the employee
     * @throws IOException To manage when IO operations fails
     * @return boolean This function returns true in case of success and false in case of failure
     */
    boolean login(String userName, String password) {
        // check if credential is correct by reading the employee's file
        File file = new File(Constant.pathToRoot + Constant.employeetDataPath + userName);
        if (file.exists() && !file.isDirectory()) {
            FileReader fr;
            try {
                fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                if (password.equals(br.readLine())) {
                    // Password in file same as password inputed
                    return true;
                }
            } catch (IOException e) {
                HelperClass.showSuccessMessage("Error while reading exception, Restart application");
            }
        }

        // credential incorrect
        return false;
    }

    /**
     * This method is used to create a client account
     * @param fullName This parameter holds the fullname of the client
     * @param userName This parameter holds the username of the client
     * @param Password This parameter holds the password of the client
     * @param accountTypeStatus This parameter holds the account status of the client
     * @throws FileNotFoundException To be executed  if the file is not accessible
     * @return String This function returns the message to be displayed on screen in case of success/failure.
     */
    String createClient(String fullName,
        String userName,
        String Password,
        byte accountTypeStatus,
        String sin,
        String phoneNumber,
        String age,
        String address
    ) {
        try {
            // Validating if user name requested is available
            if (HelperClass.getMatchingFile("_" + userName) == null) { // user name available
                String accountNumber = generateNewAccountNumber();
                PrintWriter writeObj = new PrintWriter(Constant.clientDataPath + accountNumber + "_" + userName);
                writeObj.println(accountNumber);
                writeObj.println(userName);
                writeObj.println(Password);
                writeObj.println(fullName);
                writeObj.println(sin);
                writeObj.println(phoneNumber);
                writeObj.println(age);
                writeObj.println(address);
                writeObj.println(0); // Saving account balance
                writeObj.println(0); // Checking account balance
                writeObj.println(accountTypeStatus); // 1 -> only savings, 2 -> only checking, 3 -> both
                writeObj.close();
                // Show generated account number
                HelperClass.showSuccessMessage("New account opened for " + fullName + "\nThe Account number is : " + accountNumber);

                return "ok";
            }
        } catch (IOException e) {
            HelperClass.showSuccessMessage("Error while reading exception, Restart application");
        }

        return "User Name already exist";
    }

    /**
     * This method is used to generate an account number
     * @return String This function returns the account number generated.
     */
    String generateNewAccountNumber() {
        File directory = new File(Constant.pathToRoot + Constant.clientDataPath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = 0;
        File lastFile, file;
        lastFile = file = null;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                if (file.lastModified() > lastModifiedTime) {
                    lastFile = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }
        if (lastFile != null) { // At least one account exist in the system
            String lastFileName = lastFile.getName();
            String[] fileNameSplit = lastFileName.split("_", 2);
            return String.valueOf(Integer.parseInt(fileNameSplit[0]) + 1);
        }
        // Return the first account number, if no account exist
        return Constant.bankCode + "1000";
    }

    /**
     * This method is used to delete a client account
     * @param accountNumber This parameter holds the account number of the client
     * @throws IOException To manage when IO operations fails
     * @return String This function returns the message to be displayed on screen in case of success/failure.
     */
    String deleteClient(String accountNumber) {
        File clientFile = HelperClass.getMatchingFile(accountNumber + "_");
        if (clientFile != null) {
            try {
                Files.deleteIfExists(clientFile.toPath());
            } catch (IOException e) {
                HelperClass.showSuccessMessage("Error while reading exception, Restart application");
            }

            return "ok";
        }

        return "No such account found";
        
    }


    /**
     * This method is used to return an array list of all client's name and his account number
     * @throws IOException To manage when IO operations fails
     * @return ArrayList of all clients
     */
    ArrayList getAllClients() {
        try {
            ArrayList < String > clients = new ArrayList < > ();
            File directory = new File(Constant.pathToRoot + Constant.clientDataPath);
            
            if (directory != null) {
                File[] listOfFiles = directory.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        Path path = Paths.get(listOfFiles[i].getPath());
                        List < String > lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                        clients.add(lines.get(Constant.lineNumberFullName) + ": " + lines.get(Constant.lineNumberAccountNumber));
                    }
                }
            }

            return clients;
        } catch (IOException e) {
            HelperClass.showSuccessMessage("Error while reading exception, Restart application");
        }

        return null;
    }
}

/**
 * This class is used for handling operation done by a client
 */
class ClientAccount {
    String accountNumber;
    String userName;
    String password;
    String fullName;
    String sin;
    String phoneNumber;
    String age;
    String address;
    double savingsBalance;
    double checkingBalance;
    byte accountTypeStatus;


    /**
     * This is an empty constructor
     */
    ClientAccount() {}

    /**
     * This Constructor is invoked to get details of a client other than the logged in client
     * @param accountNumber This parameter holds the account number of the client to be found
     * @throws IOException To manage when IO operations fails
     * @throws FileNotFoundException To be executed  if the file is not accessible
     */
    ClientAccount(String accountNumber) {
        System.out.println("Constructer found " + accountNumber);
        File clientFile = HelperClass.getMatchingFile(accountNumber + "_");
        if (clientFile != null) { // A user under the given account number exist
            System.out.println("File found");
            this.initializeAttributes(clientFile);
        }
    }

    /**
     * Returns the user name of the client
     */
    String getUserName() {
        return this.userName;
    }

    /**
     * This method returns account type status of client
     * 1 means only savings account
     * 2 means only checking account
     * 3 means both savings and checking account 
     * @return byte returns the byte indicating account status
     */
    byte getAccountTypeStatus() {
        return this.accountTypeStatus;
    }

    /**
     * This method returns saving account balance of the client
     * @return double the savings account balance
     */
    double getSavingsBalance() {
        return this.savingsBalance;
    }

    /**
     * This method returns checking account balance of the client
     * @return double the checking account balance
     */
    double getCheckingBalance() {
        return this.checkingBalance;
    }

    /**
     * This method is used to validate client login
     * @param userName This parameter holds the username of the client
     * @param password This parameter holds the password of the client     
     * @throws IOException To manage when IO operations fails
     * @return boolean This function returns true in case of success and false in case of failure
     */
    boolean login(String userName, String password) {
        // Read data from correct file and return true if credential correct
        File clientFile = HelperClass.getMatchingFile("_" + userName);
        if (clientFile != null) { // A user under the entered user name exist
            this.initializeAttributes(clientFile);
            if (password.equals(this.password)) {
                return true;
            }
        }
        // Either user does not exist or password miss match
        return false;
    }

    /**
     * This method is to initialize the attribute of this class after reading data from correct file
     * @param clientFile This parameter holds the client's file
     * @throws IOException To manage when IO operations fails
     */
    void initializeAttributes(File clientFile) {
        try {
            List < String > lines = Files.readAllLines(Paths.get(clientFile.toString()), StandardCharsets.UTF_8);
            // Initializing objects after reading each lines
            this.accountNumber = lines.get(Constant.lineNumberAccountNumber);
            this.userName = lines.get(Constant.lineNumberUserName);
            this.password = lines.get(Constant.lineNumberPassword);
            this.fullName = lines.get(Constant.lineNumberFullName);
            this.sin = lines.get(Constant.lineSin);
            this.phoneNumber = lines.get(Constant.linePhoneNumber);
            this.age = lines.get(Constant.lineAge);
            this.address = lines.get(Constant.lineAddress);
            this.savingsBalance = Double.parseDouble(lines.get(Constant.lineNumberSavingsBalance));
            this.checkingBalance = Double.parseDouble(lines.get(Constant.lineNumberCheckingBalance));
            this.accountTypeStatus = Byte.parseByte(lines.get(Constant.lineNumberAccountType));
        } catch (IOException e) {
            HelperClass.showSuccessMessage("Error while reading exception, Restart application");
        }
    }

    /**
     * This method is used to deposit amount from client account
     * @param accountType This parameter holds the account type of the client
     * @param amount This parameter holds the amount to be withdrawn     
     * @throws IOException To manage when IO operations fails
     * @return String This function returns the message to be displayed on screen in case of success/failure.
     */
    String depositAmount(String accountType, String amount) {
        try {
            if (accountType.equals("Savings Account")) {
                if (this.accountTypeStatus == 1 || this.accountTypeStatus == 3) { // To make sure receiver has a savings account
                    double deposit = Double.parseDouble(amount) + this.savingsBalance;
                    String deposits = deposit + "";
                    Path path = Paths.get(Constant.pathToRoot + Constant.clientDataPath + this.accountNumber + "_" + this.userName);
                    List < String > lines = Files.readAllLines(path, StandardCharsets.UTF_8);

                    lines.set(Constant.lineNumberSavingsBalance, deposits); // Savings balance changed
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    this.savingsBalance = Double.parseDouble(deposits);

                    return "ok";
                }

                return "No Savings bank account";
            } else if (accountType.equals("Checking Account")) {
                if (this.accountTypeStatus == 2 || this.accountTypeStatus == 3) { // To make sure receiver has a checking account
                    double deposit = Double.parseDouble(amount) + this.checkingBalance;
                    String deposits = deposit + "";
                    Path path = Paths.get(Constant.pathToRoot + Constant.clientDataPath + this.accountNumber + "_" + this.userName);
                    List < String > lines = Files.readAllLines(path, StandardCharsets.UTF_8);

                    lines.set(Constant.lineNumberCheckingBalance, deposits); // Savings balance changed
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    this.checkingBalance = Double.parseDouble(deposits);

                    return "ok";
                }

                return "No Checkings bank account";
            }

            return "Invalid input";
        } catch (IOException e) {
            HelperClass.showSuccessMessage("Error while reading exception, Restart application");
        }

        return null;
    }

    /**
     * This method is used to withdraw amount from client account
     * @param accountType This parameter holds the account type of the client
     * @param amount This parameter holds the amount to be withdrawn
     * @param chargable This parameter holds true if extra charges are added on the transaction else it will hold false
     * @throws IOException To manage when IO operations fails
     * @return String This function returns the message to be displayed on screen in case of success/failure.
     */
    String withdrawAmount(String accountType, String amount, boolean chargable) {
        try {
            // If transaction charge applicable
            double withdrawAmount = chargable ? (Double.parseDouble(amount) + Constant.transactionCharge) : Double.parseDouble(amount);
            if (accountType.equals("Savings Account")) {
                if (this.savingsBalance < withdrawAmount) {
                    return "Insufficient Balance in Savings Account";
                } else {
                    double withdraw = this.savingsBalance - withdrawAmount;
                    String withdraws = withdraw + "";
                    Path path = Paths.get(Constant.pathToRoot + Constant.clientDataPath + this.accountNumber + "_" + this.userName);
                    List < String > lines = Files.readAllLines(path, StandardCharsets.UTF_8);

                    lines.set(Constant.lineNumberSavingsBalance, withdraws); // Savings balance changed
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    this.savingsBalance = Double.parseDouble(withdraws);

                }
            } else if (accountType.equals("Checking Account")) {
                if (this.checkingBalance < withdrawAmount) {
                    return "Insufficient Balance in Checking Account";
                } else {
                    double withdraw = this.checkingBalance - withdrawAmount;
                    String withdraws = withdraw + "";
                    Path path = Paths.get(Constant.pathToRoot + Constant.clientDataPath + this.accountNumber + "_" + this.userName);
                    List < String > lines = Files.readAllLines(path, StandardCharsets.UTF_8);

                    lines.set(Constant.lineNumberCheckingBalance, withdraws); // Checking balance changed
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    this.checkingBalance = Double.parseDouble(withdraws);
                }
            }
            return "ok";
        } catch (IOException e) {
            HelperClass.showSuccessMessage("Error while reading exception, Restart application");
        }

        return null;
    }

    /**
     * This method is used to pay a utility bill of the client
     * @param amount This parameter holds the amount to be paid
     * @throws IOException To manage when IO operations fails
     * @return String This function returns the message to be displayed on screen in case of success/failure.
     */
    String payUtitilyBill(String amount) {
        return this.withdrawAmount("Checking Account", amount, true);
    }
}

/**
 * This is the Main class for the Java Project.
 */
public class Main {

    /**
     * @param args An array of sequence of characters passed to the main method
     * @throws IOException To manage when IO operations fails
     */
    public static void main(String[] args) {
        try {
            welcomeScreen();
        } catch (Exception e) {
            HelperClass.showSuccessMessage("Application terminated unexpected, Please restart");
        }
    }

    /**
     * This method is used to display the welcome screen of the banking system
     * @throws IOException To manage when IO operations fails
     */
    static void welcomeScreen() {
        // To show the Welcome Screen for Bank system.
        JLabel label = new JLabel("Welcome to the BANK!!! Please click OK to continue...");
        Object[] login = {
            label
        };
        int loginDialogKeyPressed = JOptionPane.showConfirmDialog(null, login, "Greetings", JOptionPane.OK_CANCEL_OPTION);
        if (loginDialogKeyPressed == JOptionPane.OK_OPTION) {
            showEmployeeOrClient("");
        }
    }

    /**
     * This method is used to display the dialog box for choosing employee or client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails
     */
    static void showEmployeeOrClient(String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel employeeLable = new JLabel("1. Employee\n");
        JLabel clientLabel = new JLabel("2. Client");
        JTextField choice = new JTextField();
        Object[] login = {
            validation,
            employeeLable,
            clientLabel,
            choice
        };
        int loginDialogKeyPressed = JOptionPane.showConfirmDialog(null, login, "Who Are You ?", JOptionPane.OK_CANCEL_OPTION);
        if (loginDialogKeyPressed == JOptionPane.OK_OPTION) {
            switch (choice.getText()) {
                case "1": // Employee login
                    showLoginDialog("employee", "");
                    break;
                case "2": // Customer login
                    showLoginDialog("client", "");
                    break;
                default:
                    showEmployeeOrClient("Invalid Input");
                    break;
            }
        } else if (loginDialogKeyPressed == 2) {
            // Pressed cancel
            welcomeScreen();
        }
    }

    /**
     * This method is used to display the dialog box for for employee and client login
     * @param userType This parameter holds the type of user, it can be employee or client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails
     */
    static void showLoginDialog(String userType, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel userNameLabel = new JLabel("User Name");
        JTextField userName = new JTextField();
        JLabel passwordLoginLabel = new JLabel("Password");
        JTextField passwordLogin = new JPasswordField();
        Object[] login = {
            validation,
            userNameLabel,
            userName,
            passwordLoginLabel,
            passwordLogin
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, login, "Welcome " + userType, JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String userNameValue = userName.getText();
            String passwordValue = passwordLogin.getText();
            if (userNameValue != null && !userNameValue.isEmpty() && passwordValue != null && !passwordValue.isEmpty()) {
                if (userType == "client") { // Client is trying to login
                    ClientAccount clientAccount = new ClientAccount();
                    boolean loginCheck = clientAccount.login(userNameValue, passwordValue);
                    if (!loginCheck) { // credential incorrect
                        showLoginDialog("client", "Incorrect Credential");
                    } else { // Credentials correct
                        showMenuForClient(clientAccount, "");
                    }
                } else { // Employee is trying to login
                    EmployeeAccount employeeAccount = new EmployeeAccount();
                    boolean loginCheck = employeeAccount.login(userNameValue, passwordValue);
                    if (!loginCheck) { // credential incorrect
                        showLoginDialog("employee", "Incorrect Credential");
                    } else { // Credentials correct 
                        showMenuForEmployee(employeeAccount, "");
                    }
                }
            } else {
                showLoginDialog(userType, "Incorrect Credential");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showEmployeeOrClient("");
        }
    }

    /**
     * 
     * This method is used to display the menu dialog after successful employee login
     * @param employeeAccount This parameter holds object of the logged in employee
     * @param validationMessage This parameter holds the validation message to be shown if any 
     * @throws IOException To manage when IO operations fails
     */
    static void showMenuForEmployee(EmployeeAccount employeeAccount, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel create = new JLabel("1. Create Client Account");
        JLabel edit = new JLabel("2. Edit Client Account");
        JLabel delete = new JLabel("3. Delete Client Account");
        JLabel clientList = new JLabel("4. Show Clients list");
        JTextField choice = new JTextField();
        Object[] menu = {
            validation,
            create,
            edit,
            delete,
            clientList,
            choice
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Choose action", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            switch (choice.getText()) {
                case "1": // Create Account
                    showCreateClientDialog(employeeAccount, "");
                    break;
                case "2": // Edit Account
                    showEnterAccountNumberToEdit(employeeAccount, "");
                    break;
                case "3": // Delete Account
                    showDeleteClientDialog(employeeAccount, "");
                    break;
                case "4": // Client list
                    showClientListDialog(employeeAccount);
                    break;
                default: // Default
                    showMenuForEmployee(employeeAccount, "Invalid input");
                    break;
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showLoginDialog("employee", "");
        }
    }

    /**
     * This method is used to display the form for creating a new client
     * @param employeeAccount This parameter holds object of the logged in employee
     * @param validationMessage This parameter holds the validation message to be shown if any 
     * @throws IOException To manage when IO operations fails
     */
    static void showCreateClientDialog(EmployeeAccount employeeAccount, String validationMessage) {
        // Validation section
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        validationMessage = "";
        // Input Fields
        JLabel nameLabel = new JLabel("Full Name");
        JTextField name = new JTextField();
        JLabel userNameLabel = new JLabel("User Name");
        JTextField userName = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        JTextField password = new JPasswordField();
        JLabel accountTypeLabel = new JLabel("Account Type");
        JLabel sinLabel = new JLabel("SIN Number");
        JTextField sin = new JTextField();
        JLabel phoneNumberLabel = new JLabel("Phone Number");
        JTextField phoneNumber = new JTextField();
        JLabel ageLabel = new JLabel("Age");
        JTextField age = new JTextField();
        JLabel addressLabel = new JLabel("Address");
        JTextField address = new JTextField();

        String[] accountTypes = {
            "Savings Account",
            "Checking Account",
            "Both Savings and checking"
        };
        JComboBox accountDropdown = new JComboBox(accountTypes);
        Object[] ob = {
            validation,
            nameLabel,
            name,
            userNameLabel,
            userName,
            passwordLabel,
            password,
            accountTypeLabel,
            accountDropdown,
            sinLabel,
            sin,
            phoneNumberLabel,
            phoneNumber,
            ageLabel,
            age,
            addressLabel,
            address
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, ob, "New Client", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            // Get values from from
            String nameValue = name.getText();
            String userNameValue = userName.getText();
            String sinValue = sin.getText();
            String phoneNumberValue = phoneNumber.getText();
            String ageValue = age.getText();
            String addressValue = address.getText();
            String passwordValue = password.getText();
            // Form input validation
            if (nameValue == null || nameValue.isEmpty() ||
                passwordValue == null || passwordValue.isEmpty() ||
                userNameValue == null || userNameValue.isEmpty() ||
                sinValue == null || sinValue.isEmpty() ||
                phoneNumberValue == null || phoneNumberValue.isEmpty() ||
                ageValue == null || ageValue.isEmpty() ||
                addressValue == null || addressValue.isEmpty()
            ) {
                validationMessage = "All fields in the form are mandatory ";
            }
            if (!sinValue.isEmpty() && !HelperClass.isNumericString(sinValue)) {
                validationMessage += "Sin number invalid ";
            }
            if (!ageValue.isEmpty()  && !HelperClass.isNumericString(ageValue)) {
                validationMessage += "Age is invalid ";
            }
            if (!validationMessage.isEmpty()) {
                showCreateClientDialog(employeeAccount, validationMessage);
            }
            // If valid
            byte accountTypeStatus;
            String AccountType = accountDropdown.getItemAt(accountDropdown.getSelectedIndex()).toString();
            if (AccountType.equals(accountTypes[0])) {
                accountTypeStatus = 1; // Only savings
            } else if (AccountType.equals(accountTypes[1])) {
                accountTypeStatus = 2; // Only accounts
            } else {
                accountTypeStatus = 3; // Both
            }
            String creationStatus = employeeAccount.createClient(nameValue, userNameValue, passwordValue, accountTypeStatus, sinValue, phoneNumberValue, ageValue, addressValue); // call create function in client account class
            if (creationStatus == "ok") { // Validation passed
                showMenuForEmployee(employeeAccount, "");
            } else { // Some invalid input
                showCreateClientDialog(employeeAccount, creationStatus);
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForEmployee(employeeAccount, "");
        }
    }

    /**
     * This method is used to show the form to enter the account number of the client to be edited
     * @param employeeAccount This parameter holds object of the logged in employee
     * @param validationMessage This parameter holds the validation message to be shown if any 
     * @throws IOException To manage when IO operations fails
     */
    static void showEnterAccountNumberToEdit(EmployeeAccount employeeAccount, String validationMessage) {
        // Validation section
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        // Input Fields
        JLabel accountLabel = new JLabel("Account number");
        JTextField accountNumber = new JTextField();
        Object[] ob = {
            validation,
            accountLabel,
            accountNumber
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, ob, "Enter Client's Account number to Edit", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String accountNumberValue = accountNumber.getText();
            // Form input validation
            if (accountNumberValue != null && !accountNumberValue.isEmpty()) {
                // If valid
                File clientFile = HelperClass.getMatchingFile(accountNumberValue + "_"); // To get the file of the required client
                if (clientFile != null) { // Account Number exists
                    showeditClientDialog(clientFile, employeeAccount, "");
                } else {
                    showEnterAccountNumberToEdit(employeeAccount, "Account number does not exist");
                }
            } else {
                showEnterAccountNumberToEdit(employeeAccount, "Enter an account number");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForEmployee(employeeAccount, "");
        }
    }

    /**
     * This method is used to show the form to edit the details of a client
     * @param clientFile This parameter holds file of the client to be edited
     * @param employeeAccount This parameter holds object of the logged in employee
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showeditClientDialog(File clientFile, EmployeeAccount employeeAccount, String validationMessage) {
        try {
            // Validation section
            JLabel validation = new JLabel(validationMessage);
            validation.setForeground(Color.red);
            validationMessage = "";
            // Store all lines in the client file in a List
            Path filePath = Paths.get(clientFile.toString());
            List < String > lines = Files.readAllLines(Paths.get(clientFile.toString()), StandardCharsets.UTF_8);
            // Input Fields
            JLabel accountNumber = new JLabel("Account number: " + lines.get(Constant.lineNumberAccountNumber));
            JLabel UserName = new JLabel("User name: " + lines.get(Constant.lineNumberUserName) + "\n\n");
            JLabel nameLabel = new JLabel("Full Name");
            JTextField name = new JTextField(lines.get(Constant.lineNumberFullName));
            JLabel accountTypeLabel = new JLabel("Account Type");
            String[] accountTypes = {
                "Savings Account",
                "Checking Account",
                "Both Savings and checking"
            };
            JTextField sin = new JTextField(lines.get(Constant.lineSin));
            JTextField phoneNumber = new JTextField(lines.get(Constant.linePhoneNumber));
            JTextField age = new JTextField(lines.get(Constant.lineAge));
            JTextField address = new JTextField(lines.get(Constant.lineAddress));
            JComboBox accountDropdown = new JComboBox(accountTypes);
            String clientAccountType = accountTypes[Integer.parseInt(lines.get(Constant.lineNumberAccountType)) - 1];
            accountDropdown.setSelectedItem(clientAccountType);
            // Objects in pop up
            Object[] ob = {
                validation,
                accountNumber,
                UserName,
                nameLabel,
                name,
                accountTypeLabel,
                accountDropdown,
                "Sin number",
                sin,
                "Phone number",
                phoneNumber,
                "Age",
                age,
                "Address",
                address
            };
            int keyPressed = JOptionPane.showConfirmDialog(null, ob, "Edit Client Details", JOptionPane.OK_CANCEL_OPTION);
            if (keyPressed == JOptionPane.OK_OPTION) {
                String nameValue = name.getText();
                String sinValue = sin.getText();
                String phoneNumberValue = phoneNumber.getText();
                String ageValue = age.getText();
                String addressValue = address.getText();
                // Form input validation
                if (nameValue == null || nameValue.isEmpty() ||
                    sinValue == null || sinValue.isEmpty() ||
                    phoneNumberValue == null || phoneNumberValue.isEmpty() ||
                    ageValue == null || ageValue.isEmpty() ||
                    addressValue == null || addressValue.isEmpty()
                ) {
                    validationMessage = "All fields in the form are mandatory";
                }
                if (!sinValue.isEmpty() && !HelperClass.isNumericString(sinValue)) {
                    validationMessage += "Sin number invalid ";
                }
                if (!ageValue.isEmpty()  && !HelperClass.isNumericString(ageValue)) {
                    validationMessage += "Age is invalid ";
                }
                if (!validationMessage.isEmpty()) {
                    showeditClientDialog(clientFile, employeeAccount, validationMessage);
                } else {
                    byte accountTypeStatus;
                    String AccountType = accountDropdown.getItemAt(accountDropdown.getSelectedIndex()).toString();
                    if (AccountType.equals(accountTypes[0])) {
                        accountTypeStatus = 1; // Only savings
                    } else if (AccountType.equals(accountTypes[1])) {
                        accountTypeStatus = 2; // Only accounts
                    } else {
                        accountTypeStatus = 3; // Both
                    }
                    // Updates lines in the client File
                    lines.set(Constant.lineNumberFullName, nameValue);
                    lines.set(Constant.lineNumberAccountType, String.valueOf(accountTypeStatus));
                    lines.set(Constant.lineSin, String.valueOf(sinValue));
                    lines.set(Constant.linePhoneNumber, String.valueOf(phoneNumberValue));
                    lines.set(Constant.lineAge, String.valueOf(ageValue));
                    lines.set(Constant.lineAddress, String.valueOf(addressValue));
                    Files.write(filePath, lines, StandardCharsets.UTF_8);
                    HelperClass.showSuccessMessage("Client Details Successfully Updated");
                    showMenuForEmployee(employeeAccount, "");
                }
            } else if (keyPressed == 2) {
                // Pressed cancel
                showMenuForEmployee(employeeAccount, "");
            }
        } catch (IOException e) {
            HelperClass.showSuccessMessage("Error while reading exception, Restart application");
        }
    }

    /**
     * This method is used to show the form to delete a client
     * @param employeeAccount This parameter holds object of the logged in employee
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showDeleteClientDialog(EmployeeAccount employeeAccount, String validationMessage) {
        // Validation section
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        // Input Fields
        JLabel accountLabel = new JLabel("Account Number");
        JTextField accountNumber = new JTextField();
        Object[] ob = {
            validation,
            accountLabel,
            accountNumber
        };

        int keyPressed = JOptionPane.showConfirmDialog(null, ob, "Remove a Client !!!", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String accountNumberValue = accountNumber.getText();
            // Form input validation
            if (accountNumberValue != null && !accountNumberValue.isEmpty()) {
                // If valid
                String deletionStatus = employeeAccount.deleteClient(accountNumberValue); // call delete function in employee account class
                if (deletionStatus == "ok") { // Validation passed
                    HelperClass.showSuccessMessage("client deleted");
                    showMenuForEmployee(employeeAccount, "");
                } else { // Some invalid input
                    showDeleteClientDialog(employeeAccount, deletionStatus);
                }
            } else {
                showDeleteClientDialog(employeeAccount, "Enter an account number to delete");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForEmployee(employeeAccount, "");
        }
    }

    /**
     * This method is used to show a list of all client's name and their account number
     * @param employeeAccount This parameter holds object of the logged in employee
     * @throws IOException To manage when IO operations fails
     */
    static void showClientListDialog(EmployeeAccount employeeAccount) {
        ArrayList clients = employeeAccount.getAllClients();
        String details = "Total number of clients: " + clients.size() + "\n\n";
        for (int counter = 0; counter < clients.size(); counter++) {
            details = details + clients.get(counter) + '\n';
        }
        if (clients.isEmpty()) {
            details = "Currently no clients ";
        }
        int keyPressed = JOptionPane.showConfirmDialog(null, details, "Account holders and Account numbers", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION || keyPressed == JOptionPane.CANCEL_OPTION) {
            showMenuForEmployee(employeeAccount, "");
        }

    }

    /**
     * This method is used to show the menu for client
     * @param clientAccount This parameter holds object of the logged in client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showMenuForClient(ClientAccount clientAccount, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel details = new JLabel("1. Account Details");
        JLabel deposit = new JLabel("2. Deposit money ");
        JLabel balance = new JLabel("3. Check Balance");
        JLabel withdraw = new JLabel("4. Withdraw");
        JLabel billPayment = new JLabel("5. Pay Utiility bills");
        JLabel transfer = new JLabel("6. Transfer Money");
        JLabel logout = new JLabel("0. Logout");
        JTextField choice = new JTextField();
        Object[] menu = {
            validation,
            details,
            deposit,
            balance,
            withdraw,
            billPayment,
            transfer,
            logout,
            choice
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Choose action", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            switch (choice.getText()) {
                case "1": // Show account details
                    showAccountDetails(clientAccount);
                    break;
                case "2": // Deposit Money
                    showDepositDialog(clientAccount, "");
                    break;
                case "3": // Get Balance
                    showBalanceDialog(clientAccount);
                    break;
                case "4": // Withdraw
                    showWithdrawDialog(clientAccount, "");
                    break;
                case "5": // Pay bills
                    showPayUtilityBillDialog(clientAccount, "");
                    break;
                case "6": // Transfer to another account
                    showTransferAmountDialog(clientAccount, "");
                    break;
                case "0": // Quit
                    showEmployeeOrClient("");
                    break;
                default:
                    showMenuForClient(clientAccount, "Invalid input");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showLoginDialog("client", "");
        }
    }

    /**
     * This method is used to show the account details of the logged in client
     * @param clientAccount This parameter holds object of the logged in client
     * @throws IOException To manage when IO operations fails
     */
    static void showAccountDetails(ClientAccount clientAccount) {
        String accountsLabel = null;
        byte accountTypeStatus = clientAccount.getAccountTypeStatus();
        if (accountTypeStatus == 1) { // Only Savings account or both
            accountsLabel = "Accounts available: Saving Account";
        } else if (accountTypeStatus == 2) {
            accountsLabel = "Accounts available: Checking Account";
        } else {
            accountsLabel = "Accounts available: Saving Account & Checking Account";
        }
        Object[] menu = {
            "Account number: " + clientAccount.accountNumber,
            "User name: " + clientAccount.userName,
            "Full name: " + clientAccount.fullName,
            "SIN number: " + clientAccount.sin,
            "Phone number: " + clientAccount.phoneNumber,
            "Age: " + clientAccount.age,
            "Address: " + clientAccount.address,
            accountsLabel
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Your Account details", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION || keyPressed == JOptionPane.CANCEL_OPTION) {
            showMenuForClient(clientAccount, "");
        }
    }

    /**
     * This method is used to show Dialog box for showing balance details of logged in client
     * @param clientAccount This parameter holds object of the logged in client
     * @throws IOException To manage when IO operations fails
     */
    static void showBalanceDialog(ClientAccount clientAccount) {
        byte accountTypeStatus = clientAccount.getAccountTypeStatus();
        JLabel savings, checking;
        savings = checking = null;
        if (accountTypeStatus == 1 || accountTypeStatus == 3) { // Only Savings account or both
            savings = new JLabel("Saving Account: " + clientAccount.getSavingsBalance() + " $");
        }
        if (accountTypeStatus == 2 || accountTypeStatus == 3) { // Only checking account or both
            checking = new JLabel("Checking Account: " + clientAccount.getCheckingBalance() + " $");
        }
        Object[] menu = {
            savings,
            checking
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Your Account balance", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION || keyPressed == JOptionPane.CANCEL_OPTION) {
            showMenuForClient(clientAccount, "");
        }
    }

    /**
     * This method is used to show Dialog box for depositing amount to logged in client's account
     * @param clientAccount This parameter holds object of the logged in client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showDepositDialog(ClientAccount clientAccount, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel accountTypeLabel = new JLabel("Select Account type");
        byte accountTypeStatus = clientAccount.getAccountTypeStatus();
        JComboBox accountDropdown;
        if (accountTypeStatus == 1) {
            String[] accountTypes = {
                "Savings Account"
            };
            accountDropdown = new JComboBox(accountTypes);
        } else if (accountTypeStatus == 2) {
            String[] accountTypes = {
                "Checking Account"
            };
            accountDropdown = new JComboBox(accountTypes);
        } else {
            String[] accountTypes = {
                "Savings Account",
                "Checking Account"
            };
            accountDropdown = new JComboBox(accountTypes);
        }
        JLabel amountLabel = new JLabel("Enter amount");
        JTextField amount = new JTextField();
        Object[] menu = {
            validation,
            accountTypeLabel,
            accountDropdown,
            amountLabel,
            amount
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Deposit money", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String amountValue = amount.getText();
            if (HelperClass.isNumericString(amountValue)) {
                // Valid input
                String AccountType = accountDropdown.getItemAt(accountDropdown.getSelectedIndex()).toString();
                clientAccount.depositAmount(AccountType, amountValue);
                HelperClass.showSuccessMessage("Transaction complete");
                showMenuForClient(clientAccount, "");
            } else {
                showDepositDialog(clientAccount, "Enter a valid amount");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForClient(clientAccount, "");
        }
    }

    /**
     * This method is used to show Dialog box for withdrawing amount from logged in client's account
     * @param clientAccount This parameter holds object of the logged in client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showWithdrawDialog(ClientAccount clientAccount, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel accountTypeLabel = new JLabel("Select Account type");
        byte accountTypeStatus = clientAccount.getAccountTypeStatus();
        JComboBox accountDropdown;
        if (accountTypeStatus == 1) {
            String[] accountTypes = {
                "Savings Account"
            };
            accountDropdown = new JComboBox(accountTypes);
        } else if (accountTypeStatus == 2) {
            String[] accountTypes = {
                "Checking Account"
            };
            accountDropdown = new JComboBox(accountTypes);
        } else {
            String[] accountTypes = {
                "Savings Account",
                "Checking Account"
            };
            accountDropdown = new JComboBox(accountTypes);
        }
        JLabel amountLabel = new JLabel("Enter amount");
        JTextField amount = new JTextField();
        Object[] menu = {
            validation,
            accountTypeLabel,
            accountDropdown,
            amountLabel,
            amount
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Withdraw money", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String amountValue = amount.getText();
            if (HelperClass.isNumericString(amountValue)) {
                // Valid input
                String AccountType = accountDropdown.getItemAt(accountDropdown.getSelectedIndex()).toString();
                String withdrawStatus = clientAccount.withdrawAmount(AccountType, amountValue, false);
                if (withdrawStatus == "ok") {
                    HelperClass.showSuccessMessage("Transaction complete");
                    showMenuForClient(clientAccount, "");
                } else {
                    showWithdrawDialog(clientAccount, withdrawStatus);
                }
            } else {
                showWithdrawDialog(clientAccount, "Enter a valid amount");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForClient(clientAccount, "");
        }
    }

    /**
     * This method is used to show Dialog box for paying utility bills of logged in client
     * @param clientAccount This parameter holds object of the logged in client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showPayUtilityBillDialog(ClientAccount clientAccount, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel chargingLabel = new JLabel("Additional charge will be:  " + Constant.transactionCharge + " $");
        JLabel utilityLabel = new JLabel("Select Utility");
        String[] utilities = Constant.utilities;
        JComboBox utilitiesDropdown = new JComboBox(utilities);
        JLabel amountLabel = new JLabel("Enter amount");
        JTextField amount = new JTextField();
        Object[] menu = {
            validation,
            chargingLabel,
            utilityLabel,
            utilitiesDropdown,
            amountLabel,
            amount
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Pay utitily bill", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String amountValue = amount.getText();
            if (HelperClass.isNumericString(amountValue)) {
                byte accountTypeStatus = clientAccount.getAccountTypeStatus();
                if (accountTypeStatus == 2 || accountTypeStatus == 3) { // Checking if client has a checking account to proceed
                    String withdrawStatus = clientAccount.payUtitilyBill(amountValue);
                    if (withdrawStatus == "ok") {
                        HelperClass.showSuccessMessage("Transaction complete");
                        showMenuForClient(clientAccount, "");
                    } else {
                        showPayUtilityBillDialog(clientAccount, withdrawStatus);
                    }
                } else {
                    showPayUtilityBillDialog(clientAccount, "Please activate your checking account first");
                }
            } else {
                showPayUtilityBillDialog(clientAccount, "Enter a valid amount");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForClient(clientAccount, "");
        }
    }

    /**
     * This method is used to show Dialog box for transferring money from logged in account to another
     * @param clientAccount This parameter holds object of the logged in client
     * @param validationMessage This parameter holds the validation message to be shown if any
     * @throws IOException To manage when IO operations fails 
     */
    static void showTransferAmountDialog(ClientAccount clientAccount, String validationMessage) {
        JLabel validation = new JLabel(validationMessage);
        validation.setForeground(Color.red);
        JLabel chargingLabel = new JLabel("Additional charge will be:  " + Constant.transactionCharge + " $");
        JLabel toAccountLabel = new JLabel("Send to Account number");
        JTextField toAccount = new JTextField();
        JLabel amountLabel = new JLabel("Enter amount");
        JTextField amount = new JTextField();
        Object[] menu = {
            validation,
            chargingLabel,
            toAccountLabel,
            toAccount,
            amountLabel,
            amount
        };
        int keyPressed = JOptionPane.showConfirmDialog(null, menu, "Transfer money to another account", JOptionPane.OK_CANCEL_OPTION);
        if (keyPressed == JOptionPane.OK_OPTION) {
            String toAccountValue = toAccount.getText();
            String amountValue = amount.getText();
            if (HelperClass.isNumericString(amountValue) && !toAccountValue.isEmpty() && toAccountValue != null) {
                ClientAccount receiverAccount = new ClientAccount(toAccountValue);
                if (receiverAccount.accountNumber != null) {
                    if (clientAccount.getAccountTypeStatus() == 2 || clientAccount.getAccountTypeStatus() == 3) {
                        if (receiverAccount.getAccountTypeStatus() == 2 || receiverAccount.getAccountTypeStatus() == 3) {
                            if (clientAccount.withdrawAmount("Checking Account", amountValue, true) == "ok") {
                                if (receiverAccount.depositAmount("Checking Account", amountValue) == "ok") {
                                    HelperClass.showSuccessMessage("Transaction complete");
                                    showMenuForClient(clientAccount, "");
                                }
                            } else {
                                showTransferAmountDialog(clientAccount, "No sufficient balance in your checking account");
                            }
                        } else {
                            showTransferAmountDialog(clientAccount, "Reciever has not activated his checking account");
                        }
                    } else {
                        showTransferAmountDialog(clientAccount, "Please activate your checking account first");
                    }
                } else {
                    showTransferAmountDialog(clientAccount, "Invalid account number entered");
                }
            } else {
                showTransferAmountDialog(clientAccount, "Invalid Inputs entered");
            }
        } else if (keyPressed == 2) {
            // Pressed cancel
            showMenuForClient(clientAccount, "");
        }
    }

}
