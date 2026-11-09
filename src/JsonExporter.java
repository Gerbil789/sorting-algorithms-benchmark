import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JsonExporter {
    private final List<String> jsonEntries = new ArrayList<>();

    public void addEntry(int size, String dataType, String inputType, String algorithm, double timeMs) {
        String jsonObject = String.format(Locale.US, 
            "  {\n" +
            "    \"size\": %d,\n" +
            "    \"dataType\": \"%s\",\n" +
            "    \"inputType\": \"%s\",\n" +
            "    \"algorithm\": \"%s\",\n" +
            "    \"timeMs\": %.4f\n" +
            "  }", 
            size, dataType, inputType, algorithm, timeMs
        );
        jsonEntries.add(jsonObject);
    }

    public void save(String filename) {
        System.out.println("\nSaving results to " + filename + "...");
        String jsonOutput = "[\n" + String.join(",\n", jsonEntries) + "\n]";
        try {
            Files.writeString(Paths.get(filename), jsonOutput);
            System.out.println("Data successfully saved!");
        } catch (IOException e) {
            System.err.println("Failed to write JSON file: " + e.getMessage());
        }
    }
}