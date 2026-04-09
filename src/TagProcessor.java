import java.io.*;
import java.util.*;

public class TagProcessor {

    private Set<String> stopWords = new HashSet<>();
    private Map<String, Integer> tagMap = new TreeMap<>();

    public void loadStopWords(File file) throws IOException {
        stopWords.clear();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String word = sc.nextLine().trim().toLowerCase();
                if (!word.isEmpty()) stopWords.add(word);
            }
        }
    }

    public void extractTags(File file) throws IOException {
        tagMap.clear();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext()) {
                String word = sc.next().replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!word.isEmpty() && !stopWords.contains(word)) {
                    tagMap.put(word, tagMap.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    public void saveTags(File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
                pw.printf("%-25s %d%n", entry.getKey(), entry.getValue());
            }
        }
    }

    public Map<String, Integer> getTagMap() { return tagMap; }
    public int getStopWordCount() { return stopWords.size(); }
    public boolean hasStopWords() { return !stopWords.isEmpty(); }
    public boolean hasTags() { return !tagMap.isEmpty(); }
}