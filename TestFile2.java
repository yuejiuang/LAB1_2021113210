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

class TestFile2 {

	// 用例1: word1 或 word2 不在图中
    @Test
    public void testQueryBridgeWords_NotInGraph() throws IOException {
    	String filePath = "no_words.txt";
        Files.write(Paths.get(filePath), "x y".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        
        String result = GraphUtils.queryBridgeWords(graph, "a","b");

        assertEquals("No a or b in the graph!", result);
    }

    // 用例2: word1 和 word2 在图中，存在桥接词
    @Test
    public void testQueryBridgeWords_WithBridge() throws IOException {
    	String filePath = "havebridge.txt";
        Files.write(Paths.get(filePath), "a c b".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        
        String result = GraphUtils.queryBridgeWords(graph, "a","b");

        assertEquals("The bridge words from a to b are: c.", result);
    }

    // 用例3: word1 和 word2 在图中，不存在桥接词
    @Test
    public void testQueryBridgeWords_NoBridge() throws IOException {
    	String filePath = "no_bridge.txt";
        Files.write(Paths.get(filePath), "a b c".getBytes());
        DirectedGraph graph = createGraphFromFile(filePath);
        Map<String, Map<String, Integer>> expected = new HashMap<>();
        
        String result = GraphUtils.queryBridgeWords(graph, "a","b");

        assertEquals("No bridge words from a to b!", result);
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

