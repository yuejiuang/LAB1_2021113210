import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextGraph {
    public static void main(String[] args) {
        // 创建一个扫描器对象，用于从控制台读取输入
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入文本文件路径：");
        // 读取用户输入的文件路径
        String filePath = scanner.nextLine();

        try {
            // 从指定文件路径创建一个有向图
            DirectedGraph graph = GraphUtils.createGraphFromFile(filePath);            

            // 布尔变量，用于控制主循环是否退出
            boolean exit = false;
            while (!exit) {
                // 显示菜单选项
                System.out.println("请选择功能：1-展示有向图内容 2-查询桥接词 3-生成新文本 4-计算最短路径 5-随机游走 6-退出");
                int choice = scanner.nextInt();
                scanner.nextLine();  // 读取换行符

                // 根据用户的选择执行相应的功能
                switch (choice) {
                    case 1:
                        // 显示有向图的内容
                        GraphUtils.showDirectedGraph(graph);
                        break;
                    case 2:
                        // 查询桥接词
                        System.out.println("请输入两个单词：");
                        String word1 = scanner.next();
                        String word2 = scanner.next();
                        System.out.println(GraphUtils.queryBridgeWords(graph, word1, word2));
                        break;
                    case 3:
                        // 生成新文本
                        System.out.println("请输入新文本：");
                        String newText = scanner.nextLine();
                        System.out.println(GraphUtils.generateNewText(graph, newText));
                        break;
                    case 4:
                        // 计算最短路径
                        System.out.println("请输入两个单词：");
                        String start = scanner.next();
                        String end = scanner.next();
                        System.out.println(GraphUtils.calcShortestPath(graph, start, end));
                        break;
                    case 5:
                        // 随机游走
                        System.out.println(GraphUtils.randomWalk(graph));
                        break;
                    case 6:
                        // 退出程序
                        exit = true;
                        break;
                    default:
                        // 处理无效输入
                        System.out.println("无效选择，请重新选择。");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 表示一个有向图的类
class DirectedGraph {
    // 使用邻接表表示有向图
    // 从源节点到目标节点及其边权重的映射
    Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();

    // 添加一条边到图中
    public void addEdge(String source, String destination) {
        // 确保源节点在邻接表中存在
        adjacencyList.putIfAbsent(source, new HashMap<>());
        // 获取源节点的边集合
        Map<String, Integer> edges = adjacencyList.get(source);
        // 增加目标节点的边权重，如果不存在则设置为0
        edges.put(destination, edges.getOrDefault(destination, 0) + 1);
    }

    // 获取邻接表
    public Map<String, Map<String, Integer>> getAdjacencyList() {
        return adjacencyList;
    }
}

// 用于图操作的工具类
class GraphUtils {
    static AtomicBoolean stopFlag = new AtomicBoolean(false);

    // 从文件创建一个有向图
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

    // 显示有向图
    public static void showDirectedGraph(DirectedGraph graph) {
        System.out.println("有向图内容：");
        // 遍历并输出邻接表中的内容
        for (Map.Entry<String, Map<String, Integer>> entry : graph.getAdjacencyList().entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    // 查询两个单词之间的桥接词
    public static String queryBridgeWords(DirectedGraph graph, String word1, String word2) {
        // 检查两个单词是否存在于图中
        if (!graph.getAdjacencyList().containsKey(word1) || !graph.getAdjacencyList().containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        // 查找从word1到word2的桥接词
        Set<String> bridges = new HashSet<>();
        for (String middle : graph.getAdjacencyList().get(word1).keySet()) {
            if (graph.getAdjacencyList().get(middle).containsKey(word2)) {
                bridges.add(middle);
            }
        }

        // 返回结果
        if (bridges.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridges) + ".";
        }
    }

    // 生成包含桥接词的新文本
    public static String generateNewText(DirectedGraph graph, String inputText) {
        String[] words = inputText.split("\\W+");
        StringBuilder newText = new StringBuilder();

        // 遍历单词并在其间插入桥接词
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i].toLowerCase();
            String word2 = words[i + 1].toLowerCase();
            newText.append(words[i]).append(" ");

            Set<String> bridges = new HashSet<>();
            Map<String, Integer> edges = graph.getAdjacencyList().get(word1);
            if (edges != null) {
                for (String middle : edges.keySet()) {
                    Map<String, Integer> middleEdges = graph.getAdjacencyList().get(middle);
                    if (middleEdges != null && middleEdges.containsKey(word2)) {
                        bridges.add(middle);
                    }
                }
            }

            // 随机选择一个桥接词插入到新文本中
            if (!bridges.isEmpty()) {
                String[] bridgeArray = bridges.toArray(new String[0]);
                String bridge = bridgeArray[new Random().nextInt(bridgeArray.length)];
                newText.append(bridge).append(" ");
            }
        }
        // 添加最后一个单词
        newText.append(words[words.length - 1]);

        return newText.toString();
    }

    // 计算两个单词之间的最短路径，Dijkstra算法
    public static String calcShortestPath(DirectedGraph graph, String start, String end) {
        // 检查起点和终点是否存在于图中
        if (!graph.getAdjacencyList().containsKey(start) || !graph.getAdjacencyList().containsKey(end)) {
            return "No " + start + " or " + end + " in the graph!";
        }

        // 初始化Dijkstra算法所需的数据结构
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // 设置初始距离
        for (String node : graph.getAdjacencyList().keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(start, 0);
        queue.add(start);

        // Dijkstra算法
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                break;
            }

            for (Map.Entry<String, Integer> neighbor : graph.getAdjacencyList().get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    queue.add(neighbor.getKey());
                }
            }
        }

        // 构建最短路径
        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        // 检查是否存在路径
        if (path.size() == 1 && !path.get(0).equals(start)) {
            return "No path from " + start + " to " + end + "!";
        }

        return "The shortest path from " + start + " to " + end + " is: " + String.join(" -> ", path) +
                " with length " + distances.get(end) + ".";
    }

    // 执行随机游走
    public static String randomWalk(DirectedGraph graph) throws IOException {
        Scanner scanner = new Scanner(System.in);
        List<String> nodes = new ArrayList<>(graph.getAdjacencyList().keySet());
        Random random = new Random();
        String current = nodes.get(random.nextInt(nodes.size()));
        StringBuilder walk = new StringBuilder(current);

        Set<String> visitedEdges = new HashSet<>();
        boolean done = false;

        // 随机游走
        while (!done) {
            System.out.println("当前节点: " + current + ". 继续遍历按'c'，停止遍历按's'：");
            String input = scanner.nextLine();
            if ("s".equalsIgnoreCase(input)) {
                break;
            }

            Map<String, Integer> edges = graph.getAdjacencyList().get(current);
            if (edges == null || edges.isEmpty()) {
                System.out.println("没有出边，遍历结束。");
                break;
            }

            List<String> possibleNextNodes = new ArrayList<>();
            for (String next : edges.keySet()) {
                String edge = current + "->" + next;
                if (!visitedEdges.contains(edge)) {
                    possibleNextNodes.add(next);
                }
            }

            if (possibleNextNodes.isEmpty()) {
                System.out.println("没有新的节点可以访问，或已经访问过所有可能的边，遍历结束。");
                break;
            }

            String nextNode = possibleNextNodes.get(random.nextInt(possibleNextNodes.size()));
            visitedEdges.add(current + "->" + nextNode);
            current = nextNode;
            walk.append(" -> ").append(current);
        }

        // 将随机游走的结果保存到文件
        saveWalkToFile(walk.toString());
        return "Random walk: " + walk.toString();
    }

    // 保存随机游走的结果到文件
    private static void saveWalkToFile(String walk) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("random_walk_output.txt"))) {
            writer.write(walk);
        }
    }
}
