package selab1;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestFile {

    @Test
    public void testEmptyFile() throws IOException {
        String filePath = "test_empty.txt";
        Files.write(Paths.get(filePath), "".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        assertTrue(graph.getAdjacencyList().isEmpty());
    }

    @Test
    public void testSingleWord() throws IOException {
        String filePath = "test_single_word.txt";
        Files.write(Paths.get(filePath), "Hello".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        expected.put("hello", new HashMap<>());
        assertEquals(expected, graph.getAdjacencyList());
    }

    @Test
    public void testTwoWords() throws IOException {
        String filePath = "test_two_words.txt";
        Files.write(Paths.get(filePath), "Hello World".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        Map<String, Integer> edges = new HashMap<>();
        edges.put("world", 1);
        expected.put("hello", edges);
        expected.put("world", new HashMap<>());
        assertEquals(expected, graph.getAdjacencyList());
    }

    @Test
    public void testMultipleWords() throws IOException {
        String filePath = "test_multiple_words.txt";
        Files.write(Paths.get(filePath), "Hello world hello".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        Map<String, Integer> edges1 = new HashMap<>();
        edges1.put("world", 1);
        expected.put("hello", edges1);

        Map<String, Integer> edges2 = new HashMap<>();
        edges2.put("hello", 1);
        expected.put("world", edges2);


        assertEquals(expected, graph.getAdjacencyList());
    }

    @Test
    public void testWithPunctuation() throws IOException {
        String filePath = "test_with_punctuation.txt";
        Files.write(Paths.get(filePath), "Hello, world! Hello.".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        Map<String, Integer> edges1 = new HashMap<>();
        edges1.put("world", 1);
        expected.put("hello", edges1);

        Map<String, Integer> edges2 = new HashMap<>();
        edges2.put("hello", 1);
        expected.put("world", edges2);


        assertEquals(expected, graph.getAdjacencyList());
    }

    @Test
    public void testCaseInsensitive() throws IOException {
        String filePath = "test_case_insensitive.txt";
        Files.write(Paths.get(filePath), "Hello hello HeLLo".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        Map<String, Integer> edges = new HashMap<>();
        edges.put("hello", 2);
        expected.put("hello", edges);
        assertEquals(expected, graph.getAdjacencyList());
        
    }
    @Test
    public void testFileNotFound() {
        String filePath = "nonexistent_file.txt";
        assertThrows(IOException.class, () -> {
            createGraphFromFile(filePath);
        });
    }

    // The function to be tested
    public static DirectedGraph createGraphFromFile(String filePath) throws IOException {
        DirectedGraph graph = new DirectedGraph();
        StringBuilder fileContent = new StringBuilder();

        // 读取整个文件内容到StringBuilder
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append(" ");
            }
        }

        // 将文件内容分割成单词数组
        String[] words = fileContent.toString().split("\\W+");

        // 遍历单词数组并添加边
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i].toLowerCase();
            String word2 = words[i + 1].toLowerCase();
            if (!word1.isEmpty() && !word2.isEmpty()) {
                graph.addEdge(word1, word2);
            }
        }
        // 处理最后一个单词，确保其存在于邻接表中
        if (words.length > 0) {
            String lastWord = words[words.length - 1].toLowerCase();
            if (!lastWord.isEmpty()) {
                graph.adjacencyList.putIfAbsent(lastWord, new HashMap<>());
            }
        }
        return graph;
    }
}

