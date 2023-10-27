import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
interface Configuration {
    void displayInfo();
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
    @Override
    public String toString() {
        return "ID: " + getId() +
                "\nTitle: " + getTitle();
    }
}
class Book extends Publication {
    private String author;
    private int year;
    private int popularityCount;
    private int price;
    public static Map<String, String> content = new HashMap<>();
    public Book(int id, String title, String author, int year, int popularityCount, int price,String content) {
        super(id, title);
        this.author = author;
        this.year = year;
        this.popularityCount = popularityCount;
        this.price = price;
        this.content.put(title,content);
    }
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
    public static Map getContent(){
        return content;
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
    public String toString() {
        return "ID: " + getId() +
                "\nTitle: " + getTitle() +
                "\nAuthor: " + author +
                "\nYear: " + year +
                "\nPopularity Count: " + popularityCount +
                "\nPrice: $" + price;
    }
}
class Library {
    private List<Publication> publications = new ArrayList<>();
    public void addPublication(Publication publication) {
        publications.add(publication);
    }
    public boolean editPublication(int id, Publication newPublication) {
        for (int i = 0; i < publications.size(); i++) {
            if (publications.get(i).getId() == id) {
                System.out.println(newPublication);
                publications.set(i, newPublication);
                System.out.println(publications);
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
}
class PopularityChartFrame extends JFrame {
    private final int[] popularityData;
    private final String[] bookNames;
    public PopularityChartFrame(int[] popularityData, String[] bookNames) {
        this.popularityData = popularityData;
        this.bookNames = bookNames;
        setTitle("Popularity Chart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int x = 50; // Initial X position
        int barWidth = 40; // Width of each bar
        int maxHeight = 500; // Maximum height for the bars (adjust as needed)
        // Calculate the maximum popularity count
        int maxPopularity = Arrays.stream(popularityData).max().orElse(0);
        // Draw Y-axis and labels
        g.drawLine(x, 50, x, getHeight() - 50); // Y-axis
        g.drawLine(x - 5, 50, x + 5, 50); // Y-axis endpoint
        g.drawString("Popularity Count", x - 60, 30);
        // Calculate the number of divisions on the Y-axis
        int numDivisions = maxPopularity + 1; // Each division represents a step of 1
        for (int i = 0; i < numDivisions; i++) {
            int y = getHeight() - (i * (maxHeight / numDivisions)) - 50;
            g.drawString(Integer.toString(i), x - 30, y + 5); // Y-axis labels
        }
        // Draw X-axis and labels
        g.drawLine(x, getHeight() - 50, x + (popularityData.length * 60), getHeight() - 50); // X-axis
        for (int i = 0; i < popularityData.length; i++) {
            int barHeight = (int) (maxHeight * (popularityData[i] / (double) maxPopularity));
            g.setColor(Color.CYAN);
            g.fillRect(x, getHeight() - barHeight - 50, barWidth, barHeight);
            g.setColor(Color.black);
            g.drawRect(x, getHeight() - barHeight - 50, barWidth, barHeight);
            g.drawString(bookNames[i], x, getHeight() - 10); // Display book name at the end of X-axis
            x += 60; // Adjust for spacing
        }
    }
}
class ButtonRenderer extends JButton implements TableCellRenderer {
    private boolean isPushed;
    public ButtonRenderer() {
        setOpaque(true);
        setText("Read");
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText("Read");
        isPushed = true;
        return this;
    }
}
public class LibraryManagementSystem {
    private Library library;
    private static DefaultTableModel tableModel;
    private JTable table;
    public LibraryManagementSystem() {
        library = new Library();
        // Create a table to display books
        String[] columnNames = {"ID", "Title", "Author", "Year", "Popularity Count", "Price", "Read Item"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadDataFromFile(library);
        JFrame frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        // Add a mouse motion listener for row highlighting
        table.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.getSelectionModel().setSelectionInterval(row, row);
                } else {
                    table.getSelectionModel().clearSelection();
                }
            }
    });
        frame.add(scrollPane, BorderLayout.CENTER);
        // Create buttons for Add, Edit, Delete and View Popularity
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton viewPopularityButton = new JButton("View Popularity");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a pane to input book details
                JPanel addBookPanel = new JPanel(new GridLayout(5, 2));
                JTextField titleField = new JTextField(15);
                JTextField authorField = new JTextField(15);
                JTextField yearField = new JTextField(15);
                JTextField popularityCountField = new JTextField(15);
                JTextField priceField = new JTextField(15);
                addBookPanel.add(new JLabel("Title:"));
                addBookPanel.add(titleField);
                addBookPanel.add(new JLabel("Author:"));
                addBookPanel.add(authorField);
                addBookPanel.add(new JLabel("Year:"));
                addBookPanel.add(yearField);
                addBookPanel.add(new JLabel("Popularity Count:"));
                addBookPanel.add(popularityCountField);
                addBookPanel.add(new JLabel("Price:"));
                addBookPanel.add(priceField);
                int result = JOptionPane.showConfirmDialog(null, addBookPanel, "Add Book", JOptionPane.OK_CANCEL_OPTION);
                System.out.println("Enter the content of this book");
                Scanner in= new Scanner(System.in);
                String content=in.nextLine();
                System.out.println("Content written successfully!");
                if (result == JOptionPane.OK_OPTION) {
                    String title = titleField.getText();
                    String author = authorField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    int popularityCount = Integer.parseInt(popularityCountField.getText());
                    int price = Integer.parseInt(priceField.getText());
                    // Create and add the book
                    int nextId = tableModel.getRowCount() + 1;
                    Book newBook = new Book(nextId, title, author, year, popularityCount, price,content);
                    library.addPublication(newBook);
                    // Update the table with the new book
                    tableModel.addRow(new Object[]{nextId, title, author, year, popularityCount, price});
                    // Write data to the file (similar to your writeDataToFile method)
                    writeDataToFile(newBook);
                }
            }
        });
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idToEdit = JOptionPane.showInputDialog("Enter the ID of the publication to edit:");
                int publicationId = Integer.parseInt(idToEdit);
                //System.out.println(publicationId);
                Publication publicationToEdit = library.getPublicationById(publicationId);
                //System.out.println(publicationId);
                if (publicationToEdit == null) {
                    JOptionPane.showMessageDialog(null, "Publication not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (publicationToEdit instanceof Book) {
                    // Create a panel to input updated details
                    JPanel editPublicationPanel = new JPanel(new GridLayout(5, 2));
                    JTextField titleField = new JTextField(publicationToEdit.getTitle());
                    JTextField authorField = new JTextField(((Book) publicationToEdit).getAuthor());
                    JTextField yearField = new JTextField(String.valueOf(((Book) publicationToEdit).getYear()));
                    JTextField popularityCountField = new JTextField(String.valueOf(((Book) publicationToEdit).getPopularityCount()));
                    JTextField priceField = new JTextField(String.valueOf(((Book) publicationToEdit).getPrice()));
                    editPublicationPanel.add(new JLabel("Title:"));
                    editPublicationPanel.add(titleField);
                    editPublicationPanel.add(new JLabel("Author:"));
                    editPublicationPanel.add(authorField);
                    editPublicationPanel.add(new JLabel("Year:"));
                    editPublicationPanel.add(yearField);
                    editPublicationPanel.add(new JLabel("Popularity Count:"));
                    editPublicationPanel.add(popularityCountField);
                    editPublicationPanel.add(new JLabel("Price:"));
                    editPublicationPanel.add(priceField);
                    int result = JOptionPane.showConfirmDialog(null, editPublicationPanel, "Edit Publication", JOptionPane.OK_CANCEL_OPTION);
                   //System.out.println(result);
                    if (result == JOptionPane.OK_OPTION) {
                        String updatedTitle = titleField.getText();
                        //System.out.println(updatedTitle);
                        String updatedAuthor = authorField.getText();
                        //System.out.println(updatedAuthor);
                        int updatedYear = Integer.parseInt(yearField.getText());
                        //System.out.println(updatedYear);
                        int updatedPopularityCount = Integer.parseInt(popularityCountField.getText());
                        //System.out.println(updatedPopularityCount);
                        int updatedPrice = Integer.parseInt(priceField.getText());
                        //System.out.println(updatedPrice);
                        // Update the book details
                        Book updatedBook = new Book(publicationId, updatedTitle, updatedAuthor, updatedYear, updatedPopularityCount, updatedPrice);
                        //System.out.println(updatedBook);
                        library.editPublication(publicationId, updatedBook);
                        // Update the data in the file
                        updateDataInFile(publicationId, updatedBook, updatedTitle);
                        // Update the display table
                        int rowIndex = table.convertRowIndexToModel(publicationId-1);
                        //System.out.println(rowIndex);
                        tableModel.setValueAt(updatedTitle, rowIndex, 1);
                        tableModel.setValueAt(updatedAuthor, rowIndex, 2);
                        tableModel.setValueAt(updatedYear, rowIndex, 3);
                        tableModel.setValueAt(updatedPopularityCount, rowIndex, 4);
                        tableModel.setValueAt(updatedPrice, rowIndex, 5);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display an input dialog to get the ID from the user
                String input = JOptionPane.showInputDialog("Enter ID to delete:");
                // Check if the user canceled the input or left it empty
                if (input == null || input.isEmpty()) {
                    return; // Exit the action if no input was provided
                }
                try {
                    int publicationIdToDelete = Integer.parseInt(input);
                    removeDataFromFile(publicationIdToDelete);
                    // Delete the publication with the specified ID
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int rowCount = model.getRowCount();
                    int selectedRow = -1;
                    for (int i = 0; i < rowCount; i++) {
                        if ((int) model.getValueAt(i, 0) == publicationIdToDelete) {
                            selectedRow = i;
                            model.removeRow(i);
                            break; // Assuming IDs are unique; stop searching once found
                        }
                    }
                    if (selectedRow != -1) {
                        JOptionPane.showMessageDialog(null, "Publication with ID " + publicationIdToDelete + " deleted successfully.");
                        reloadTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Publication with ID " + publicationIdToDelete + " not found.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid ID.");
                }
            }
        });
        viewPopularityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open a new screen to display the graphical representation
                displayPopularityChart();
            }
        });
        class ButtonEditor extends DefaultCellEditor {
            protected JButton button;
            private String label;
            private boolean isPushed;
            public ButtonEditor(JCheckBox checkBox) {
                super(checkBox);
                button = new JButton();
                button.setOpaque(true);
                button.addActionListener(e -> {
                    // Handle the "Read" button click here
                    int row = table.getSelectedRow();
                    String bookTitle = tableModel.getValueAt(row, 1).toString();
                    Map<String, String> resultMap = Book.getContent();
                    openBookContentWindow(bookTitle,resultMap);
                });
            }
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                if (isSelected) {
                    button.setForeground(table.getSelectionForeground());
                    button.setBackground(table.getSelectionBackground());
                } else {
                    button.setForeground(table.getForeground());
                    button.setBackground(table.getBackground());
                }
                label = (value == null) ? "Read" : value.toString();
                button.setText(label);
                isPushed = true;
                return button;
            }
            public Object getCellEditorValue() {
                if (isPushed) {
                }
                isPushed = false;
                return label;
            }
            public boolean stopCellEditing() {
                isPushed = false;
                return super.stopCellEditing();
            }
            protected void fireEditingStopped() {
                super.fireEditingStopped();
            }
        }
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewPopularityButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize(800, 400);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LibraryManagementSystem();
            }
        });
    }
    public void reloadTable(){
        main(null);
    }
    public static void openBookContentWindow(String bookTitle,Map<String, String> content)
 {
        JFrame bookContentFrame = new JFrame(bookTitle + " - Read Book");
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);
        textArea.setEditable(false); // Make the text area read-only
        //a book will have no content when not included in if statement but is in file
        String bookContent = null;
        // Create a JScrollPane for the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        //this is to test the scroll bar as large content is placed in this book
            if(bookTitle.equals("Agricultural Evolution"))
            {
                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        "Chapter 1: Introduction to Urban Agriculture\n" +
                        "\n" +
                        "Overview of urban agriculture\n" +
                        "Historical context and growth\n" +
                        "Importance in modern cities\n\n" +
                        "Chapter 2: Types of Urban Agriculture\n\n" +
                        "\n" +
                        "Community gardens\n" +
                        "Rooftop gardens\n" +
                        "Vertical farming\n" +
                        "Hydroponics and aquaponics\n" +
                        "Guerrilla gardening\n\n" +
                        "Chapter 3: Benefits of Urban Agriculture\n\n" +
                        "\n" +
                        "Access to fresh produce\n" +
                        "Economic opportunities\n" +
                        "Environmental benefits\n" +
                        "Social and community cohesion\n" +
                        "Educational value\n\n" +
                        "Chapter 4: Challenges and Barriers\n\n" +
                        "\n" +
                        "Space limitations\n" +
                        "Soil quality and contamination\n" +
                        "Zoning and legal issues\n" +
                        "Water and resource constraints\n" +
                        "Education and awareness\n\n" +
                        "Chapter 5: Case Studies\n\n" +
                        "\n" +
                        "Urban farming in New York City\n" +
                        "Vertical farming in Singapore\n" +
                        "Community gardens in Berlin\n" +
                        "Rooftop gardens in Tokyo\n" +
                        "Hydroponics in Los Angeles\n\n" +
                        "Chapter 6: Technological Innovations\n\n" +
                        "\n" +
                        "IoT in urban agriculture\n" +
                        "Automated vertical farming systems\n" +
                        "Sustainable practices\n" +
                        "Data-driven agriculture\n" +
                        "AI for crop management\n\n" +
                        "Chapter 7: Future Trends\n\n" +
                        "\n" +
                        "Urban agriculture in smart cities\n" +
                        "Integration with urban planning\n" +
                        "Expanding food sovereignty\n" +
                        "Reducing food miles\n" +
                        "Policy and government support\n\n" +
                        "Chapter 8: Sustainability and Environmental Impact\n\n" +
                        "\n" +
                        "Reducing food waste\n" +
                        "Carbon footprint reduction\n" +
                        "Biodiversity and urban ecosystems\n" +
                        "Sustainable agriculture practices\n\n" +
                        "Chapter 9: Community Engagement and Education\n\n" +
                        "\n" +
                        "Involving youth and schools\n" +
                        "Public awareness campaigns\n" +
                        "Workshops and training programs\n\n" +
                        "Chapter 10: Success Stories\n\n" +
                        "\n" +
                        "Individuals and communities making a difference\n" +
                        "Economic success stories\n" +
                        "Transforming neighborhoods";
            }
            //just a simple content
            else if (bookTitle.equals("novel title"))
            {
                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        "hello123456";
            }
            //just a simple content
            else if(bookTitle.equals("self book"))
            {
                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        "hello1234567890";
            }
            //Show the content entered by user when he adds a new book
            else{

                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        content.get(bookTitle);
            }
        textArea.setText(bookContent);
        bookContentFrame.add(scrollPane);
        bookContentFrame.pack();
        bookContentFrame.setVisible(true);
        bookContentFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        bookContentFrame, "Are you sure you want to close this book?", "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);

                if (confirm == JOptionPane.YES_OPTION) {
                    bookContentFrame.dispose();
                } else if (confirm == JOptionPane.NO_OPTION) {
                    Map<String, String> resultMap = Book.getContent();
                    openBookContentWindow(bookTitle,resultMap);
                    // Do nothing, bookContentFrame will remain open
                }
            }
        });
    }
    public void displayPopularityChart() {
        // Read popularity data from the file
        int[] popularityData = readPopularityDataFromFile(); // Implement this method to read data
        String[] bookData=readBookNamesFromFile();
        if (popularityData != null) {
            PopularityChartFrame frame = new PopularityChartFrame(popularityData,bookData);
            frame.setVisible(true);
        } else {
            // Handle the case where there is no data to display
            JOptionPane.showMessageDialog(null, "No popularity data available.");
        }
    }
    private int[] readPopularityDataFromFile() {
        String filePath = "data.txt"; // Replace with your actual file path
        List<Integer> popularityDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 6) {
                    int popularityCount = Integer.parseInt(parts[parts.length - 2]); // Assuming popularity count is in the second-to-last position
                    popularityDataList.add(popularityCount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Convert the list to an array
        int[] popularityData = new int[popularityDataList.size()];
        for (int i = 0; i < popularityDataList.size(); i++) {
            popularityData[i] = popularityDataList.get(i);
        }
        return popularityData;
    }
    private String[] readBookNamesFromFile() {
        String filePath = "data.txt"; // Replace with your actual file path
        List<String> bookNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 6) {
                    String bookName = parts[1]; // Assuming book name is in the second position
                    bookNames.add(bookName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Convert the list to a string array
        String[] bookNamesArray = bookNames.toArray(new String[0]);
        return bookNamesArray;
    }
    private static void writeDataToFile(Book book) {
        String filePath = "data.txt";
        try (FileWriter writer = new FileWriter(filePath, true); BufferedWriter bufferedWriter = new BufferedWriter(writer); PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            // Format the data as a comma-separated string
            String dataToWrite = String.format("%d, %s, %s, %d, %d, %d",
                    book.getId(), book.getTitle(), book.getAuthor(), book.getYear(), book.getPopularityCount(), book.getPrice());
            // Write the formatted data to the file
            printWriter.println(dataToWrite);
        } catch (IOException e) {
            e.printStackTrace();
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
                String author = parts[2];
                int year = Integer.parseInt(parts[3]);
                // Create and add the book to the library
                int nextId = library.getAllPublications().size() + 1;
                String content=null;
                Book book = new Book(nextId, title, author, year, popularityCount, price,content);
                library.addPublication(book);
                tableModel.addRow(new Object[]{id, title, author, year, popularityCount, price});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}