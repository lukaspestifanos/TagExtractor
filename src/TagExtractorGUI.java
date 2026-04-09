import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.Map;

public class TagExtractorGUI extends JFrame {

    private JLabel fileLabel;
    private JTextArea outputArea;
    private JButton pickTextFileBtn, pickStopWordsBtn, saveBtn;
    private TagProcessor processor = new TagProcessor();

    public TagExtractorGUI() {
        setTitle("Tag Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pickStopWordsBtn = new JButton("Pick Stop Words File");
        pickTextFileBtn = new JButton("Pick Text File");
        saveBtn = new JButton("Save Tags");
        saveBtn.setEnabled(false);
        topPanel.add(pickStopWordsBtn);
        topPanel.add(pickTextFileBtn);
        topPanel.add(saveBtn);
        add(topPanel, BorderLayout.NORTH);

        fileLabel = new JLabel("No file selected.");
        fileLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        add(fileLabel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        pickStopWordsBtn.addActionListener(e -> loadStopWords());
        pickTextFileBtn.addActionListener(e -> loadTextFile());
        saveBtn.addActionListener(e -> saveTags());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadStopWords() {
        File file = chooseFile("Pick Stop Words File");
        if (file == null) return;
        try {
            processor.loadStopWords(file);
            JOptionPane.showMessageDialog(this,
                    "Loaded " + processor.getStopWordCount() + " stop words from: " + file.getName());
        } catch (IOException ex) {
            showError("Error reading stop words file: " + ex.getMessage());
        }
    }

    private void loadTextFile() {
        if (!processor.hasStopWords()) {
            JOptionPane.showMessageDialog(this, "Please load a stop words file first.");
            return;
        }
        File file = chooseFile("Pick Text File");
        if (file == null) return;
        fileLabel.setText("Extracting tags from: " + file.getName());
        try {
            processor.extractTags(file);
            displayTags();
            saveBtn.setEnabled(true);
        } catch (IOException ex) {
            showError("Error reading text file: " + ex.getMessage());
        }
    }

    private void displayTags() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : processor.getTagMap().entrySet()) {
            sb.append(String.format("%-25s %d%n", entry.getKey(), entry.getValue()));
        }
        outputArea.setText(sb.toString());
    }

    private void saveTags() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("tags_output.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                processor.saveTags(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Tags saved to: " + chooser.getSelectedFile().getName());
            } catch (IOException ex) {
                showError("Error saving file: " + ex.getMessage());
            }
        }
    }

    private File chooseFile(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        return chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}