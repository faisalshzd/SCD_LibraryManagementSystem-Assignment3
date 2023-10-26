import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
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
}
class Library {
    private List<Publication> publications = new ArrayList<>();
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
        // Create buttons for Add, Edit, Delete
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
                if (result == JOptionPane.OK_OPTION) {
                    String title = titleField.getText();
                    String author = authorField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    int popularityCount = Integer.parseInt(popularityCountField.getText());
                    int price = Integer.parseInt(priceField.getText());

                    // Create and add the book
                    int nextId = tableModel.getRowCount() + 1;
                    Book newBook = new Book(nextId, title, author, year, popularityCount, price);
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
                Publication publicationToEdit = library.getPublicationById(publicationId);
                if (publicationToEdit == null) {
                    JOptionPane.showMessageDialog(null, "Publication not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
                if (result == JOptionPane.OK_OPTION) {
                    String updatedTitle = titleField.getText();
                    String updatedAuthor = authorField.getText();
                    int updatedYear = Integer.parseInt(yearField.getText());
                    int updatedPopularityCount = Integer.parseInt(popularityCountField.getText());
                    int updatedPrice = Integer.parseInt(priceField.getText());
                    // Update the book details
                    Book updatedBook = new Book(publicationId, updatedTitle, updatedAuthor, updatedYear, updatedPopularityCount, updatedPrice);
                    library.editPublication(publicationId, updatedBook);
                    // Update the data in the file
                    updateDataInFile(publicationId, updatedBook, updatedTitle);
                    // Update the display table
                    int rowIndex = table.convertRowIndexToModel(table.getSelectedRow());
                    tableModel.setValueAt(updatedTitle, rowIndex, 1);
                    tableModel.setValueAt(updatedAuthor, rowIndex, 2);
                    tableModel.setValueAt(updatedYear, rowIndex, 3);
                    tableModel.setValueAt(updatedPopularityCount, rowIndex, 4);
                    tableModel.setValueAt(updatedPrice, rowIndex, 5);
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
                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(null, "Publication with ID " + publicationIdToDelete + " deleted successfully.");
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
                    openBookContentWindow(bookTitle);
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
                    // Perform the "Read" button action, if needed
                    // Note: The action is handled in the actionPerformed method.
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
        // Add the "Read" button column to the table
        JButton readButton = new JButton("Read");
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
    public static void openBookContentWindow(String bookTitle) {
        JFrame bookContentFrame = new JFrame(bookTitle + " - Read Book");
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);
        textArea.setEditable(false); // Make the text area read-only
        String bookContent = "no content";
        // Create a JScrollPane for the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
            if(bookTitle.equals("book title"))
            {
                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        "hello123";
            }
            else if (bookTitle.equals("novel title"))
            {
                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        "hello123456";
            }
            else if(bookTitle.equals("self book"))
            {
                bookContent = "This is the content of the book titled '" + bookTitle + "'.\n\n" +
                        "hello1234567890";
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
                    openBookContentWindow(bookTitle);
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
                Book book = new Book(nextId, title, author, year, popularityCount, price);
                library.addPublication(book);
                tableModel.addRow(new Object[]{id, title, author, year, popularityCount, price});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}