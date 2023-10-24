import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
interface Configuration {
    void displayInfo();
    double calculateCost();
}
abstract class Publication implements Configuration {
    private int id;
    private String title;
    public Publication(int id, String title) {
        this.id = id;
        this.title = title;
    }
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    @Override
    public void displayInfo() {
        System.out.println("------------");
        System.out.println("ID: " + id );
        System.out.println("Title: " + title);
    }
    public abstract double calculateCost();
}
class Book extends Publication {
    private String author;
    private int year;
    private int popularityCount;
    private int price;
    public Book(int id, String title, String author, int year, int popularityCount, int price) {
        super(id, title);
        this.author = author;
        this.year = year;
        this.popularityCount = popularityCount;
        this.price = price;
    }
    public String getAuthor() {
        return author;
    }
    public int getYear() {
        return year;
    }
    public int getPopularityCount() {
        return popularityCount;
    }
    public int getPrice() {
        return price;
    }
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Author: " + author);
        System.out.println("Year: " + year);
        System.out.println("Popularity Count: " + popularityCount);
        System.out.println("Price: $" + price);
    }
    @Override
    public double calculateCost(){
        double bookCost = getPrice();
        double gst = (0.2 * bookCost);
        return (bookCost + gst + 200);
    }
}
class Borrower {
    private int userId;
    private String name;
    private List<Integer> borrowedItems; // Store item IDs of borrowed items
    public Borrower() {
        borrowedItems = new ArrayList<>(); // Initialize the list here
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Integer> getBorrowedItems() {
        return borrowedItems;
    }
    public void setBorrowedItems(List<Integer> borrowedItems) {
        this.borrowedItems = borrowedItems;
    }
    public void borrowItem(int itemId) {
        borrowedItems.add(itemId);
    }
    public void returnItem(int itemId) {
        borrowedItems.remove(Integer.valueOf(itemId));
    }
}
class LibraryItem extends Publication {
    private boolean isBorrowed;
    private int borrowerId;
    private int popularityCount;
    private double cost;
    public LibraryItem(int id, String title, int popularityCount, double cost) {
        super(id, title);
        this.popularityCount = popularityCount;
        this.cost=cost;
    }
    public boolean isBorrowed() {
        return isBorrowed;
    }
    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }
    public int getBorrowerId() {
        return borrowerId;
    }
    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }
    public int getPopularityCount() {
        return popularityCount;
    }
    public void setPopularityCount(int popularityCount) {
        this.popularityCount = popularityCount;
    }
    @Override
    public double calculateCost() {

        return cost;
    }
}
class Library {
    private List<Publication> publications = new ArrayList<>();
    private List<LibraryItem> libraryItems = new ArrayList<>();
    private List< Borrower> borrowers;
    public Library() {
        libraryItems = new ArrayList<>();
        borrowers = new ArrayList<>();
    }
    public List<Borrower> getBorrowers() {
        return borrowers;
    }
    public void addPublication(Publication publication) {
        publications.add(publication);
    }

