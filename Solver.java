import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

public class Solver {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            File file = new File("dictionaries/scrabble.txt");
            Scanner fileScan = new Scanner(file);
            int numWords = 0;
            Trie trie = new Trie();
            while (fileScan.hasNextLine()) {
                String word = fileScan.nextLine().toLowerCase();
                numWords++;
                trie.insert(word);
            }
            System.out.println("Number of words in dictionary: " + numWords);
            Scanner s = new Scanner(System.in);
            int row = 1;
            ArrayList<ArrayList<String>> board = new ArrayList<>();
            Pattern onlyAlphabetical = Pattern.compile("[^a-zA-Z]");
            while (row <= 4) {
                System.out.println("Enter row #" + row + " of board:");
                boolean valid = false;
                while (!valid) {
                    String rowStr = s.nextLine().toLowerCase();
                    if (rowStr.length() != 4) {
                        System.out.println("Invalid input; did not enter 4 characters. Try again:");
                    } else if (onlyAlphabetical.matcher(rowStr).find()) {
                        System.out.println("Invalid input; only enter alphabetical characters. Try again:");
                    } 
                    else {
                        valid = true;
                        String[] letters = new String[]{rowStr.charAt(0) + "", rowStr.charAt(1) + "", rowStr.charAt(2) + "", rowStr.charAt(3) + ""};
                        board.add(new ArrayList<String>(Arrays.asList(letters)));
                    }
                }    
                row++;    
            }
            System.out.println("Solving for the following board:");
            for (ArrayList<String> a : board) {
                for (String str : a) {
                    System.out.print(str + " ");
                }
                System.out.println();
            }
            Game game = new Game(board, trie);
            ArrayList<String> words = new ArrayList<>(game.solve());
            System.out.println("--- SOLUTIONS (" + words.size() + ") ---");
            words.sort((s1, s2) -> s2.length() - s1.length());
            for (String word : words) {
                System.out.println(word);
            }
        } catch (IOException e) {
            System.out.println("Error with files.");
        }
    }
}

public class Trie {
    private class Node {
        Node[] children;
        boolean isWord;

        public Node() {
            children = new Node[26];
            isWord = false;
        }
    }

    Node root;

    public Trie() {
        root = new Node();
    }

    public void insert(String word) {
        Node head = root;
        for (int i = 0; i < word.length(); i++) {
            char cur = word.charAt(i);
            int charIdx = cur - 'a';
            if (head.children[charIdx] == null)
                head.children[charIdx] = new Node();
            head = head.children[charIdx];
        }
        head.isWord = true;
    } 

    public boolean search(String word) {
        Node head = root;
        for (int i = 0; i < word.length(); i++) {
            char cur = word.charAt(i);
            int charIdx = cur - 'a';
            if (head.children[charIdx] == null)
                return false;
            head = head.children[charIdx];
        }
        return head.isWord;
    }   

    public boolean startsWith(String prefix) {
        Node head = root;
        for (int i = 0; i < prefix.length(); i++) {
            char cur = prefix.charAt(i);
            int charIdx = cur - 'a';
            if (head.children[charIdx] == null)
                return false;
            head = head.children[charIdx];
        }
        return true;
    }
}

public class Game {

    ArrayList<ArrayList<String>> board;
    int maxScore;
    int[] score;
    boolean[][] visited;
    Trie trie;
    
    public Game(ArrayList<ArrayList<String>> b, Trie t) {
        board = b;
        trie = t;
        maxScore = 0;
        visited = new boolean[4][4];
        score = new int[]{0, 0, 0, 100, 400, 800, 1400, 1800, 2200, 2600, 3000, 3400, 3800, 4200, 4600, 5000, 5400};
    }

    // returns list of words and the associated max score
    public HashSet<String> solve() {
        HashSet<String> words = new HashSet<>();
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                dfs(i, j, "", words);
            }
        }
        System.out.println("--- MAXIMUM POSSIBLE SCORE: " + maxScore + " ---");
        return words;
    }

    public void dfs(int i, int j, String build, HashSet<String> words) {
        if (i < 0 || i >= 4 || j < 0 || j >= 4 || visited[i][j]) {
            return;
        } else {
            char cur = board.get(i).get(j).charAt(0);
            build += cur;
            if (!trie.startsWith(build)) {
                return;
            }
            visited[i][j] = true;
            dfs(i + 1, j, build, words);
            dfs(i + 1, j + 1, build, words);
            dfs(i - 1, j, build, words);
            dfs(i - 1, j - 1, build, words);
            dfs(i, j + 1, build, words);
            dfs(i, j - 1, build, words);
            dfs(i - 1, j + 1, build, words);
            dfs(i + 1, j - 1, build, words);
            visited[i][j] = false;
            if (build.length() >= 3) {
                if (trie.search(build)) {
                    if (!words.contains(build)) {
                        words.add(build);
                        maxScore += score[build.length()];
                    }
                }
            }
        }
    }
}