    public boolean editPublication(int id, Publication newPublication) {
        for (int i = 0; i < publications.size(); i++) {
            if (publications.get(i).getId() == id) {
                publications.set(i, newPublication);
                return true;
            }
        }
        return false;
    }
    public boolean deletePublication(int id) {
        for (Publication publication : publications) {
            if (publication.getId() == id) {
                publications.remove(publication);
                return true;
            }
        }
        return false;
    }
    public List<Publication> getAllPublications() {
        return publications;
    }
    public void displayPublicationDetails(Publication publication) {
        if (publication != null) {
            publication.displayInfo();
        } else {
            System.out.println("Publication not found.");
        }
    }
    public Publication getPublicationById(int id) {
        for (Publication publication : publications) {
            if (publication.getId() == id) {
                return publication;
            }
        }
        return null;
    }
    public void addLibraryItem(LibraryItem item) {
        libraryItems.add(item);
    }
    public void addBorrower(Borrower borrower) {
        borrowers.add(borrower);
    }
    public void borrowItem(int userId, int itemId) {
        Borrower borrower = findBorrower(userId);
        LibraryItem item = findLibraryItem(itemId);

        if (borrower != null && item != null && !item.isBorrowed()) {
            borrower.borrowItem(itemId);
            item.setBorrowed(true);
            item.setBorrowerId(userId);
            // Increase popularity count when an item is borrowed
            item.setPopularityCount(item.getPopularityCount() + 1);
        }
    }
    public void returnItem(int userId, int itemId) {
        Borrower borrower = findBorrower(userId);
        LibraryItem item = findLibraryItem(itemId);
        if (borrower != null && item != null && borrower.getBorrowedItems().contains(itemId)) {
            borrower.returnItem(itemId);
            item.setBorrowed(false);
            item.setBorrowerId(0);
        }
    }
    public void updatePopularityCount(int itemId, int popularityCount) {
        LibraryItem item = findLibraryItem(itemId);
        if (item != null) {
            item.setPopularityCount(popularityCount);
        }
    }
    public void displayBorrowers() {
        for (Borrower borrower : borrowers) {
            System.out.println("---------------");
            System.out.println("Borrower ID: " + borrower.getUserId());
            System.out.println("Borrower Name: " + borrower.getName());
            List<String> borrowedItemTitles = borrower.getBorrowedItems().stream()
                    .map(itemId -> findLibraryItem(itemId).getTitle())
                    .collect(Collectors.toList());
            System.out.println("Borrowed Items: " + String.join(", ", borrowedItemTitles));
            System.out.println();
        }
    }
    public void displayHotPicks() {
        List<LibraryItem> hotPicks = libraryItems.stream()
                .sorted((item1, item2) -> Integer.compare(item2.getPopularityCount(), item1.getPopularityCount()))
                .collect(Collectors.toList());

        System.out.println("Hot Picks:");
        for (LibraryItem item : hotPicks) {
            System.out.println("---------------");
            System.out.println("Title: " + item.getTitle());
            System.out.println("Popularity Count: " + item.getPopularityCount());
            System.out.println();
        }
    }
    public Borrower findBorrower(int userId) {
        return borrowers.stream()
                .filter(borrower -> borrower.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }
    public LibraryItem findLibraryItem(int itemId) {
        for (LibraryItem item : libraryItems) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }
}
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        loadDataFromFile(library);

        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\nLibrary Management System Menu:");
            System.out.println("1. Add a Publication");
            System.out.println("2. Edit a Publication");
            System.out.println("3. Delete a Publication");
            System.out.println("4. View All Publications");
            System.out.println("5. View Publication by ID");
            System.out.println("6. Hot Picks!");
            System.out.println("7. Borrow an Item");
            System.out.println("8. Return Book");
            System.out.println("9. View Borrowers List");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    // Add a Publication
                    addPublication(scanner, library);
                    break;
                case 2:
                    // Edit a Publication
                    editPublication(scanner, library);
                    break;
                case 3:
                    // Delete a Publication
                    deletePublication(scanner, library);
                    break;
                case 4:
                    // View All Publications
                    displayAllPublications(library);
                    break;
                case 5:
                    System.out.print("Enter Publication ID to View: ");
                    int idToView = scanner.nextInt();
                    Publication publicationToView = library.getPublicationById(idToView);
                    library.displayPublicationDetails(publicationToView);
                    break;
                case 6:
                    // Display Hot Picks
                    library.displayHotPicks();
                    break;
                case 7:
                    // Borrow a book
                    borrowBook(scanner, library);
                    break;
                case 8:
                    // Return a book
                    returnBook(scanner, library);
                    break;
                case 9:
                    // Display Borrowers
                    library.displayBorrowers();
                    break;
                case 10:
                    // Exit
                    System.out.println("Exiting Library Management System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 10);
        scanner.close();
    }
    private static void addPublication(Scanner scanner, Library library) {
        scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        // Prepare data to write to the file
        String dataToWrite = null;
            // Book
            System.out.print("Enter Author: ");
            String author = scanner.nextLine();
            System.out.print("Enter Year: ");
            int year = scanner.nextInt();
            System.out.print("Enter Popularity Count: ");
            int popularityCount = scanner.nextInt();
            System.out.print("Enter Price: ");
            int price = scanner.nextInt();
            int nextId = library.getAllPublications().size() + 1;
            Book newBook = new Book(nextId, title, author, year, popularityCount, price);
            library.addPublication(newBook);
            double newcost = newBook.calculateCost();
            // Create a formatted string to write to the file
            dataToWrite = String.format("%d, %s, %s, %d, %d, %d",
                    nextId, title, author, year, popularityCount, price);
            System.out.println(dataToWrite);
            System.out.println("Book added successfully.");
        // Write the data to the file
        writeDataToFile(dataToWrite);
    }
    private static void writeDataToFile(String dataToWrite) {
        String filePath = "data.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(dataToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void editPublication(Scanner scanner, Library library) {
        System.out.print("Enter Publication ID to Edit: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        Publication publicationToEdit = library.getPublicationById(id);
        if (publicationToEdit != null) {
            System.out.println("Enter New Details:");
            System.out.print("Enter Title: ");
            String newTitle = scanner.nextLine();
            if (publicationToEdit instanceof Book) {
                // Editing a Book
                Book bookToEdit = (Book) publicationToEdit;
                System.out.print("Enter New Author: ");
                String newAuthor = scanner.nextLine();
                System.out.print("Enter New Year: ");
                int newYear = scanner.nextInt();
                System.out.print("Enter New Popularity Count: ");
                int newPopularityCount = scanner.nextInt();
                System.out.print("Enter New Price: ");
                int newPrice = scanner.nextInt();
                Book newBook = new Book(id, newTitle, newAuthor, newYear, newPopularityCount, newPrice);
                library.editPublication(id, newBook);
                System.out.println("Book edited successfully.");
                // Update data in the file
                updateDataInFile(id, newBook, newTitle);
            } else {
                System.out.println("Publication with ID " + id + " not found.");
            }
        }
    }
    private static void updateDataInFile(int bookId, Book newBook, String newTitle) {
        String filePath = "data.txt";
        List<String> newLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                int id;
                if (!parts[0].isEmpty()) {
                    id = Integer.parseInt(parts[0]);
                } else {
                    newLines.add(line); // Skip this line and move to the next one
                    continue;
                }
                if (id == bookId) {
                    // Replace the line with the updated book data
                    newLines.add(String.format("%d, %s, %s, %d, %d, %d",
                            bookId, newTitle, newBook.getAuthor(), newBook.getYear(), newBook.getPopularityCount(), newBook.getPrice()));
                } else {
                    newLines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // Write the updated data (all lines, including the edited one) back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (String newLine : newLines) {
                writer.println(newLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private static void deletePublication(Scanner scanner, Library library) {
        System.out.print("Enter Publication ID to Delete: ");
        int id = scanner.nextInt();
        boolean deleted = library.deletePublication(id);
        if (deleted) {
            // Notify the user
            System.out.println("Publication with ID " + id + " deleted successfully.");
            // Remove the data from the file
            removeDataFromFile(id);
        } else {
            System.out.println("Publication with ID " + id + " not found.");
        }
    }
    private static void removeDataFromFile(int id) {
        String filePath = "data.txt";
        List<String> newLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length < 6) {
                    // Skip this line if it doesn't have enough parts to represent valid data
                    continue;
                }
                int itemID = Integer.parseInt(parts[0]);
                if (itemID == id) {
                    // Skip this line to effectively delete it from the file
                    continue;
                }
                newLines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Write the updated data (all lines except the deleted one) back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (String newLine : newLines) {
                writer.println(newLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void displayAllPublications( Library library) {
        System.out.println("All Publications:");
        // Load data from the file if the library is empty
        if (library.getAllPublications().isEmpty()) {
            loadDataFromFile(library);
        }
        // Display all publications
        List<Publication> allPublications = library.getAllPublications();
        for (Publication publication : allPublications) {
            publication.displayInfo();
        }
    }
    private static void borrowBook(Scanner scanner, Library library) {
        System.out.print("Enter the ID of the book you want to borrow: ");
        int bookId = scanner.nextInt();
        // Check if the book exists in the library
        LibraryItem bookToBorrow = library.findLibraryItem(bookId);
        System.out.println(bookToBorrow.getTitle());
        if (bookToBorrow == null) {
            System.out.println("Book not found.");
            return;
        }
        // Check if the book is already borrowed
        if (bookToBorrow.isBorrowed()) {
            System.out.println("This book is already borrowed by another user.");
            return;
        }
        // Borrow the book
        System.out.print("Enter your name: ");
        String userName = scanner.next();
        Borrower borrower = new Borrower();
        borrower.setName(userName);
        borrower.setUserId(library.getBorrowers().size() + 1); // Generate a unique user ID
        library.addBorrower(borrower);
        bookToBorrow.setPopularityCount(bookToBorrow.getPopularityCount());
        library.borrowItem(borrower.getUserId(), bookId);
        double cost = bookToBorrow.calculateCost();
        System.out.println(userName + ", you have successfully borrowed the book: " + bookToBorrow.getTitle());
        System.out.println("Cost: $" + cost);
    }
    private static void returnBook(Scanner scanner, Library library) {
        System.out.print("Enter your User ID: ");
        int userId = scanner.nextInt();
        // Check if the user exists in the library
        Borrower borrower = library.findBorrower(userId);
        if (borrower == null) {
            System.out.println("User not found. Please register as a borrower first.");
            return;
        }
        System.out.print("Enter the ID of the book you want to return: ");
        int bookId = scanner.nextInt();
        // Check if the book exists in the library
        LibraryItem bookToReturn = library.findLibraryItem(bookId);
        if (bookToReturn == null) {
            System.out.println("Book not found.");
            return;
        }
        // Check if the user has borrowed this book
        if (!borrower.getBorrowedItems().contains(bookId)) {
            System.out.println(borrower.getName() + ", you haven't borrowed this book.");
            return;
        }
        // Return the book
        library.returnItem(userId, bookId);
        System.out.println(borrower.getName() + ", you have successfully returned the book: " + bookToReturn.getTitle());
    }
    private static void loadDataFromFile(Library library) {
        String filePath = "data.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                String[] parts = line.split(", ");
                //int id = Integer.parseInt(parts[0]);
                int id;
                if (!parts[0].isEmpty()) {
                    id = Integer.parseInt(parts[0]);
                } else {
                    continue; // Skip this line and move to the next one
                }
                String title = parts[1];
                //String type = parts[2];
                    int popularityCount = Integer.parseInt(parts[parts.length - 2]);
                    int price = Integer.parseInt(parts[parts.length - 1]);
                    double newcost=(price+(0.2*price)+200);
                    LibraryItem libraryItem = new LibraryItem(id, title, popularityCount,newcost);
                    library.addLibraryItem(libraryItem);
                    String author = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    int popularityCount2 = Integer.parseInt(parts[4]);
                    int price2 = Integer.parseInt(parts[5]);
                    // Create and add the book to the library
                    int nextId = library.getAllPublications().size() + 1;
                    Book book = new Book(nextId, title, author, year, popularityCount2, price2);
                    library.addPublication(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}